# AI Test Platform — Security Architecture & Threat Model

**Security Model:** Defense-in-depth, Zero-Trust Architecture  
**Standards Compliance:** OWASP Top 10 (2021), RFC 1918, RFC 6749 (OAuth2), CWE-918 (SSRF)

---

## 1. Threat Vectors & Defense Matrix

| Threat Category | Potential Attack | Platform Defense Implementation |
|:---|:---|:---|
| **SSRF (Server-Side Request Forgery)** | User submits `http://169.254.169.254` (cloud metadata) or `http://localhost:5433` | `DefaultSsrfGuardAdapter` resolves DNS and verifies that all returned IPs are not link-local, loopback, or site-local RFC 1918 subnets. |
| **Proctored Examination Abuse** | User attempts to automate high-stakes certification exam | Hardcoded domain blacklist in `SsrfGuardPort` (`proctorio.com`, `respondus.com`, `honorlock.com`, `pearsonvue.com`, `ets.org`). Request rejected with HTTP 403. |
| **Credential Theft in Vault** | Third-party website credentials intercepted | AES-256-GCM encryption with randomized IVs managed by `AesGcmCredentialVaultAdapter`. |
| **Cross-Tenant Data Leakage** | Tenant A queries Tenant B's discovery or solving sessions | Every JPA query and domain repository enforces `tenant_id` equality check (`WHERE tenant_id = :tenantId`). |
| **Brute Force & Credential Stuffing** | Automated login attacks on `/api/v1/auth/login` | Argon2id password hashing + Redis distributed rate limiting on IP and email. |
| **Broken Object Level Auth (BOLA)** | User queries `/api/v1/url-tests/sessions/{id}` belonging to another user | Controller checks `SecurityUtils.getRequiredCurrentUser().tenantId()`. |

---

## 2. Authentication Architecture

- **Local Authentication:** Argon2id hash verification with secure salt. Returns JWT access token (15m expiry) and refresh token (7d expiry).
- **Google OAuth 2.0:** Verified using Google Public Keys (`GoogleIdTokenVerifier`). If email exists, securely links Google identity; if new, provisions tenant and user account.
- **SSO Target Authentication:** Users interacting with external sites via Playwright can securely provide session cookies or bearer tokens which are injected only inside the ephemeral browser instance.

---

## 3. Production Readiness Status

- All endpoints under `/api/v1/**` (except `/api/v1/auth/**`) require valid JWT authentication.
- All static evidence files under `/artifacts/**` are read-only and mapped securely via `WebMvcConfig`.
- ProblemDetail JSON format complies with RFC 7807 (`application/problem+json`).
