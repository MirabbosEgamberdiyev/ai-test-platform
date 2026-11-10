-- ==============================================================================
-- ENTERPRISE AI TEST PLATFORM - FLYWAY V3 PRODUCTION AUTH & SECURITY TOKENS
-- ==============================================================================

-- 1. EXTEND IAM USERS FOR GOOGLE OAUTH & EMAIL VERIFICATION
ALTER TABLE iam_users ADD COLUMN IF NOT EXISTS google_id VARCHAR(100);
ALTER TABLE iam_users ADD COLUMN IF NOT EXISTS auth_provider VARCHAR(30) NOT NULL DEFAULT 'LOCAL';
ALTER TABLE iam_users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_users_google_id ON iam_users(google_id);

-- 2. DEDICATED SECURITY TOKENS FOR PASSWORD RESET & EMAIL ACTIVATION
CREATE TABLE IF NOT EXISTS iam_security_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES iam_users(id) ON DELETE CASCADE,
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    token_type VARCHAR(50) NOT NULL, -- 'PASSWORD_RESET', 'EMAIL_VERIFICATION'
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sec_tokens_hash ON iam_security_tokens(token_hash);
CREATE INDEX IF NOT EXISTS idx_sec_tokens_user_type ON iam_security_tokens(user_id, token_type);
