-- ==============================================================================
-- ENTERPRISE AI TEST AUTOMATION PLATFORM - FLYWAY V2 UNIVERSAL WEB TESTING SCHEMA
-- ==============================================================================

-- 1. EXTEND REGISTRY WEBSITES FOR ENTERPRISE WEB TESTING
ALTER TABLE registry_websites ADD COLUMN IF NOT EXISTS environment VARCHAR(50) NOT NULL DEFAULT 'PRODUCTION';
ALTER TABLE registry_websites ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE registry_websites ADD COLUMN IF NOT EXISTS testing_instructions TEXT;
ALTER TABLE registry_websites ADD COLUMN IF NOT EXISTS is_authorized BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE registry_websites ADD COLUMN IF NOT EXISTS tenant_id UUID;

-- 2. UNIVERSAL WEB TEST PROJECTS
CREATE TABLE IF NOT EXISTS web_test_projects (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    website_id UUID REFERENCES registry_websites(id) ON DELETE SET NULL,
    name VARCHAR(150) NOT NULL,
    target_url VARCHAR(500) NOT NULL,
    environment VARCHAR(50) NOT NULL DEFAULT 'PRODUCTION',
    description TEXT,
    instructions TEXT,
    is_authorized BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_web_projects_tenant ON web_test_projects(tenant_id);

-- 3. AI-GENERATED WEB TEST PLANS
CREATE TABLE IF NOT EXISTS web_test_plans (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES web_test_projects(id) ON DELETE CASCADE,
    tenant_id UUID NOT NULL,
    natural_language_goal TEXT NOT NULL,
    detected_scope VARCHAR(200),
    language VARCHAR(20) NOT NULL DEFAULT 'en',
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    discovery_summary TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_web_plans_proj ON web_test_plans(project_id);
CREATE INDEX IF NOT EXISTS idx_web_plans_tenant ON web_test_plans(tenant_id);

-- 4. STRUCTURED WEB TEST CASES
CREATE TABLE IF NOT EXISTS web_test_cases (
    id UUID PRIMARY KEY,
    plan_id UUID NOT NULL REFERENCES web_test_plans(id) ON DELETE CASCADE,
    tenant_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    steps_json TEXT NOT NULL,
    expected_result TEXT NOT NULL,
    order_index INT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_web_cases_plan ON web_test_cases(plan_id);

-- 5. WEB TEST RUNS (EXECUTION LIFECYCLE)
CREATE TABLE IF NOT EXISTS web_test_runs (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES web_test_projects(id) ON DELETE CASCADE,
    plan_id UUID NOT NULL REFERENCES web_test_plans(id) ON DELETE CASCADE,
    tenant_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'CREATED',
    browser_type VARCHAR(50) NOT NULL DEFAULT 'CHROMIUM',
    viewport_width INT NOT NULL DEFAULT 1280,
    viewport_height INT NOT NULL DEFAULT 800,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    total_cases INT NOT NULL DEFAULT 0,
    passed_cases INT NOT NULL DEFAULT 0,
    failed_cases INT NOT NULL DEFAULT 0,
    blocked_cases INT NOT NULL DEFAULT 0,
    skipped_cases INT NOT NULL DEFAULT 0,
    error_summary TEXT,
    execution_logs TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_web_runs_tenant ON web_test_runs(tenant_id);
CREATE INDEX IF NOT EXISTS idx_web_runs_project ON web_test_runs(project_id);

-- 6. WEB DEFECTS WITH AI ROOT-CAUSE ANALYSIS
CREATE TABLE IF NOT EXISTS web_defects (
    id UUID PRIMARY KEY,
    run_id UUID NOT NULL REFERENCES web_test_runs(id) ON DELETE CASCADE,
    case_id UUID REFERENCES web_test_cases(id) ON DELETE SET NULL,
    tenant_id UUID NOT NULL,
    defect_code VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    severity VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    reproduction_steps TEXT NOT NULL,
    expected_result TEXT NOT NULL,
    actual_result TEXT NOT NULL,
    screenshot_path VARCHAR(500),
    console_logs TEXT,
    network_evidence TEXT,
    ai_root_cause TEXT,
    confidence_score DOUBLE PRECISION NOT NULL DEFAULT 0.85,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_web_defects_run ON web_defects(run_id);
CREATE INDEX IF NOT EXISTS idx_web_defects_tenant ON web_defects(tenant_id);

-- 7. PROFESSIONAL WEB TEST REPORTS
CREATE TABLE IF NOT EXISTS web_test_reports (
    id UUID PRIMARY KEY,
    run_id UUID NOT NULL REFERENCES web_test_runs(id) ON DELETE CASCADE,
    tenant_id UUID NOT NULL,
    website_name VARCHAR(150) NOT NULL,
    target_url VARCHAR(500) NOT NULL,
    execution_duration_ms BIGINT NOT NULL DEFAULT 0,
    pass_percentage DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    critical_defects_count INT NOT NULL DEFAULT 0,
    high_defects_count INT NOT NULL DEFAULT 0,
    medium_defects_count INT NOT NULL DEFAULT 0,
    low_defects_count INT NOT NULL DEFAULT 0,
    ai_executive_summary TEXT,
    ai_recommendations TEXT,
    json_report_path VARCHAR(500),
    html_report_path VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_web_reports_run ON web_test_reports(run_id);
CREATE INDEX IF NOT EXISTS idx_web_reports_tenant ON web_test_reports(tenant_id);
