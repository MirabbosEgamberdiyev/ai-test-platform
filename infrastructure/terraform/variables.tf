variable "aws_region" {
  type        = string
  default     = "us-east-1"
  description = "Target AWS Region"
}

variable "environment" {
  type        = string
  default     = "production"
  description = "Deployment environment (staging, production)"
}

variable "vpc_cidr" {
  type        = string
  default     = "10.100.0.0/16"
  description = "CIDR block for the dedicated platform VPC"
}

variable "db_password" {
  type        = string
  sensitive   = true
  description = "Master password for PostgreSQL database"
}

variable "redis_auth_token" {
  type        = string
  sensitive   = true
  description = "Auth token for Redis ElastiCache cluster"
}
