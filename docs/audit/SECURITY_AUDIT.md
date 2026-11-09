# SECURITY AUDIT & THREAT MODEL — UNIVERSAL AI WEB TESTING PLATFORM
**Audit Date:** November 9, 2026  
**Auditor:** Cybersecurity & Infrastructure Engineering Team  
**Standard:** OWASP Top 10 2021, ASVS L2, CWE-918 (SSRF Mitigation)

---

## 1. Executive Summary
The platform executes real browser instances against user-provided external target URLs. This operational model introduces unique threat vectors:
1. **Server-Side Request Forgery (SSRF) & Internal Network Pivoting:** Malicious users directing browser workers to probe internal cluster IPs (`10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`, `127.0.0.1`, `169.254.169.254`).
2. **Credential Theft & Secret Leaks:** Insecure storage or exposure of authorized test account passwords.
3. **Cross-Tenant Data Exposure:** Leaking test reports, screenshots, or credentials between organizations.
4. **Denial-of-Service / Resource Exhaustion:** Infinite browser loops, heavy parallel load, or huge DOM allocations.
5. **Malicious Script Injection via Target Websites:** Stored XSS via scraped web content.

All controls have been audited and designed into the architecture.

---

## 2. Threat Mitigation Matrix

| Threat ID | Threat Description | Severity | Architectural Mitigation | Status |
|---|---|---|---|---|
| **TH-01** | **SSRF against Cloud Metadata / Localhost** | `CRITICAL` | `DefaultSsrfGuardAdapter` blocks `169.254.169.254`, loopbacks, RFC 1918 private subnets, link-local addresses, and non-HTTP(S) schemes. DNS pre-resolution verifies resolved IPs before Playwright navigation. | `VERIFIED` |
| **TH-02** | **Test Account Credential Exposure** | `HIGH` | `AesGcmCredentialVaultAdapter` encrypts credentials with AES-256-GCM. Frontend uses password masking. Browser execution fills passwords without writing them to execution logs or screenshots. | `VERIFIED` |
| **TH-03** | **Cross-Tenant Data Tampering** | `CRITICAL` | Row-Level Tenant Isolation: All JPA entities (`WebTestProject`, `WebTestPlan`, `WebTestRun`, `Defect`, `WebTestReport`) enforce `tenantId` filtering in all queries and commands. | `VERIFIED` |
| **TH-04** | **Browser Context State Leakage** | `HIGH` | Ephemeral Playwright `BrowserContext` instantiated per test run and destroyed in a `finally` block. Cookies, local storage, indexedDB, and cache are purged immediately after execution. | `VERIFIED` |
| **TH-05** | **Execution Runaway / DoS** | `MEDIUM` | Hard timeout of 120 seconds per test run and 15 seconds per step. Concurrency limit enforced per tenant via Redis rate limiter. | `VERIFIED` |
| **TH-06** | **Target Website Authorization** | `HIGH` | Explicit user confirmation flag required before test execution. Disclaimer and audit log record user affirmation of authorization. Anti-bot and proctoring bypass features are strictly prohibited. | `VERIFIED` |

---

## 3. SSRF Protection Deep Dive
The `DefaultSsrfGuardAdapter` enforces the following rules on every target URL before browser worker navigation:
1. **Scheme Validation:** Only `http` and `https` schemes are accepted. Schemes such as `file://`, `ftp://`, `gopher://`, `javascript:`, `data:` are rejected with `ErrorCode.SSRF_TARGET_BLOCKED`.
2. **Host IP Blacklisting:**
   - Loopback: `127.0.0.0/8`, `::1`
   - RFC 1918 Private: `10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`
   - AWS / GCP / Azure Metadata: `169.254.169.254`, `fd00:ec2::254`
   - Carrier-grade NAT: `100.64.0.0/10`
   - Link-local: `169.254.0.0/16`
3. **Private Environment Exception Handling:** Only when explicitly marked as an authorized private test environment with administrative approval may private IP ranges be whitelisted.

---

## 4. Credential Vault Security
- Algorithms: **AES-256-GCM** with 96-bit random IV and 128-bit authentication tag.
- Encryption Keys: Injected via environment variable `APP_SECURITY_ENCRYPTION_KEY` (never committed to repository).
- In-Transit Protection: TLS 1.3 encryption across all client-to-backend and backend-to-database connections.
