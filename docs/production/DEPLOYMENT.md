# Production Deployment & Operations Guide

## 1. Prerequisites & Infrastructure Requirements

- **Linux / Windows Server**: 4+ vCPU, 8 GB+ RAM.
- **Java Runtime**: OpenJDK 21 LTS (64-bit).
- **PostgreSQL**: PostgreSQL 16+ (Database `ai_test_platform`, Flyway automated migrations V1-V3).
- **Redis**: Redis 7+ (Token blacklist, cache).
- **Node.js**: Node.js 20+ LTS (Next.js 14 production standalone or npm run start).
- **Docker & Compose**: For running containerized database, cache, and Playwright worker nodes.

---

## 2. Docker & Service Orchestration

Run all infrastructure services from project root:

```bash
docker compose -f docker-compose.yml up -d postgres redis
```

Verify service health:
```bash
docker ps
```

---

## 3. Database Migration Management

Flyway automates schema state:
- `V1__init_schema.sql`: Core IAM, tenants, test plans, practice sessions, questions.
- `V2__universal_web_testing.sql`: Web projects, universal execution runs, test plan items, defects.
- `V3__production_auth_and_exam_engine.sql`: Google OAuth columns (`google_id`, `auth_provider`, `email_verified`) and `iam_security_tokens` table for SHA-256 password resets.

Migrations execute automatically on application startup.

---

## 4. Building Production Artifacts

### Backend Fat Jar:
```bash
cd backend
mvn clean package -DskipTests
# Output: backend/platform-app/target/platform-app-1.0.0-SNAPSHOT.jar
```

### Frontend Production Build:
```bash
cd frontend
npm install
npm run build
# Output: frontend/.next optimized production bundle
```

---

## 5. Running Production Services

### Running Spring Boot Backend (Port 8085):
```bash
java -jar -Dspring.profiles.active=prod backend/platform-app/target/platform-app-1.0.0-SNAPSHOT.jar
```

### Running Next.js Frontend (Port 3000):
```bash
cd frontend
npm run start -- -p 3000
```
