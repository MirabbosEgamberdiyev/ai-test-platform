# ADR-006: Defense-in-Depth SSRF Guard and AES-256-GCM Vault

## Status
Accepted

## Context
Allowing users to connect external test portal URLs opens vectors for Server-Side Request Forgery (SSRF), such as port-scanning internal cloud networks or hitting instance metadata services (`http://169.254.169.254`). Additionally, stored user credentials for third-party platforms represent high-value targets.

## Decision
We enforce a two-tier defense:
1. **SSRF Guard (`SsrfGuardPort`)**:
   - Only HTTP and HTTPS schemes are allowed.
   - Target domain must be verified against an approved or admin-verified domain registry.
   - Synchronous DNS resolution resolves all IPv4 and IPv6 addresses.
   - Hardcoded blacklist blocks RFC 1918 private subnets (`10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`), loopback (`127.0.0.0/8`, `::1`), cloud metadata (`169.254.169.254`), and link-local (`fe80::/10`).
2. **Credential Vault (`AES-256-GCM`)**:
   - Credentials are encrypted at rest using AES-GCM (256-bit key) with random 96-bit IV per item.
   - Vault master key is injected via secure environment variables or HashiCorp Vault.
   - Plaintext passwords and OTPs are forbidden from all logs, metrics, and error payloads.

## Consequences
- **Positive**: Complete prevention of cloud metadata exfiltration and internal network scanning; compliance with enterprise data security audits.
- **Negative**: Users must use validated public URLs or request domain whitelisting for custom private school portals.
