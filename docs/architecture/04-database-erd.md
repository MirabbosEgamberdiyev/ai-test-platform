# Database Architecture & Entity-Relationship Model

## 1. Storage Strategy & Guidelines

1. **RDBMS**: PostgreSQL 16 LTS with UTF-8 encoding.
2. **Primary Keys**: UUID (`uuid_generate_v4()` or application-generated UUIDv7) for uniform distributed generation, security against enumeration attacks, and multi-tenant portability.
3. **Multi-Tenancy**: Logical tenant isolation via `tenant_id UUID NOT NULL` indexed across all tenant-scoped tables, reinforced with Hibernate multitenancy filters.
4. **Optimistic Locking**: Every mutable aggregate root contains a `version BIGINT NOT NULL DEFAULT 0` column.
5. **Auditing**: Standard columns `created_at TIMESTAMPTZ NOT NULL`, `updated_at TIMESTAMPTZ NOT NULL`, `created_by VARCHAR(100)`, and `updated_by VARCHAR(100)`.
6. **No Cross-Schema Hard Foreign Keys**: Tables belong to their respective Bounded Context schemas. Cross-context references are maintained by ID only to preserve microservice independence.

---

## 2. Entity-Relationship Diagram (Mermaid)

```mermaid
erDiagram
    iam_tenants ||--o{ iam_users : has
    iam_users ||--o{ iam_refresh_tokens : owns
    iam_tenants ||--o{ registry_integrations : configures
    registry_websites ||--o{ registry_integrations : connects
    registry_integrations ||--o{ registry_vault_secrets : secures

    iam_users ||--o{ testops_sessions : executes
    registry_integrations ||--o{ testops_sessions : uses
    testops_sessions ||--o{ testops_questions : contains
    testops_questions ||--o{ ai_analyses : analyzes
    testops_sessions ||--o{ testops_results : produces

    iam_tenants ||--o{ billing_subscriptions : subscribes
    billing_subscriptions ||--o{ billing_invoices : generates

    iam_tenants ||--o{ ai_token_usages : records
    iam_tenants ||--o{ audit_logs : generates

    iam_tenants {
        UUID id PK
        VARCHAR name
        VARCHAR slug UK
        VARCHAR status
        VARCHAR tier
        BIGINT version
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    iam_users {
        UUID id PK
        UUID tenant_id FK
        VARCHAR email UK
        VARCHAR password_hash
        VARCHAR full_name
        VARCHAR role
        VARCHAR status
        VARCHAR telegram_chat_id
        BOOLEAN mfa_enabled
        VARCHAR mfa_secret
        BIGINT version
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    iam_refresh_tokens {
        UUID id PK
        UUID user_id FK
        VARCHAR token_hash UK
        BOOLEAN revoked
        TIMESTAMPTZ expires_at
        TIMESTAMPTZ created_at
    }

    registry_websites {
        UUID id PK
        VARCHAR domain_name UK
        VARCHAR display_name
        VARCHAR base_url
        VARCHAR platform_type
        VARCHAR compliance_status
        BOOLEAN ssrf_verified
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    registry_integrations {
        UUID id PK
        UUID tenant_id
        UUID website_id
        VARCHAR auth_type
        VARCHAR status
        JSONB config_json
        BIGINT version
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    registry_vault_secrets {
        UUID id PK
        UUID integration_id
        TEXT encrypted_payload
        VARCHAR iv
        INTEGER key_version
        TIMESTAMPTZ expires_at
        TIMESTAMPTZ created_at
    }

    testops_sessions {
        UUID id PK
        UUID tenant_id
        UUID user_id
        UUID integration_id
        VARCHAR state
        VARCHAR practice_mode
        VARCHAR target_url
        INTEGER total_questions
        INTEGER answered_questions
        BIGINT version
        TIMESTAMPTZ started_at
        TIMESTAMPTZ finished_at
    }

    testops_questions {
        UUID id PK
        UUID session_id
        INTEGER sequence_number
        TEXT question_text
        VARCHAR question_type
        JSONB options_json
        TEXT image_url
        VARCHAR user_answer
        BOOLEAN is_correct
        TIMESTAMPTZ created_at
    }

    ai_analyses {
        UUID id PK
        UUID question_id
        VARCHAR provider
        VARCHAR model
        TEXT reasoning
        VARCHAR predicted_answer
        NUMERIC confidence_score
        INTEGER prompt_tokens
        INTEGER completion_tokens
        INTEGER latency_ms
        TIMESTAMPTZ created_at
    }

    testops_results {
        UUID id PK
        UUID session_id UK
        UUID tenant_id
        UUID user_id
        INTEGER total_score
        NUMERIC accuracy_percentage
        INTEGER time_spent_seconds
        JSONB summary_report_json
        TIMESTAMPTZ created_at
    }

    ai_token_usages {
        UUID id PK
        UUID tenant_id
        UUID user_id
        VARCHAR provider
        VARCHAR model
        INTEGER input_tokens
        INTEGER output_tokens
        NUMERIC estimated_cost_usd
        TIMESTAMPTZ recorded_at
    }

    billing_subscriptions {
        UUID id PK
        UUID tenant_id UK
        VARCHAR plan_tier
        VARCHAR status
        INTEGER monthly_token_limit
        INTEGER used_tokens
        INTEGER concurrent_sessions_limit
        TIMESTAMPTZ billing_cycle_start
        TIMESTAMPTZ billing_cycle_end
    }

    audit_logs {
        UUID id PK
        UUID tenant_id
        UUID actor_id
        VARCHAR action_type
        VARCHAR resource_type
        VARCHAR resource_id
        VARCHAR client_ip
        VARCHAR user_agent
        JSONB details_json
        TIMESTAMPTZ timestamp
    }

    outbox_events {
        UUID id PK
        VARCHAR aggregate_type
        VARCHAR aggregate_id
        VARCHAR event_type
        JSONB payload
        BOOLEAN processed
        TIMESTAMPTZ scheduled_at
        TIMESTAMPTZ created_at
    }
```

---

## 3. Database Indexes & Optimization Rules

1. `CREATE INDEX idx_users_tenant_email ON iam_users(tenant_id, email);`
2. `CREATE INDEX idx_refresh_tokens_hash ON iam_refresh_tokens(token_hash) WHERE revoked = false;`
3. `CREATE INDEX idx_sessions_tenant_user_state ON testops_sessions(tenant_id, user_id, state);`
4. `CREATE INDEX idx_questions_session ON testops_questions(session_id, sequence_number);`
5. `CREATE INDEX idx_analyses_question ON ai_analyses(question_id);`
6. `CREATE INDEX idx_audit_tenant_time ON audit_logs(tenant_id, timestamp DESC);`
7. `CREATE INDEX idx_outbox_unprocessed ON outbox_events(scheduled_at) WHERE processed = false;`
