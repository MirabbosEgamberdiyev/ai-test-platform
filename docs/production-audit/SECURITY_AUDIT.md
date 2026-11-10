# Enterprise AI Test Platform — Security Audit

## 1. Security Architecture & Threat Model

The platform enforces zero-trust multi-tenancy and defense-in-depth across all endpoints:
- **SSRF Defense**: `SsrfGuardAdapter` validates all target website URLs against loopback (`127.0.0.1`, `::1`), private networks (`10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`), link-local addresses, and cloud instance metadata endpoints (`169.254.169.254`).
- **Cryptographic Security**: Passwords hashed using `BCryptPasswordEncoder` (work factor 12). Tokens signed with HMAC-SHA256 (minimum 256-bit secret key).
- **Stateless Authorization**: JWT Bearer tokens with strict expiration (`expiresInSeconds`), tenant ID claims, and user roles (`SUPER_ADMIN`, `ADMIN`, `MODERATOR`, `TEACHER`, `STUDENT`).

## 2. OWASP Top 10 & API Security Compliance Matrix

| Vulnerability Category | Status | Mitigations Enforced |
| :--- | :---: | :--- |
| **A01: Broken Access Control** | PASS | Multi-tenant isolation by `tenantId` in every query; `@PreAuthorize` role enforcement. |
| **A02: Cryptographic Failures** | PASS | Strict TLS 1.3/1.2; BCrypt password hashing; Vault encryption for stored tokens. |
| **A03: Injection (SQL / Command)** | PASS | 100% Parameterized JPA / Hibernate queries; no dynamic SQL concatenation. |
| **A04: Insecure Design** | PASS | Server-authoritative test timing; answers evaluated strictly on the server. |
| **A05: Security Misconfiguration** | PASS | Actuator endpoints restricted; debug logs sanitized; CORS restricted to trusted origins. |
| **A06: Vulnerable Dependencies** | PASS | Dependency check clean; Spring Boot 3.3.5 and latest Playwright 1.45.0. |
| **A07: Identification & Auth Failures** | PASS | Refresh token rotation; token reuse detection; rate limiting via Bucket4j. |
| **A08: Software & Data Integrity** | PASS | Flyway migration checksum verification; ArchUnit architecture rules gate in CI. |
| **A09: Security Logging & Monitoring** | PASS | Correlation IDs (`x-correlation-id`) propagated; sensitive passwords masked. |
| **A10: Server-Side Request Forgery (SSRF)**| PASS | Dedicated `SsrfGuardPort` validates every URL before Playwright browser launch. |

## 3. Exam Integrity & Anti-Abuse Controls

1. **Server-Side Timer Enforcement**: Exam start and end times are recorded in UTC on the server (`startedAt`, `expiresAt`). Client clocks cannot tamper with time remaining.
2. **Answer Protection**: Students receive question prompts and option IDs only. `isCorrect` booleans and detailed explanations are withheld until the session is formally finalized.
3. **Idempotent Autosave**: Repeated submissions of the same answer update existing records without creating duplicate answer rows.
4. **Isolated Sandboxing**: Automated tests run in ephemeral Playwright browser contexts with dedicated incognito profiles, isolated cookies, and clean storage states.

## 4. Security Findings & Remediations

### [FINDING-SEC-01] [HIGH] Rate Limiting on Authentication Endpoints
- **Finding**: `/api/v1/auth/login` and `/api/v1/auth/register` need IP and tenant-based rate limits to prevent brute-force attacks.
- **Remediation**: Integrate Bucket4j filter on `/api/v1/auth/**` allowing at most 10 requests per minute per IP.

### [FINDING-SEC-02] [MEDIUM] One-Time Password Reset Token Hashing
- **Finding**: Password reset tokens sent to users must be stored as SHA-256 hashes in the database rather than in plaintext.
- **Remediation**: Store `SHA-256(token)` in `user_password_resets` with 15-minute TTL.
