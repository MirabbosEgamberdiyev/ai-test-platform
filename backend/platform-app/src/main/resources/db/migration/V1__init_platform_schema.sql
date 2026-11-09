-- ==============================================================================
-- ENTERPRISE AI TEST AUTOMATION PLATFORM - FLYWAY V1 INITIAL SCHEMA
-- ==============================================================================

-- 1. IAM MODULE TABLES
CREATE TABLE IF NOT EXISTS iam_tenants (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    tier VARCHAR(30) NOT NULL DEFAULT 'FREE',
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS iam_users (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_LEARNER',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    telegram_chat_id VARCHAR(50),
    mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    mfa_secret VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_tenant_email ON iam_users(tenant_id, email);

CREATE TABLE IF NOT EXISTS iam_refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_hash ON iam_refresh_tokens(token_hash) WHERE revoked = false;

-- 2. REGISTRY MODULE TABLES
CREATE TABLE IF NOT EXISTS registry_websites (
    id UUID PRIMARY KEY,
    domain_name VARCHAR(150) NOT NULL UNIQUE,
    display_name VARCHAR(150) NOT NULL,
    base_url VARCHAR(500) NOT NULL,
    platform_type VARCHAR(50) NOT NULL DEFAULT 'GENERIC_LMS',
    compliance_status VARCHAR(30) NOT NULL DEFAULT 'APPROVED',
    ssrf_verified BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS registry_integrations (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    website_id UUID NOT NULL,
    auth_type VARCHAR(50) NOT NULL DEFAULT 'USER_ASSISTED',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    config_json TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS registry_vault_secrets (
    id UUID PRIMARY KEY,
    integration_id UUID NOT NULL,
    encrypted_payload TEXT NOT NULL,
    iv VARCHAR(64) NOT NULL,
    key_version INTEGER NOT NULL DEFAULT 1,
    expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. TESTOPS & EXECUTION TABLES
CREATE TABLE IF NOT EXISTS testops_sessions (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL,
    integration_id UUID,
    target_url VARCHAR(500) NOT NULL,
    state VARCHAR(30) NOT NULL DEFAULT 'INITIALIZED',
    practice_mode VARCHAR(30) NOT NULL DEFAULT 'STUDY_ASSISTED',
    total_questions INTEGER NOT NULL DEFAULT 0,
    answered_questions INTEGER NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    started_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_sessions_tenant_user_state ON testops_sessions(tenant_id, user_id, state);

CREATE TABLE IF NOT EXISTS testops_questions (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL,
    sequence_number INTEGER NOT NULL,
    question_text TEXT NOT NULL,
    question_type VARCHAR(50) NOT NULL,
    options_json TEXT,
    image_url VARCHAR(500),
    user_answer VARCHAR(500),
    is_correct BOOLEAN,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_questions_session ON testops_questions(session_id, sequence_number);

CREATE TABLE IF NOT EXISTS testops_results (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL UNIQUE,
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL,
    total_questions INTEGER NOT NULL,
    correct_answers INTEGER NOT NULL,
    accuracy_percentage NUMERIC(5, 2) NOT NULL,
    time_spent_seconds BIGINT NOT NULL,
    summary_report_json TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_results_tenant_user ON testops_results(tenant_id, user_id);

-- 4. BILLING & SUBSCRIPTIONS
CREATE TABLE IF NOT EXISTS billing_subscriptions (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL UNIQUE,
    plan_tier VARCHAR(50) NOT NULL DEFAULT 'FREE',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    monthly_token_limit BIGINT NOT NULL DEFAULT 50000,
    used_tokens BIGINT NOT NULL DEFAULT 0,
    concurrent_sessions_limit INTEGER NOT NULL DEFAULT 2,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. AUDIT & OUTBOX TABLES
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    actor_id UUID,
    action_type VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    resource_id VARCHAR(100),
    client_ip VARCHAR(50),
    details_json TEXT,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_tenant_time ON audit_logs(tenant_id, timestamp DESC);

CREATE TABLE IF NOT EXISTS outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(100) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload_json TEXT NOT NULL,
    processed BOOLEAN NOT NULL DEFAULT FALSE,
    scheduled_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_outbox_unprocessed ON outbox_events(scheduled_at) WHERE processed = false;

-- 6. SEED DATA (Default allowed practice environments)
INSERT INTO registry_websites (id, domain_name, display_name, base_url, platform_type, compliance_status, ssrf_verified, created_at, updated_at)
VALUES 
    ('a0000000-0000-0000-0000-000000000001', 'quiz.example.com', 'Demo Practice LMS', 'https://quiz.example.com', 'GENERIC_LMS', 'APPROVED', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0000000-0000-0000-0000-000000000002', 'moodle.org', 'Moodle Open Portal', 'https://moodle.org', 'MOODLE', 'APPROVED', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (domain_name) DO NOTHING;
