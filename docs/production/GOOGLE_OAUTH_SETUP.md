# Google OAuth 2.0 & SSO Configuration Guide

## 1. Overview & Architecture

The Enterprise AI Test Automation Platform supports Google OAuth 2.0 / OpenID Connect (OIDC) authentication for:
1. **Platform User Identity**: Direct user sign-in and registration via Google Accounts.
2. **Account Linking**: Associating a Google identity with an existing email/password account.
3. **Universal Web Testing with SSO**: Simulating and testing external applications where end-users authenticate via Google or Telegram SSO (e.g. `web.pravaonline.uz`).

---

## 2. Platform Authentication (OIDC ID Token Validation)

### Architecture
- **Protocol**: OpenID Connect (OIDC) / OAuth 2.0
- **Validation**: Cryptographic signature validation via Google Public Keys JWK endpoint (`https://www.googleapis.com/oauth2/v3/certs`) or Google ID Token API client.
- **Claims Verification**:
  - `iss`: Must equal `accounts.google.com` or `https://accounts.google.com`.
  - `aud`: Must match configured `GOOGLE_CLIENT_ID`.
  - `exp`: Token must be unexpired.
  - `email_verified`: Must be `true`.
- **Auto-Provisioning**:
  - If a user signs in with Google and an existing account matches `email`, the account is automatically linked with `googleId` and marked `emailVerified=true`.
  - If no account exists, a new tenant and user account is provisioned with role `ROLE_TENANT_ADMIN`.

### Status Notice: External Configuration Dependency
> [!IMPORTANT]
> **Status**: `BLOCKED_BY_EXTERNAL_CONFIGURATION`
>
> The Google OAuth 2.0 verification pipeline, DTOs, domain models, database schema (`iam_users.google_id`, `iam_users.auth_provider`), and frontend UI buttons are fully implemented in code.
>
> To activate live Google OAuth in production, the system administrator must supply real Google Cloud Console OAuth 2.0 Client credentials in the production `.env` file as detailed below.

---

## 3. Google Cloud Console Setup Steps

1. Navigate to [Google Cloud Console](https://console.cloud.google.com/).
2. Create or select a project (e.g. `ai-testops-platform`).
3. Configure the **OAuth Consent Screen**:
   - User Type: `External` (or `Internal` for Google Workspace organizations).
   - App Name: `AiTestOps Enterprise Platform`.
   - User support email & Developer contact info.
   - Scopes: `openid`, `profile`, `email`.
4. Create Credentials:
   - Navigate to **APIs & Services** > **Credentials**.
   - Click **Create Credentials** > **OAuth client ID**.
   - Application type: **Web application**.
   - Authorized JavaScript origins:
     - `http://localhost:3000` (development)
     - `https://app.aitestops.io` (production)
   - Authorized redirect URIs:
     - `http://localhost:3000/login`
     - `https://app.aitestops.io/login`
5. Copy the generated **Client ID** and **Client Secret**.

---

## 4. Environment Variables Configuration

Add the following variables to `.env` or deployment secrets:

```env
# Backend Google OAuth
APP_SECURITY_OAUTH2_GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
APP_SECURITY_OAUTH2_GOOGLE_CLIENT_SECRET=GOCSPX-your-google-client-secret

# Frontend Google OAuth Client ID
NEXT_PUBLIC_GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
```

---

## 5. Automated Web Testing with Google & Telegram SSO

When testing target websites (e.g. `web.pravaonline.uz`) that protect routes like `/me` or `/tickets` behind Google or Telegram SSO:
1. **SSO Session Cookie / Storage Mode (Recommended)**:
   - Provide the authorized session cookie or JWT in the **Test Account Credentials** step.
   - The Playwright execution engine automatically injects these cookies and local storage tokens into the browser sandbox before navigating to protected endpoints.
2. **Bearer Token Mode**:
   - Provide the API Authorization Bearer token.
   - The engine automatically attaches the token to all subsequent HTTP requests and browser headers.
3. **Automated Sandbox SSO Navigation**:
   - For authorized testing environments with test Google/Telegram credentials, the AI automation engine navigates to the OAuth provider, satisfies test credentials, and validates the redirected authenticated state.
