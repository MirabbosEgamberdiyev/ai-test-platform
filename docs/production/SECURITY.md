# Production Security Architecture & Verification

## 1. Threat Model & Defense In Depth

The Enterprise AI Test Automation Platform enforces multi-layer defense across all threat boundaries:

### 1.1 Server-Side Request Forgery (SSRF) Defense
- **Target URL Validation**: Every submitted website URL is pre-screened before network interaction.
- **CIDR Blocklist**:
  - `127.0.0.0/8` (Loopback)
  - `10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16` (Private RFC 1918)
  - `169.254.169.254` (Cloud Metadata AWS/GCP/Azure)
  - `::1`, `fe80::/10` (IPv6 loopback/link-local)
- **Execution Sandboxing**: Playwright browser instances run in isolated worker processes with disabled local file scheme navigation (`file://`).

### 1.2 Authentication & Authorization Security
- **Multi-Tenant Row-Level Security**: Every database query is scoped by `tenant_id` from the cryptographically verified JWT `CurrentUser`.
- **Credential Storage**: Passwords hashed using Spring Security `BCryptPasswordEncoder` (work factor 12).
- **Session Revocation**:
  - Changing or resetting a password purges all active refresh tokens for the user across all devices.
  - An explicit `/api/v1/auth/logout-all` endpoint invalidates all sessions.
- **Account Enumeration Protection**: Password reset requests return identical success messaging regardless of email existence.
- **Timing & Replay Protection**:
  - Reset and verification tokens are stored as irreversible SHA-256 hashes in `iam_security_tokens`.
  - Tokens expire in 15 minutes and can only be redeemed once (`used_at IS NOT NULL`).

### 1.3 Exam & Student Answer Protection
- **Anti-Cheating Answer Scrubbing**:
  - During active exam or practice sessions, `isCorrect` fields and answer explanations are removed from all API payloads.
  - Answers are revealed only upon session submission and finalization.
- **Deterministic Server-Side Arithmetic**:
  - Official scores, correct count, and accuracy percentages are computed exclusively via deterministic server-side arithmetic (`BigDecimal` with `RoundingMode.HALF_UP`).
  - No client-side or AI-generated numeric score overriding is permitted.
