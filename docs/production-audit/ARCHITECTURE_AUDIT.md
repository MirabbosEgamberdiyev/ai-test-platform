# Enterprise AI Test Platform — Architecture Audit

## 1. Architectural Topology

The platform is designed as a **Modular Monolith** adhering to **Hexagonal Architecture (Ports and Adapters)** and **Domain-Driven Design (DDD)**.
Each module contains distinct boundaries:
- `domain`: Pure business rules, entities, value objects, domain exceptions. Zero framework dependencies.
- `application`: Use cases, application services, input DTOs, output ports (interfaces).
- `infrastructure`: Database persistence adapters (JPA/Hibernate), external clients (Playwright, Gemini, OpenAI, BCrypt).
- `presentation`: REST controllers, request mapping, validation.

## 2. Multi-Tenancy Architecture

All operational data is partitioned by `TenantId (UUID)`.
- **Database level**: Every entity (`User`, `WebTestProject`, `WebTestPlan`, `PracticeSession`, `ExamSession`) maintains a foreign key to `tenants(id)`.
- **Security level**: `SecurityUtils.getRequiredCurrentUser()` extracts `tenantId` from the cryptographically verified JWT bearer token and injects it into application services.
- **Data isolation**: JPA queries strictly include `WHERE tenant_id = :tenantId`. Cross-tenant data leakage is structurally impossible.

## 3. Architecture Verification Matrix

| Architectural Principle | Status | Evidence / Implementation |
| :--- | :--- | :--- |
| **Separation of Concerns** | COMPLIANT | Presentation -> Application -> Domain <- Infrastructure |
| **Domain Purity** | COMPLIANT | Domain packages contain zero Spring, JPA, or Playwright imports |
| **ArchUnit Governance** | COMPLIANT | `CleanArchitectureGuardTest` passes on every build |
| **Outbox Pattern** | COMPLIANT | `OutboxEvent` table records events atomically with business transactions |
| **Idempotent Commands** | COMPLIANT | Commands use deterministic idempotency keys and unique constraints |
| **SSRF Defense-in-Depth** | COMPLIANT | `SsrfGuardAdapter` blocks private RFC 1918 IPs, loopback, cloud metadata |

## 4. Key Architecture Findings & Classification

### [FINDING-ARCH-01] [HIGH] Google OAuth 2.0 Integration Completeness
- **Root Cause**: The initial implementation lacked a server-side Google ID Token and Authorization Code verification adapter.
- **Impact**: Users could not sign in or link their Google accounts directly via standard OIDC tokens.
- **Remediation**: Implement `GoogleOAuthService` utilizing `GoogleIdTokenVerifier` (or Nimbus OIDC validator) to verify issuer, audience, signature, and email verification status.

### [FINDING-ARCH-02] [HIGH] Question Bank Answer Leakage Prevention
- **Root Cause**: Student-facing question endpoints must structurally omit `isCorrect` and detailed explanations during active sessions.
- **Impact**: Inspection of network responses in browser DevTools could expose answers prior to exam submission.
- **Remediation**: Create dedicated `StudentQuestionDto` that strips `isCorrect` and `explanation` until `ExamSessionStatus.SUBMITTED` or `COMPLETED`.

### [FINDING-ARCH-03] [MEDIUM] Password Reset & Email Verification Token Lifecycle
- **Root Cause**: Password reset tokens were stored in memory rather than persisted in the database with cryptographic hashes and expiration timestamps.
- **Impact**: Server restarts would invalidate in-flight password reset requests.
- **Remediation**: Add `user_tokens` table managed via Flyway migration with one-time use and TTL expiration.
