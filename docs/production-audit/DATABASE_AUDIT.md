# Enterprise AI Test Platform — Database Audit

## 1. Database Architecture & Migrations

- **Database Engine**: PostgreSQL 16 (Production) / H2 In-Memory (Test and Dev fallback)
- **Migration Engine**: Flyway 10.x
- **Connection Pool**: HikariCP (optimized pool sizing: `maximumPoolSize=20`, `minimumIdle=5`)
- **Isolation Level**: `READ_COMMITTED` with optimistic locking (`@Version`)

## 2. Existing Schema Migration Inventory

| Version | Description | Target Tables |
| :--- | :--- | :--- |
| `V1__init_platform_schema.sql` | Core Platform, IAM, Registry, Practice Engine, Audit | `tenants`, `users`, `refresh_tokens`, `registry_websites`, `test_questions`, `practice_sessions`, `practice_session_answers`, `audit_events`, `outbox_events` |
| `V2__universal_web_testing_schema.sql` | Universal AI Web Testing Engine | `web_test_projects`, `web_test_plans`, `web_test_cases`, `web_test_runs`, `web_defects`, `web_test_reports` |

## 3. Schema Evaluation & Indexing Analysis

### Core Entities & Relationships
1. **`tenants`**: Primary partition key `id (UUID)`, unique slug `slug (VARCHAR(50))`.
2. **`users`**: Foreign key `tenant_id -> tenants(id)`, unique index on `(tenant_id, email)`.
3. **`test_questions`**: Questions categorized by `subject`, `topic`, `difficulty_level`, and `is_active`. Indexed by `(tenant_id, subject, topic)`.
4. **`practice_sessions`**: Tracks student exam progress. Foreign keys `tenant_id`, `user_id`. Indexed by `(tenant_id, user_id, status)`.
5. **`practice_session_answers`**: Stores answers submitted by students. Unique composite key `(session_id, question_id)` preventing duplicate submissions.
6. **`web_test_runs`**: Stores test execution runs. Foreign keys `project_id`, `plan_id`. Indexed by `(tenant_id, created_at DESC)`.

## 4. Database Findings & Migration Plan

### [FINDING-DB-01] [HIGH] User Identity Enhancement for Google & Email Verification
- **Gap**: The `users` table lacks dedicated columns for `google_id`, `email_verified (BOOLEAN)`, and `auth_provider (VARCHAR)`.
- **Remediation**: Create `V3__production_auth_and_exam_engine.sql` adding:
  - `google_id VARCHAR(100) NULL` with unique index `idx_users_google_id`
  - `email_verified BOOLEAN DEFAULT FALSE`
  - `auth_provider VARCHAR(20) DEFAULT 'LOCAL'`

### [FINDING-DB-02] [MEDIUM] Persistent Password Reset & Email Verification Tokens
- **Gap**: Temporary tokens for password recovery and email activation must be persisted with expiration timestamps.
- **Remediation**: Add table `user_security_tokens` in `V3` migration:
  - `id UUID PRIMARY KEY`, `user_id UUID REFERENCES users(id)`, `token_hash VARCHAR(128)`, `token_type VARCHAR(30)`, `expires_at TIMESTAMP`, `used_at TIMESTAMP`.
