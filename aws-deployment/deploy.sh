#!/bin/bash

# Inventory Management System - AWS Deployment Script
# This script automates the deployment of the application to AWS ECS

set -e

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
AWS_REGION=${AWS_REGION:-us-east-1}
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
ENVIRONMENT=${ENVIRONMENT:-prod}
ECR_REGISTRY="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

# Functions
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Main script
main() {
    log_info "Starting AWS Deployment Process..."
    log_info "Region: $AWS_REGION"
    log_info "Environment: $ENVIRONMENT"
    log_info "Account ID: $AWS_ACCOUNT_ID"

    # Step 1: Login to ECR
    log_info "Step 1: Authenticating with AWS ECR..."
    aws ecr get-login-password --region $AWS_REGION | \
        docker login --username AWS --password-stdin $ECR_REGISTRY
    log_info "✓ Logged in to ECR"

    # Step 2: Create ECR repositories if they don't exist
    log_info "Step 2: Creating ECR repositories..."
    for repo in inventory-app inventory-nginx; do
        if aws ecr describe-repositories --repository-names $repo --region $AWS_REGION &>/dev/null; then
            log_info "✓ Repository $repo already exists"
        else
            log_info "Creating repository $repo..."
            aws ecr create-repository --repository-name $repo --region $AWS_REGION
            log_info "✓ Created repository $repo"
        fi
    done

    # Step 3: Build and push Docker images
    log_info "Step 3: Building Docker images..."

    # Build inventory-app
    log_info "Building inventory-app..."
    docker build -t inventory-app:latest .
    docker tag inventory-app:latest $ECR_REGISTRY/inventory-app:latest
    docker tag inventory-app:latest $ECR_REGISTRY/inventory-app:$(date +%Y%m%d-%H%M%S)

    log_info "Pushing to ECR..."
    docker push $ECR_REGISTRY/inventory-app:latest
    docker push $ECR_REGISTRY/inventory-app:$(date +%Y%m%d-%H%M%S)
    log_info "✓ inventory-app pushed"

    # Build inventory-nginx
    log_info "Building inventory-nginx..."
    docker build -t inventory-nginx:latest -f nginx/Dockerfile ./nginx
    docker tag inventory-nginx:latest $ECR_REGISTRY/inventory-nginx:latest
    docker tag inventory-nginx:latest $ECR_REGISTRY/inventory-nginx:$(date +%Y%m%d-%H%M%S)

    log_info "Pushing to ECR..."
    docker push $ECR_REGISTRY/inventory-nginx:latest
    docker push $ECR_REGISTRY/inventory-nginx:$(date +%Y%m%d-%H%M%S)
    log_info "✓ inventory-nginx pushed"

    # Step 4: Create CloudFormation stack
    log_info "Step 4: Creating/Updating CloudFormation stack..."
    read -sp "Enter DB Password: " DB_PASSWORD
    echo
    read -sp "Enter Redis Password: " REDIS_PASSWORD
    echo

    aws cloudformation deploy \
        --template-file aws-deployment/cloudformation-template.yaml \
        --stack-name inventory-stack-$ENVIRONMENT \
        --parameter-overrides \
            EnvironmentName=$ENVIRONMENT \
            DBUsername=postgres \
            DBPassword=$DB_PASSWORD \
            RedisPassword=$REDIS_PASSWORD \
        --capabilities CAPABILITY_NAMED_IAM \
        --region $AWS_REGION \
        --no-fail-on-empty-changeset

    log_info "✓ CloudFormation stack deployed"

    # Step 5: Get stack outputs
    log_info "Step 5: Retrieving stack outputs..."
    aws cloudformation describe-stacks \
        --stack-name inventory-stack-$ENVIRONMENT \
        --region $AWS_REGION \
        --query 'Stacks[0].Outputs' \
        --output table

    log_info "Step 6: Deployment complete!"
    log_info "Access your application at the Load Balancer DNS above"
}

# Error handling
trap 'log_error "Deployment failed!"; exit 1' ERR

# Run main
main "$@"

