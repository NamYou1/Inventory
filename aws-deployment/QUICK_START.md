# AWS Quick Start Guide

## 🚀 Fastest Path to Cloud (5 minutes)

### Prerequisites
```bash
# Install AWS CLI
# Windows: https://awscli.amazonaws.com/AWSCLIV2.msi
# Mac: brew install awscli
# Linux: pip install awscli

# Install Docker
# Download: https://www.docker.com/products/docker-desktop
```

### Step 1: Configure AWS (1 minute)
```bash
# Get your AWS Account ID
aws configure

# Enter:
# AWS Access Key ID: [Get from AWS IAM Console]
# AWS Secret Access Key: [Get from AWS IAM Console]
# Default region name: us-east-1
# Default output format: json

# Verify
aws sts get-caller-identity
```

### Step 2: Deploy with One Command (4 minutes)
```bash
cd D:\SprngBoot\Inventory

# Make the deploy script executable
chmod +x aws-deployment/deploy.sh

# Run deployment (choose your environment)
export AWS_REGION=us-east-1
export ENVIRONMENT=prod
./aws-deployment/deploy.sh
```

The script will:
1. ✅ Create ECR repositories
2. ✅ Build Docker images
3. ✅ Push to AWS
4. ✅ Create database (RDS PostgreSQL)
5. ✅ Create cache (Redis)
6. ✅ Create load balancer
7. ✅ Deploy application on ECS

---

## 📊 What Gets Deployed

```
┌─────────────────────────────────┐
│   Application Load Balancer     │
│   (Public IP address here)      │
└──────────────┬──────────────────┘
               │
    ┌──────────┴──────────┐
    │                     │
┌───▼────┐           ┌────▼───┐
│  ECS   │ ◄────────►│   ECS   │
│ Task 1 │  Network  │ Task 2  │
└───┬────┘           └────┬───┘
    │                     │
    └──────────┬──────────┘
               │
    ┌──────────┼──────────┐
    │          │          │
┌───▼──┐  ┌────▼────┐ ┌──▼───┐
│  RDS │  │  Redis  │ │  S3  │
│  DB  │  │ Cache   │ │Store │
└──────┘  └─────────┘ └──────┘
```

---

## 📍 Access Your Application

After deployment completes, you'll get:

```bash
# Application URL (from ALB DNS)
http://[ALB-DNS-NAME]

# API Docs
http://[ALB-DNS-NAME]/swagger-ui.html

# Health Check
http://[ALB-DNS-NAME]/actuator/health

# API Endpoints
http://[ALB-DNS-NAME]/api/v1/...
```

---

## 💰 Cost Estimate

**Monthly Cost**: ~$94
- ECS Tasks: $23
- RDS Database: $30
- Redis Cache: $20
- Load Balancer: $16
- Data Transfer: $5

---

## 🔧 Manual Deployment (If Script Fails)

### Step 1: ECR Authentication
```bash
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin $(aws sts get-caller-identity --query Account --output text).dkr.ecr.us-east-1.amazonaws.com
```

### Step 2: Create Repositories
```bash
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
aws ecr create-repository --repository-name inventory-app --region us-east-1
aws ecr create-repository --repository-name inventory-nginx --region us-east-1
```

### Step 3: Build & Push Images
```bash
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
REGION=us-east-1

# App image
docker build -t inventory-app:latest .
docker tag inventory-app:latest $ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/inventory-app:latest
docker push $ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/inventory-app:latest

# Nginx image
docker build -t inventory-nginx:latest -f aws-deployment/nginx.Dockerfile ./nginx
docker tag inventory-nginx:latest $ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/inventory-nginx:latest
docker push $ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/inventory-nginx:latest
```

### Step 4: Deploy Infrastructure
```bash
# Update image URIs in task definition
sed -i "s/YOUR_AWS_ACCOUNT_ID/$ACCOUNT_ID/g" aws-deployment/ecs-task-definition.json

# Deploy CloudFormation
aws cloudformation deploy \
  --template-file aws-deployment/cloudformation-template.yaml \
  --stack-name inventory-stack-prod \
  --parameter-overrides \
    EnvironmentName=prod \
    DBUsername=postgres \
    DBPassword=YourSecureDB123! \
    RedisPassword=YourRedis123! \
  --capabilities CAPABILITY_NAMED_IAM \
  --region us-east-1
```

### Step 5: Check Status
```bash
# View stack outputs
aws cloudformation describe-stacks \
  --stack-name inventory-stack-prod \
  --query 'Stacks[0].Outputs' \
  --output table \
  --region us-east-1

# Monitor ECS service
aws ecs describe-services \
  --cluster inventory-cluster-prod \
  --services inventory-service-prod \
  --region us-east-1
```

---

## 🔍 Troubleshooting

### Problem: Docker login fails
```bash
# Solution: Get fresh credentials
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com
```

### Problem: CloudFormation fails
```bash
# Delete and retry
aws cloudformation delete-stack --stack-name inventory-stack-prod
aws cloudformation wait stack-delete-complete --stack-name inventory-stack-prod
# Then run deploy again
```

### Problem: ECS tasks keep dying
```bash
# View logs
aws logs tail /ecs/inventory-app-prod --follow

# Common issues:
# - DB connection string incorrect
# - Database not accessible
# - Insufficient memory (increase in task definition)
```

### Problem: Application not reachable
```bash
# Check ALB health
aws elbv2 describe-target-health \
  --target-group-arn $(aws elbv2 describe-target-groups \
    --query 'TargetGroups[0].TargetGroupArn' --output text) \
  --region us-east-1

# Check security groups
aws ec2 describe-security-groups --region us-east-1
```

---

## 📝 Important Files

- **AWS_DEPLOYMENT_GUIDE.md** - Complete architecture & options
- **DEPLOYMENT_CHECKLIST.md** - Step-by-step verification
- **deploy.sh** - Automated deployment script
- **ecs-task-definition.json** - ECS task configuration
- **cloudformation-template.yaml** - Infrastructure as Code
- **nginx.conf** - Nginx configuration
- **application-prod.yml** - Production Spring Boot config

---

## 🛣️ Common Next Steps

1. **Setup DNS**
   ```bash
   # Create Route 53 record pointing to ALB DNS
   # Example: api.yourdomain.com → ALB DNS
   ```

2. **Enable HTTPS**
   ```bash
   # Add ACM certificate to ALB
   # Update nginx.conf with SSL settings
   ```

3. **Setup CI/CD**
   ```bash
   # Create CodePipeline for automatic deployments
   # Connect GitHub/CodeCommit repository
   ```

4. **Monitor Application**
   ```bash
   # View CloudWatch dashboards
   # Setup SNS alerts
   # View application logs
   ```

5. **Backup Database**
   ```bash
   # Enable automated backups (already enabled)
   # Create manual snapshots before major changes
   ```

---

## 📞 Getting Help

- **AWS Support**: https://console.aws.amazon.com/support/
- **AWS Documentation**: https://docs.aws.amazon.com/
- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **Docker Documentation**: https://docs.docker.com/

---

**Last Updated**: June 4, 2026

