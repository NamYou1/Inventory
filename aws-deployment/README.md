# AWS Deployment Folder

Complete AWS deployment setup for the Inventory Management System.

## 📁 Files Overview

### Documentation
- **QUICK_START.md** - 5-minute deployment guide (START HERE!)
- **DEPLOYMENT_CHECKLIST.md** - Step-by-step verification checklist

### Infrastructure as Code
- **cloudformation-template.yaml** - Complete AWS infrastructure definition
  - VPC & Networking
  - RDS PostgreSQL
  - ElastiCache Redis
  - ECS Cluster & Service
  - Application Load Balancer
  - CloudWatch Logs
  - IAM Roles & Policies
  - S3 Bucket

### Configuration Files
- **ecs-task-definition.json** - ECS task configuration
  - Container definitions
  - Environment variables
  - Health checks
  - Logging configuration
  - Resource limits

- **application-prod.yml** - Spring Boot production configuration
  - Database connection pooling
  - Redis cache settings
  - Security configuration
  - Logging setup
  - Metrics & monitoring

### Nginx Reverse Proxy
- **nginx.Dockerfile** - Nginx Docker image definition
- **nginx.conf** - Nginx configuration
  - Reverse proxy setup
  - Rate limiting
  - Security headers
  - Compression
  - Health endpoint

- **health-check.sh** - Health check script for Nginx

### Deployment Script
- **deploy.sh** - Automated deployment script
  - Build & push Docker images
  - Create ECR repositories
  - Deploy CloudFormation stack
  - Retrieve stack outputs

---

## 🚀 Quick Start

### For the impatient:
```bash
chmod +x deploy.sh
./deploy.sh
```

### For step-by-step:
See **QUICK_START.md**

### For detailed info:
See **DEPLOYMENT_CHECKLIST.md**

---

## 🏗️ Architecture

```
AWS Cloud
├── VPC (10.0.0.0/16)
│   ├── Public Subnets
│   │   ├── Application Load Balancer (Port 80/443)
│   │   │   └── Routes → ECS Tasks
│   │   └── NAT Gateway
│   │
│   ├── Private Subnets
│   │   ├── ECS Cluster (Fargate)
│   │   │   ├── Inventory App Task (Min: 2, Max: 4)
│   │   │   └── Nginx Task
│   │   ├── RDS PostgreSQL (Multi-AZ)
│   │   ├── ElastiCache Redis
│   │   └── Security Groups
│   │
│   └── Internet Gateway
│
├── S3 Bucket (File Storage)
├── CloudWatch Logs
├── CloudWatch Metrics
└── Secrets Manager
    ├── Database credentials
    ├── Redis password
    ├── JWT secret
    └── AWS S3 keys
```

---

## 💾 Database

**PostgreSQL 16**
- Multi-AZ deployment
- 30-day backup retention
- Automated failover
- Connection pooling (10 max connections)
- CloudWatch logs

Flyway migrations run automatically on startup.

---

## 🔄 Caching

**Redis 7**
- Elastic cache for session & data caching
- 600s default TTL
- Connection pooling

---

## 🔐 Security

- VPC isolated network
- Security groups for network isolation
- Secrets Manager for sensitive data
- IAM roles with least privilege
- S3 bucket encryption
- RDS encryption at rest
- HTTPS-ready (configure ACM certificate)

---

## 📊 Monitoring

- CloudWatch Container Insights
- Application logs in `/ecs/inventory-app-prod`
- Nginx logs in `/ecs/inventory-nginx-prod`
- Custom metrics via Spring Actuator
- Prometheus endpoint available

---

## 💰 Estimated Costs

|Service|Size|Cost/Month|
|---|---|---|
|ECS Tasks (Fargate)|512 CPU, 1GB RAM|$23|
|RDS PostgreSQL|db.t3.micro|$30|
|ElastiCache Redis|cache.t3.micro|$20|
|Application Load Balancer|1 ALB|$16|
|Data Transfer|~50GB|$5|
|**Total**||**~$94**|

*Costs vary by region and usage*

---

## 🔄 Auto-Scaling

Configured in CloudFormation:
- Min tasks: 2
- Max tasks: 4
- CPU target: 70%
- Memory target: 80%

---

## 🔧 Customization

### Change Instance Size
Edit `cloudformation-template.yaml`:
```yaml
InventoryDatabase:
  Properties:
    DBInstanceClass: db.t3.small  # Change from micro
```

### Change Auto-Scaling Range
Edit `cloudformation-template.yaml`:
```yaml
ECSService:
  Properties:
    DesiredCount: 3  # Change from 2
```

### Change Region
```bash
export AWS_REGION=eu-west-1
./deploy.sh
```

---

## 🚀 Deployment Workflow

1. **Prepare**
   - Commit code to git
   - Run tests locally
   - Update version in build.gradle

2. **Build & Push**
   - Build Docker images
   - Push to ECR
   - Tag with version

3. **Deploy**
   - Run CloudFormation template
   - Flyway migrations run automatically
   - ECS service updates

4. **Verify**
   - Check task health
   - Run smoke tests
   - Monitor logs

5. **Rollback** (if needed)
   ```bash
   aws ecs update-service \
     --cluster inventory-cluster-prod \
     --service inventory-service-prod \
     --force-new-deployment
   ```

---

## 📋 Pre-Deployment Checklist

- [ ] AWS Account created
- [ ] AWS CLI configured
- [ ] Docker installed & running
- [ ] Code committed to git
- [ ] Tests passing locally
- [ ] .env file secured
- [ ] Secrets created in AWS Secrets Manager

---

## 🆘 Support

### Logs
```bash
# Application logs
aws logs tail /ecs/inventory-app-prod --follow

# Nginx logs
aws logs tail /ecs/inventory-nginx-prod --follow

# Filter by error
aws logs tail /ecs/inventory-app-prod --filter-pattern ERROR
```

### Debugging
```bash
# Check ECS service status
aws ecs describe-services \
  --cluster inventory-cluster-prod \
  --services inventory-service-prod

# Check tasks
aws ecs list-tasks --cluster inventory-cluster-prod

# SSH into container (not possible with Fargate - use ECS Exec)
aws ecs execute-command \
  --cluster inventory-cluster-prod \
  --task <task-id> \
  --container inventory-app \
  --interactive \
  --command "/bin/sh"
```

---

## 📚 Additional Resources

- [AWS ECS Documentation](https://docs.aws.amazon.com/ecs/)
- [CloudFormation User Guide](https://docs.aws.amazon.com/cloudformation/)
- [RDS Best Practices](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/)
- [ElastiCache Best Practices](https://docs.aws.amazon.com/elasticache/)
- [Spring Boot on AWS](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-aws.html)

---

**Created**: June 4, 2026
**Version**: 1.0
**Status**: Production Ready ✅

