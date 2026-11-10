# Autonomous Project Audit: Database Audit

## 1. Database Architecture & Engine
- **Engine**: PostgreSQL 16.15
- **Migration Manager**: Flyway 10.x
- **Connection Pool**: HikariCP (Max 20, Min Idle 5, Validation Timeout 3000ms)
- **Active Migrations**:
  - `V1__init_platform_schema.sql` (IAM, Registry, TestOps base)
  - `V2__universal_web_testing_schema.sql` (Web projects, test runs, defects)
  - `V3__production_auth_and_exam_engine.sql` (Google OAuth, security tokens)

## 2. Schema Expansion for URL Test Solving Engine (V4 Migration)

To support the URL-based AI Test Solving workflow without altering existing tables, migration `V4__url_test_solving_engine.sql` introduces:

```sql
-- 1. URL Discovery Sessions
CREATE TABLE IF NOT EXISTS url_discovery_sessions (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL,
    target_url VARCHAR(500) NOT NULL,
    platform_id VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    page_title VARCHAR(255),
    total_tickets_found INT NOT NULL DEFAULT 0,
    completeness_notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Discovered Tickets Catalog
CREATE TABLE IF NOT EXISTS url_discovered_tickets (
    id UUID PRIMARY KEY,
    discovery_session_id UUID NOT NULL REFERENCES url_discovery_sessions(id) ON DELETE CASCADE,
    ticket_number INT NOT NULL,
    ticket_title VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    subject VARCHAR(100),
    estimated_questions INT DEFAULT 0,
    ticket_url VARCHAR(500),
    ticket_selector VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. URL Solving Sessions
CREATE TABLE IF NOT EXISTS url_solving_sessions (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL,
    discovery_session_id UUID REFERENCES url_discovery_sessions(id) ON DELETE SET NULL,
    target_url VARCHAR(500) NOT NULL,
    selected_ticket_numbers VARCHAR(500) NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_questions INT NOT NULL DEFAULT 0,
    solved_questions INT NOT NULL DEFAULT 0,
    verified_questions INT NOT NULL DEFAULT 0,
    needs_review_questions INT NOT NULL DEFAULT 0,
    ai_provider VARCHAR(50) NOT NULL DEFAULT 'GEMINI',
    ai_model VARCHAR(50),
    tokens_used INT DEFAULT 0,
    language VARCHAR(20) NOT NULL DEFAULT 'uz-Latn',
    started_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMPTZ
);

-- 4. Solved Questions & AI Rationales
CREATE TABLE IF NOT EXISTS url_solved_questions (
    id UUID PRIMARY KEY,
    solving_session_id UUID NOT NULL REFERENCES url_solving_sessions(id) ON DELETE CASCADE,
    ticket_number INT NOT NULL,
    ticket_title VARCHAR(255),
    question_number INT NOT NULL,
    question_text TEXT NOT NULL,
    question_type VARCHAR(50) NOT NULL DEFAULT 'SINGLE_CHOICE',
    options_json TEXT,
    image_url VARCHAR(500),
    selected_answer TEXT,
    explanation TEXT,
    confidence_score DECIMAL(5,2),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    is_ambiguous BOOLEAN DEFAULT FALSE,
    ambiguity_reason TEXT,
    arithmetic_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## 3. Indexing & Query Performance
- Index on `url_discovery_sessions(tenant_id, user_id)`
- Index on `url_discovered_tickets(discovery_session_id, ticket_number)`
- Index on `url_solving_sessions(tenant_id, user_id, status)`
- Index on `url_solved_questions(solving_session_id, ticket_number, question_number)`
- Enforces strict foreign keys and cascading deletes where child questions belong to parent sessions.
