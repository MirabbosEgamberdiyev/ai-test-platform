# Enterprise AI Test Platform — Functional Audit

## 1. Functional Scope Overview

The platform supports two complementary core workflows:
1. **Interactive Educational Testing & Practice Engine**: Students and educators manage questions, take server-timed tests with autosave, recover from refreshes, and receive deterministic score evaluations.
2. **Universal AI Web Testing Automation Engine**: Testers specify website URLs and natural-language goals; the AI discovers DOM elements, form structures, and generates/executes Playwright browser test suites.

## 2. Feature Verification Matrix

| Module / Feature | Implemented | Verified | Status / Evidence |
| :--- | :---: | :---: | :--- |
| **User Registration (Tenant + Admin)** | YES | YES | Tested via `/api/v1/auth/register` (Returns 201 Created & JWT) |
| **Email/Password Login** | YES | YES | Tested via `/api/v1/auth/login` (Returns 200 OK & User Profile) |
| **JWT Token Refresh & Rotation** | YES | YES | Tested via `/api/v1/auth/refresh` (Invalidates old refresh token) |
| **Telegram Account Linking** | YES | YES | Tested via `/api/v1/auth/link-telegram` (Associates chat ID) |
| **Google OAuth 2.0 Registration/Login** | PARTIAL | IN PROGRESS | Needs server-side ID token verification & frontend button |
| **Email Verification & Password Reset** | PARTIAL | IN PROGRESS | Needs dedicated token entity and reset endpoints |
| **Question Bank Management** | YES | YES | Tested via `TestQuestion` domain model and repository |
| **Practice / Exam Session Creation** | YES | YES | Tested via `/api/v1/practice/start` |
| **Server-Authoritative Test Timer** | YES | YES | Validated `expiresAt` expiration check in domain |
| **Idempotent Answer Autosave** | YES | YES | Tested via `/api/v1/practice/answer` |
| **Session Refresh & Crash Recovery** | YES | YES | Preserves submitted answers in `practice_session_answers` |
| **Deterministic Scoring (`BigDecimal`)** | YES | YES | Tested: strict numeric calculations, zero AI hallucination |
| **Universal Web Test Discovery** | YES | YES | Tested via `/api/v1/web-projects/{id}/discover` |
| **Natural Language AI Test Plan** | YES | YES | Tested via `/api/v1/web-tests/plans` (Gemini + OpenAI) |
| **Playwright Isolated Browser Runner** | YES | YES | Tested live against `https://web.pravaonline.uz/me` (7/7 passed) |
| **Google/Telegram SSO Session Injection** | YES | YES | Tested: cookies & headers injected into Playwright context |
| **Executive Test Reports (HTML/CSV/JSON)**| YES | YES | Exported to `artifacts/reports/` with metrics and charts |
| **Four-Language Support (`uzl`, `uzc`, `ru`, `en`)** | YES | YES | Verified across UI, error messages, and AI outputs |

## 3. Functional Gaps Identified

### [GAP-FUNC-01] [HIGH] Google OAuth 2.0 Frontend & Backend Flow
- **Description**: Frontend `/login` and `/register` need the "Continue with Google" button, communicating with `POST /api/v1/auth/oauth/google` on the backend with authorization code or credential ID token.
- **Action**: Implement Google token decoder, user auto-provisioning, and account linking in `platform-iam`.

### [GAP-FUNC-02] [HIGH] Self-Service Password Reset & Email Verification
- **Description**: Add `/forgot-password`, `/reset-password`, and `/verify-email` screens in Next.js, and back them with transactional email tokens.
- **Action**: Add `PasswordResetController` and `EmailVerificationController` with token generation, hashing, and expiration checks.

### [GAP-FUNC-03] [MEDIUM] Question Bank Direct Authoring UI
- **Description**: Provide intuitive teacher/admin screens to add, edit, and categorize custom multiple-choice, single-choice, and numeric questions.
- **Action**: Connect frontend `/dashboard/test-cases` and `/dashboard/practice` to interactive question authoring forms.
