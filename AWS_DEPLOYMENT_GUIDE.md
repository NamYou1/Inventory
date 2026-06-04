# AWS Deployment Guide for Inventory Management System

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Prerequisites](#prerequisites)
3. [Deployment Options](#deployment-options)
4. [Option 1: Amazon ECS (Recommended)](#option-1-amazon-ecs-recommended)
5. [Option 2: Elastic Beanstalk](#option-2-elastic-beanstalk)
6. [Option 3: EC2 with Docker](#option-3-ec2-with-docker)
7. [Option 4: AWS AppRunner](#option-4-aws-apprunner)
8. [Database Setup](#database-setup)
9. [Monitoring & Logging](#monitoring--logging)
10. [Cost Estimation](#cost-estimation)

---

## Architecture Overview

### Current Local Setup:
```
┌─────────────────────────────────────────┐
│      Nginx (Reverse Proxy)              │
│      Port: 80                           │
└──────────┬──────────────────────────────┘
           │
    ┌──────┴──────┐
    │             │
┌───▼────┐    ┌───▼────────┐
│  App   │    │  RustFS    │
│ :8080  │    │  S3-compat │
└───┬────┘    └────────────┘
    │
    ├─► PostgreSQL (:5432)
    ├─► Redis (:6379)
    └─► S3 Storage
```

### AWS Target Architecture:
```
┌────────────────────────────────────────────────┐
│             AWS Region (us-east-1)             │
├────────────────────────────────────────────────┤
│                                                │
│  ┌──────────────────────────────────────────┐ │
│  │     Application Load Balancer (ALB)      │ │
│  │            Port: 80/443                  │ │
│  └──────────────────────────────────────────┘ │
│                    │                          │
│  ┌─────────────────┴──────────────────────┐  │
│  │                                        │  │
│  ├─► ECS Cluster (Fargate/EC2)           │  │
│  │    ├─ Inventory App Task              │  │
│  │    ├─ Nginx Task                      │  │
│  │    └─ Scaling: 2-4 tasks              │  │
│  │                                       │  │
│  ├─► RDS PostgreSQL (Multi-AZ)          │  │
│  │    ├─ Auto-backup enabled             │  │
│  │    └─ Version: 16-alpine              │  │
│  │                                       │  │
│  ├─► ElastiCache Redis (Multi-AZ)       │  │
│  │    └─ Version: 7-alpine               │  │
│  │                                       │  │
│  ├─► S3 Bucket (File Storage)           │  │
│  │    ├─ Versioning enabled              │  │
│  │    └─ Lifecycle rules                 │  │
│  │                                       │  │
│  └─────────────────────────────────────  │  │
│                                          │  │
│  ┌──────────────────────────────────────┐  │
│  │  CloudWatch (Logs & Monitoring)      │  │
│  └──────────────────────────────────────┘  │
│                                             │
└─────────────────────────────────────────────┘
```

---

## Prerequisites

### AWS Account Setup:
```bash
# 1. Create AWS Account: https://aws.amazon.com

# 2. Install AWS CLI
# Windows: https://awscli.amazonaws.com/AWSCLIV2.msi
# Linux/Mac: brew install awscli

# 3. Configure AWS Credentials
aws configure
# Enter:
# - AWS Access Key ID
# - AWS Secret Access Key
# - Default region: us-east-1
# - Output format: json

# 4. Verify setup
aws sts get-caller-identity
```

### Tools Required:
- AWS CLI v2
- Docker & Docker Desktop
- AWS ECR CLI (included in AWS CLI)

---

## Deployment Options

| Option | Complexity | Cost | Best For |
|--------|-----------|------|----------|
| **ECS Fargate** | Medium | $$ | Serverless containers, auto-scaling |
| **Elastic Beanstalk** | Low | $$ | Quick deployment, managed |
| **EC2 + Docker Swarm** | High | $ | Complete control, lower cost |
| **AppRunner** | Very Low | $ | Simplest, code-to-cloud |

### Recommended: **ECS Fargate**
- ✅ Serverless container management
- ✅ Auto-scaling support
- ✅ No server management
- ✅ Pay only for resources used
- ✅ Multi-AZ by default

---

## Option 1: Amazon ECS (Recommended)

### Step 1: Create ECR Repository

```bash
# Login to AWS ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <YOUR_AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com

# Create repository
aws ecr create-repository --repository-name inventory-app --region us-east-1
aws ecr create-repository --repository-name inventory-nginx --region us-east-1
```

### Step 2: Build & Push Docker Images

```bash
# Build Inventory App
docker build -t inventory-app:latest .
docker tag inventory-app:latest <YOUR_AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/inventory-app:latest
docker push <YOUR_AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/inventory-app:latest

# Build Nginx (from ./nginx/Dockerfile)
docker build -t inventory-nginx:latest -f nginx/Dockerfile ./nginx
docker tag inventory-nginx:latest <YOUR_AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/inventory-nginx:latest
docker push <YOUR_AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/inventory-nginx:latest
```

### Step 3: Create RDS PostgreSQL Database

```bash
# Via AWS Console or CLI:
aws rds create-db-instance \
  --db-instance-identifier inventory-postgres \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --engine-version 16.1 \
  --allocated-storage 20 \
  --storage-type gp2 \
  --master-username postgres \
  --master-user-password "YourSecurePassword123!" \
  --vpc-security-group-ids sg-xxxxx \
  --multi-az \
  --backup-retention-period 7 \
  --region us-east-1
```

### Step 4: Create ElastiCache Redis

```bash
aws elasticache create-cache-cluster \
  --cache-cluster-id inventory-redis \
  --engine redis \
  --cache-node-type cache.t3.micro \
  --engine-version 7.0 \
  --num-cache-nodes 1 \
  --region us-east-1
```

### Step 5: Create ECS Cluster

```bash
# Create cluster
aws ecs create-cluster --cluster-name inventory-cluster --region us-east-1

# Create task definition (see ecs-task-definition.json)
aws ecs register-task-definition \
  --cli-input-json file://ecs-task-definition.json \
  --region us-east-1

# Create service
aws ecs create-service \
  --cluster inventory-cluster \
  --service-name inventory-service \
  --task-definition inventory-task:1 \
  --desired-count 2 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-xxxxx,subnet-xxxxx],securityGroups=[sg-xxxxx],assignPublicIp=ENABLED}" \
  --load-balancers targetGroupArn=arn:aws:elasticloadbalancing:...
```

### Step 6: Create Application Load Balancer

```bash
# Create target group
aws elbv2 create-target-group \
  --name inventory-targets \
  --protocol HTTP \
  --port 8080 \
  --vpc-id vpc-xxxxx

# Create ALB
aws elbv2 create-load-balancer \
  --name inventory-alb \
  --subnets subnet-xxxxx subnet-xxxxx \
  --security-groups sg-xxxxx
```

---

## Option 2: Elastic Beanstalk

### Quick Deployment:

```bash
# Install EB CLI
pip install awsebcli

# Initialize Elastic Beanstalk
eb init -p "Docker running on 64bit Amazon Linux 2" inventory-app --region us-east-1

# Create environment
eb create inventory-prod --instance-type t3.small

# Deploy
eb deploy

# Monitor
eb status
eb logs
```

---

## Option 3: EC2 with Docker

### Step 1: Launch EC2 Instance

```bash
# Launch Ubuntu 22.04 LTS t3.medium instance
aws ec2 run-instances \
  --image-id ami-0c55b159cbfafe1f0 \
  --instance-type t3.medium \
  --key-name your-key-pair \
  --security-groups default
```

### Step 2: Connect & Install Docker

```bash
# SSH into instance
ssh -i your-key.pem ec2-user@your-instance-ip

# Install Docker
sudo yum update -y
sudo yum install docker -y
sudo systemctl start docker
sudo usermod -a -G docker ec2-user

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.18.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### Step 3: Deploy Application

```bash
# Clone your repository
git clone your-repo.git
cd Inventory

# Create .env file with AWS resources endpoints
cat > .env << EOF
DB_URL=jdbc:postgresql://your-rds-endpoint:5432/kk_db
DB_USERNAME=postgres
DB_PASSWORD=YourPassword123!
REDIS_HOST=your-redis-endpoint
REDIS_PORT=6379
REDIS_PASSWORD=YourRedisPassword!
JWT_SECRET_BASE64=...
AWS_REGION=us-east-1
EOF

# Pull images from ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin your-account.dkr.ecr.us-east-1.amazonaws.com

# Run with docker-compose
docker-compose up -d
```

---

## Option 4: AWS AppRunner

### Ultra-Simple Deployment:

```bash
# 1. Push code to GitHub/CodeCommit

# 2. Connect AppRunner to repository
aws apprunner create-service \
  --service-name inventory-app \
  --source-configuration repositoryType=GITHUB,imageRepository.imageIdentifier=your-account.dkr.ecr.us-east-1.amazonaws.com/inventory-app

# 3. Done! Auto-scaled and managed by AWS
```

---

## Database Setup

### 1. Create RDS Database

```bash
# High-availability setup
aws rds create-db-instance \
  --db-instance-identifier inventory-db-prod \
  --db-instance-class db.t3.small \
  --engine postgres \
  --engine-version 16.1 \
  --allocated-storage 100 \
  --storage-type gp3 \
  --storage-encrypted \
  --master-username postgres \
  --master-user-password "GenerateStrongPassword123!" \
  --backup-retention-period 30 \
  --multi-az \
  --publicly-accessible false \
  --enable-cloudwatch-logs-exports postgresql \
  --enable-iam-database-authentication
```

### 2. Initialize Database (Flyway)

Flyway migrations will run automatically when app starts.

### 3. Backup & Recovery

```bash
# Create manual backup
aws rds create-db-snapshot \
  --db-instance-identifier inventory-db-prod \
  --db-snapshot-identifier inventory-db-backup-$(date +%Y%m%d)

# View backups
aws rds describe-db-snapshots

# Restore from snapshot (if needed)
aws rds restore-db-instance-from-db-snapshot \
  --db-instance-identifier inventory-db-restored \
  --db-snapshot-identifier inventory-db-backup-20240604
```

---

## Monitoring & Logging

### 1. CloudWatch Logs

```bash
# View application logs
aws logs tail /ecs/inventory-task --follow

# Create log group
aws logs create-log-group --log-group-name /ecs/inventory-prod
```

### 2. CloudFormation Template

See `cloudformation-template.yaml` for infrastructure-as-code deployment.

### 3. Monitoring Alerts

```bash
# Create alarm for high CPU
aws cloudwatch put-metric-alarm \
  --alarm-name inventory-high-cpu \
  --alarm-description "Alert when CPU exceeds 80%" \
  --metric-name CPUUtilization \
  --namespace AWS/ECS \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold
```

---

## Cost Estimation (Monthly)

### ECS Fargate Option:
```
- Task 0.25 CPU: $0.0254/hour  = ~$19/month
- Task Memory 512MB: $0.0054/hour = ~$4/month
- RDS db.t3.micro: ~$30/month
- ElastiCache t3.micro: ~$20/month
- Load Balancer: ~$16/month
- Data Transfer: ~$5/month

Total: ~$94/month
```

### Elastic Beanstalk Option:
```
- EC2 t3.small: ~$25/month
- RDS db.t3.micro: ~$30/month
- ElastiCache t3.micro: ~$20/month
- Load Balancer: ~$16/month

Total: ~$91/month
```

---

## Quick Deployment Checklist

- [ ] AWS Account created
- [ ] AWS CLI configured
- [ ] Docker images built & pushed to ECR
- [ ] RDS PostgreSQL created
- [ ] ElastiCache Redis created (optional)
- [ ] ECS Cluster & Task Definition created
- [ ] Load Balancer configured
- [ ] Security Groups configured
- [ ] Environment variables set
- [ ] Database migrations ran (Flyway)
- [ ] Health checks passing
- [ ] Monitoring enabled

---

## Rollback Procedure

```bash
# Revert to previous ECS task definition
aws ecs update-service \
  --cluster inventory-cluster \
  --service inventory-service \
  --task-definition inventory-task:2

# Monitor rollback
aws ecs describe-services \
  --cluster inventory-cluster \
  --services inventory-service
```

---

## Best Practices

1. **Security**
   - Use AWS Secrets Manager for sensitive data
   - Enable VPC endpoints for private access
   - Use IAM roles instead of access keys
   - Enable encryption for RDS & ElastiCache

2. **Performance**
   - Use RDS Multi-AZ for high availability
   - Enable Read Replicas for reporting
   - Use CloudFront for static content
   - Enable connection pooling

3. **Cost**
   - Use Reserved Instances for long-term
   - Set up auto-scaling policies
   - Use S3 Intelligent-Tiering
   - Monitor unused resources

---

## Support & Resources

- AWS Well-Architected Framework: https://aws.amazon.com/architecture/well-architected/
- ECS Best Practices: https://docs.aws.amazon.com/AmazonECS/latest/developerguide/
- RDS Best Practices: https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/
- Pricing Calculator: https://calculator.aws/


