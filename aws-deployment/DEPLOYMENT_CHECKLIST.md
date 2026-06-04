# AWS Deployment Checklist

## Pre-Deployment Steps

### AWS Account Setup
- [ ] Create AWS Account (https://aws.amazon.com)
- [ ] Set up IAM user with programmatic access
- [ ] Generate Access Key ID and Secret Access Key
- [ ] Save credentials securely

### Local Setup
- [ ] Install AWS CLI v2
- [ ] Install Docker Desktop
- [ ] Configure AWS CLI: `aws configure`
- [ ] Verify AWS access: `aws sts get-caller-identity`

### Application Preparation
- [ ] Update `.env` with production values
- [ ] Build JAR: `./gradlew bootJar`
- [ ] Test Docker build: `docker build -t inventory-app .`
- [ ] Run tests: `./gradlew test`
- [ ] Fix all compile/test errors

---

## AWS Console Setup (One-Time)

### 1. Create VPC (Optional - CloudFormation creates this)
- [ ] VPC CIDR: 10.0.0.0/16
- [ ] Public Subnets: 10.0.1.0/24, 10.0.2.0/24
- [ ] Private Subnets: 10.0.11.0/24, 10.0.12.0/24
- [ ] Internet Gateway attached
- [ ] NAT Gateway for private subnet

### 2. Store Secrets in AWS Secrets Manager
```bash
aws secretsmanager create-secret --name inventory/db-url \
  --secret-string "jdbc:postgresql://[RDS-ENDPOINT]:5432/kk_db"

aws secretsmanager create-secret --name inventory/db-username \
  --secret-string "postgres"

aws secretsmanager create-secret --name inventory/db-password \
  --secret-string "YourSecurePassword123!"

aws secretsmanager create-secret --name inventory/redis-host \
  --secret-string "your-redis-endpoint.ng.0001.use1.cache.amazonaws.com"

aws secretsmanager create-secret --name inventory/redis-password \
  --secret-string "YourRedisPassword123!"

aws secretsmanager create-secret --name inventory/jwt-secret \
  --secret-string "VGhpc0lzQVN0cm9uZ0RldmVsb3BtZW50S2V5VGhhdE11c3RCZUF0TGVhc3QzMkJ5dGVzTG9uZw=="
```

---

## Deployment Steps

### Option 1: Automated Deployment (Recommended)

```bash
# Make deploy script executable
chmod +x aws-deployment/deploy.sh

# Run deployment script
./aws-deployment/deploy.sh
```

**Script performs:**
1. ✓ Authenticates with AWS ECR
2. ✓ Creates ECR repositories
3. ✓ Builds Docker images
4. ✓ Pushes to ECR
5. ✓ Creates CloudFormation stack
6. ✓ Deploys RDS, ElastiCache, ECS, ALB

---

### Option 2: Manual Step-by-Step Deployment

#### Step 1: Login to AWS ECR
```bash
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com
```

#### Step 2: Create ECR Repositories
```bash
aws ecr create-repository --repository-name inventory-app --region us-east-1
aws ecr create-repository --repository-name inventory-nginx --region us-east-1
```

#### Step 3: Build Images
```bash
# Build inventory-app
docker build -t inventory-app:latest .
docker tag inventory-app:latest YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/inventory-app:latest
docker push YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/inventory-app:latest

# Build nginx
docker build -t inventory-nginx:latest -f aws-deployment/nginx.Dockerfile ./nginx
docker tag inventory-nginx:latest YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/inventory-nginx:latest
docker push YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/inventory-nginx:latest
```

#### Step 4: Deploy with CloudFormation
```bash
aws cloudformation deploy \
  --template-file aws-deployment/cloudformation-template.yaml \
  --stack-name inventory-stack-prod \
  --parameter-overrides \
    EnvironmentName=prod \
    DBUsername=postgres \
    DBPassword=YourSecurePassword123! \
    RedisPassword=YourRedisPassword123! \
  --capabilities CAPABILITY_NAMED_IAM \
  --region us-east-1
```

#### Step 5: Verify Deployment
```bash
# Check stack status
aws cloudformation describe-stacks \
  --stack-name inventory-stack-prod \
  --query 'Stacks[0].[StackStatus,CreationTime]' \
  --region us-east-1

# Get outputs
aws cloudformation describe-stacks \
  --stack-name inventory-stack-prod \
  --query 'Stacks[0].Outputs' \
  --output table \
  --region us-east-1

# Check ECS service
aws ecs describe-services \
  --cluster inventory-cluster-prod \
  --services inventory-service-prod \
  --region us-east-1
```

---

## Post-Deployment Verification

### 1. Check Application Health
```bash
# Get Load Balancer DNS
ALB_DNS=$(aws cloudformation describe-stacks \
  --stack-name inventory-stack-prod \
  --query 'Stacks[0].Outputs[?OutputKey==`LoadBalancerDNS`].OutputValue' \
  --output text \
  --region us-east-1)

# Test health endpoint
curl http://$ALB_DNS/actuator/health

# Should return: {"status":"UP"}
```

### 2. Check ECS Tasks
```bash
aws ecs list-tasks \
  --cluster inventory-cluster-prod \
  --region us-east-1

aws ecs describe-tasks \
  --cluster inventory-cluster-prod \
  --tasks <task-arn> \
  --region us-east-1
```

### 3. Check Logs
```bash
# App logs
aws logs tail /ecs/inventory-app-prod --follow --region us-east-1

# Nginx logs
aws logs tail /ecs/inventory-nginx-prod --follow --region us-east-1

# RDS logs
aws rds describe-db-log-files \
  --db-instance-identifier inventory-db-prod \
  --region us-east-1
```

### 4. Test API Endpoints
```bash
# Get ALB DNS
ALB_DNS=$(aws cloudformation describe-stacks \
  --stack-name inventory-stack-prod \
  --query 'Stacks[0].Outputs[?OutputKey==`LoadBalancerDNS`].OutputValue' \
  --output text \
  --region us-east-1)

# Test health
curl http://$ALB_DNS/actuator/health

# Test API
curl http://$ALB_DNS/api/v1/products

# Test Swagger UI
open http://$ALB_DNS/swagger-ui.html
```

---

## Common Issues & Solutions

### Issue: ECR Login Fails
```
Error: no basic auth credentials
```
**Solution:**
```bash
# Re-authenticate
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com
```

### Issue: CloudFormation Stack Creation Fails
```
Template error: resource already exists
```
**Solution:**
```bash
# Delete stack first
aws cloudformation delete-stack --stack-name inventory-stack-prod --region us-east-1

# Wait for deletion
aws cloudformation wait stack-delete-complete --stack-name inventory-stack-prod --region us-east-1

# Try deploying again
aws cloudformation deploy ...
```

### Issue: ECS Task keeps crashing
```
Task stopped: Essential container exited
```
**Solution:**
```bash
# Check task logs
aws logs tail /ecs/inventory-app-prod --follow

# Common causes:
# 1. Database connection string incorrect
# 2. Database not accessible from ECS
# 3. Secrets not accessible
# 4. OutOfMemory - increase task memory

# Update service
aws ecs update-service \
  --cluster inventory-cluster-prod \
  --service inventory-service-prod \
  --force-new-deployment \
  --region us-east-1
```

### Issue: RDS Database Not Accessible
```bash
# Check security group
aws ec2 describe-security-groups --group-ids sg-xxxxx

# Test connectivity
psql -h <RDS_ENDPOINT> -U postgres -d kk_db

# Run migrations manually
aws secretsmanager get-secret-value --secret-id inventory/db-password
```

---

## Monitoring & Alerts

### Set Up CloudWatch Alarms

```bash
# High CPU
aws cloudwatch put-metric-alarm \
  --alarm-name inventory-high-cpu \
  --alarm-description "Alert when CPU exceeds 80%" \
  --metric-name CPUUtilization \
  --namespace AWS/ECS \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold

# Task failed
aws cloudwatch put-metric-alarm \
  --alarm-name inventory-task-failed \
  --alarm-description "Alert when tasks fail" \
  --metric-name TaskCount \
  --namespace AWS/ECS \
  --statistic Average \
  --period 300 \
  --threshold 1 \
  --comparison-operator LessThanThreshold

# High memory
aws cloudwatch put-metric-alarm \
  --alarm-name inventory-high-memory \
  --alarm-description "Alert when memory exceeds 80%" \
  --metric-name MemoryUtilization \
  --namespace AWS/ECS \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold
```

---

## Scaling Configuration

### Auto-Scaling for ECS Service

```bash
# Create scaling policy
aws application-autoscaling register-scalable-target \
  --service-namespace ecs \
  --resource-id service/inventory-cluster-prod/inventory-service-prod \
  --scalable-dimension ecs:service:DesiredCount \
  --min-capacity 2 \
  --max-capacity 4

# Scale on CPU
aws application-autoscaling put-scaling-policy \
  --policy-name cpu-scaling \
  --service-namespace ecs \
  --resource-id service/inventory-cluster-prod/inventory-service-prod \
  --scalable-dimension ecs:service:DesiredCount \
  --policy-type TargetTrackingScaling \
  --target-tracking-scaling-policy-configuration \
    TargetValue=70,PredefinedMetricSpecification={PredefinedMetricType=ECSServiceAverageCPUUtilization}

# Scale on Memory
aws application-autoscaling put-scaling-policy \
  --policy-name memory-scaling \
  --service-namespace ecs \
  --resource-id service/inventory-cluster-prod/inventory-service-prod \
  --scalable-dimension ecs:service:DesiredCount \
  --policy-type TargetTrackingScaling \
  --target-tracking-scaling-policy-configuration \
    TargetValue=80,PredefinedMetricSpecification={PredefinedMetricType=ECSServiceAverageMemoryUtilization}
```

---

## Cleanup

### Delete All AWS Resources
```bash
# Delete CloudFormation stack (deletes all resources)
aws cloudformation delete-stack --stack-name inventory-stack-prod --region us-east-1

# Wait for deletion
aws cloudformation wait stack-delete-complete --stack-name inventory-stack-prod --region us-east-1

# Delete ECR repositories
aws ecr delete-repository --repository-name inventory-app --force --region us-east-1
aws ecr delete-repository --repository-name inventory-nginx --force --region us-east-1

# Delete CloudWatch log groups
aws logs delete-log-group --log-group-name /ecs/inventory-app-prod --region us-east-1
aws logs delete-log-group --log-group-name /ecs/inventory-nginx-prod --region us-east-1

# Verify cleanup
aws ec2 describe-vpcs --filter "Name=cidr,Values=10.0.0.0/16" --region us-east-1
```

---

## Support & Resources

- **AWS ECS Documentation**: https://docs.aws.amazon.com/ecs/
- **AWS CloudFormation**: https://docs.aws.amazon.com/cloudformation/
- **AWS RDS**: https://docs.aws.amazon.com/rds/
- **AWS ElastiCache**: https://docs.aws.amazon.com/elasticache/
- **AWS Pricing Calculator**: https://calculator.aws/

---

**Last Updated**: June 4, 2026
**Deployment Guide Version**: 1.0

