# Email & SMTP Service Configuration Guide

## 1. Overview

The Enterprise AI Test Automation Platform uses transactional email services for:
- **Email Verification**: Anti-phishing signed link on new account registration.
- **Self-Service Password Reset**: 15-minute cryptographically secure SHA-256 token delivery.
- **Account Enumeration Protection**: Endpoints return identical success messaging regardless of user existence.
- **Session Revocation**: Password update automatically invalidates active device sessions.

---

## 2. SMTP Integration Configuration

The platform supports any standard RFC 5321 / RFC 5322 compliant SMTP provider (Google Workspace SMTP, SendGrid, Amazon SES, Mailgun, Postmark):

### Environment Variables
Configure the following in `.env` or container environment:

```env
# Spring Mail SMTP Settings
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=notifications@aitestops.io
SPRING_MAIL_PASSWORD=your-app-specific-password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_REQUIRED=true

# Sender Identity
APP_MAIL_FROM_ADDRESS=notifications@aitestops.io
APP_MAIL_FROM_NAME="AiTestOps Security"
APP_FRONTEND_URL=https://app.aitestops.io
```

---

## 3. Security Token Architecture

1. **Token Generation**:
   - 256-bit secure random token generated via `SecureRandom`.
   - Stored in database table `iam_security_tokens` using irreversible SHA-256 hash.
   - Raw token is sent ONLY via email directly to the verified email owner.
2. **TTL & Expiration**:
   - `PASSWORD_RESET`: 15 minutes TTL.
   - `EMAIL_VERIFICATION`: 24 hours TTL.
3. **Usage Enforcement**:
   - Single-use token: marked `used = true` immediately upon verification.
   - Any replay attempt is rejected.
   - Updating password purges all active refresh tokens for the user across all devices.
