output "vpc_id" {
  value       = aws_vpc.platform_vpc.id
  description = "VPC ID"
}

output "postgres_endpoint" {
  value       = aws_db_instance.postgres_primary.endpoint
  description = "PostgreSQL primary host endpoint"
}

output "redis_primary_endpoint" {
  value       = aws_elasticache_replication_group.redis.primary_endpoint_address
  description = "Redis cluster primary endpoint"
}
