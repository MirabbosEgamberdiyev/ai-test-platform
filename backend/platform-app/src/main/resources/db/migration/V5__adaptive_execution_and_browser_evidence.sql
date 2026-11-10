-- ============================================================================
-- V5: Adaptive Execution Modes and Authorized Browser Evidence Schema
-- Supports ANSWER_MODE, GUIDED_MODE, AUTHORIZED_AUTOMATION_MODE, BLOCKED_MODE
-- ============================================================================

ALTER TABLE url_solving_sessions
    ADD COLUMN IF NOT EXISTS execution_mode VARCHAR(50) NOT NULL DEFAULT 'ANSWER_MODE',
    ADD COLUMN IF NOT EXISTS submission_status VARCHAR(50) NOT NULL DEFAULT 'NOT_STARTED',
    ADD COLUMN IF NOT EXISTS screenshot_path TEXT,
    ADD COLUMN IF NOT EXISTS execution_evidence_json TEXT,
    ADD COLUMN IF NOT EXISTS browser_logs TEXT;

CREATE INDEX IF NOT EXISTS idx_solving_mode ON url_solving_sessions(execution_mode);
CREATE INDEX IF NOT EXISTS idx_solving_submission_status ON url_solving_sessions(submission_status);
