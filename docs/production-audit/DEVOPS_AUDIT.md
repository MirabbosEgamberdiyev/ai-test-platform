# Enterprise AI Test Platform — DevOps & Infrastructure Audit

## 1. Containerization & Orchestration

- **Backend Container**: Multi-stage build (`backend/Dockerfile`) using `eclipse-temurin:21-jdk-alpine` for build and `eclipse-temurin:21-jre-alpine` for minimal, hardened runtime image.
- **Frontend Container**: Multi-stage build (`frontend/Dockerfile`) using `node:20-alpine` with Next.js standalone output.
- **Compose Stack (`infrastructure/docker-compose.yml`)**:
  - `postgres`: PostgreSQL 16 Alpine with persistent volume `pgdata`.
  - `redis`: Redis 7 Alpine for rate limiting and cache.
  - `rabbitmq`: RabbitMQ 3.13 Management for event-driven messaging.
  - `prometheus` & `grafana`: Pre-configured metrics and monitoring dashboards.

## 2. CI/CD Pipeline & Quality Gates

The GitHub Actions workflow (`.github/workflows/ci-pipeline.yml`) validates:
1. Java 21 compilation and Maven reactor build.
2. Unit and integration tests across all 11 modules.
3. ArchUnit clean architecture gate.
4. Next.js production build and TypeScript type-checking.
5. Secret scanning and dependency vulnerability checks.

## 3. Backup, Restore & Rollback Strategy

- **Database Backup**: Automated pg_dump scripts with daily rotation and gzip compression.
- **Rollback Procedure**: Flyway migration rollback and blue/green container traffic switching.
