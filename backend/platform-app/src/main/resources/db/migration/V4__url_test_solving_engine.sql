-- ============================================================================
-- V4: URL Test Discovery and AI Solving Engine Schema
-- Multi-tenant, isolated tickets and verifiable AI solving pipeline
-- ============================================================================

CREATE TABLE IF NOT EXISTS url_discovery_sessions (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES iam_tenants(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES iam_users(id) ON DELETE CASCADE,
    target_url VARCHAR(1000) NOT NULL,
    platform_type VARCHAR(100) NOT NULL DEFAULT 'GENERIC_TICKETS',
    discovered_title VARCHAR(500),
    ticket_count INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    error_message TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_discovery_tenant ON url_discovery_sessions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_discovery_user ON url_discovery_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_discovery_status ON url_discovery_sessions(status);

CREATE TABLE IF NOT EXISTS url_discovered_tickets (
    id UUID PRIMARY KEY,
    discovery_session_id UUID NOT NULL REFERENCES url_discovery_sessions(id) ON DELETE CASCADE,
    ticket_number INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    ticket_url VARCHAR(1000) NOT NULL,
    question_count INT NOT NULL DEFAULT 0,
    category VARCHAR(100),
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    metadata_json TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ticket_discovery_session ON url_discovered_tickets(discovery_session_id);
CREATE INDEX IF NOT EXISTS idx_ticket_number ON url_discovered_tickets(discovery_session_id, ticket_number);

CREATE TABLE IF NOT EXISTS url_solving_sessions (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES iam_tenants(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES iam_users(id) ON DELETE CASCADE,
    discovery_session_id UUID REFERENCES url_discovery_sessions(id) ON DELETE SET NULL,
    target_url VARCHAR(1000) NOT NULL,
    selected_ticket_numbers TEXT NOT NULL,
    language VARCHAR(20) NOT NULL DEFAULT 'uz-Latn',
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    total_questions INT NOT NULL DEFAULT 0,
    solved_questions_count INT NOT NULL DEFAULT 0,
    verified_questions_count INT NOT NULL DEFAULT 0,
    review_needed_count INT NOT NULL DEFAULT 0,
    execution_time_ms BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_solving_tenant ON url_solving_sessions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_solving_user ON url_solving_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_solving_created_at ON url_solving_sessions(created_at DESC);

CREATE TABLE IF NOT EXISTS url_solved_questions (
    id UUID PRIMARY KEY,
    solving_session_id UUID NOT NULL REFERENCES url_solving_sessions(id) ON DELETE CASCADE,
    ticket_number INT NOT NULL,
    ticket_title VARCHAR(255) NOT NULL,
    question_index INT NOT NULL,
    question_text TEXT NOT NULL,
    image_url TEXT,
    options_json TEXT NOT NULL,
    selected_answer_index INT NOT NULL,
    selected_answer_text TEXT NOT NULL,
    ai_confidence_score DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    verification_status VARCHAR(50) NOT NULL DEFAULT 'SOLVED',
    reasoning_explanation TEXT NOT NULL,
    rule_reference VARCHAR(500),
    ambiguity_note TEXT,
    arithmetic_verified BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_solved_q_session ON url_solved_questions(solving_session_id);
CREATE INDEX IF NOT EXISTS idx_solved_q_ticket ON url_solved_questions(solving_session_id, ticket_number);
