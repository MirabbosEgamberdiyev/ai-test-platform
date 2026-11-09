terraform {
  required_version = ">= 1.7.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.40"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.27"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.12"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# VPC and Network Isolation
resource "aws_vpc" "platform_vpc" {
  cidr_block           = var.vpc_cidr
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name        = "ai-test-platform-vpc"
    Environment = var.environment
  }
}

# Managed RDS PostgreSQL 16 (Multi-AZ)
resource "aws_db_instance" "postgres_primary" {
  identifier          = "ai-test-platform-postgres"
  engine              = "postgres"
  engine_version      = "16.2"
  instance_class      = "db.r6g.xlarge"
  allocated_storage   = 100
  max_allocated_storage = 500
  storage_type        = "gp3"
  multi_az            = true
  publicly_accessible = false
  skip_final_snapshot = false
  final_snapshot_identifier = "ai-test-platform-final-snapshot"

  db_name  = "ai_test_platform"
  username = "platform_admin"
  password = var.db_password

  backup_retention_period = 30
  backup_window          = "02:00-03:00"
  maintenance_window     = "Sun:04:00-Sun:05:00"

  storage_encrypted = true

  tags = {
    Name        = "ai-test-platform-rds"
    Environment = var.environment
  }
}

# Managed ElastiCache Redis 7 Cluster
resource "aws_elasticache_replication_group" "redis" {
  replication_group_id          = "ai-platform-redis-cluster"
  description                   = "Redis cluster for caching and distributed locks"
  engine                        = "redis"
  engine_version                = "7.1"
  node_type                     = "cache.m6g.large"
  num_cache_clusters            = 2
  parameter_group_name          = "default.redis7"
  port                          = 6379
  automatic_failover_enabled    = true
  at_rest_encryption_enabled    = true
  transit_encryption_enabled    = true
  auth_token                    = var.redis_auth_token

  tags = {
    Name        = "ai-test-platform-redis"
    Environment = var.environment
  }
}
