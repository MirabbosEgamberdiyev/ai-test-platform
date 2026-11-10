# Production Architecture Specification

## 1. System Overview

The Enterprise AI Test Automation Platform is an enterprise SaaS application architected under strict Clean Architecture and Domain-Driven Design (DDD) principles. It combines:
1. **Universal AI Web Testing Engine**: Natural-language-driven automated testing of any external website (SPA, SSR, CMS, legacy or modern) via isolated ephemeral Playwright browser sandboxes.
2. **Interactive Exam & Practice Engine**: Automated question extraction, AI-powered multi-model evaluation, server-side timer, idempotent autosave, and deterministic scoring.
3. **Multi-Tenant Identity & Access Management (IAM)**: Strict tenant row-level security, BCrypt credential hashing, Google OAuth 2.0 / OIDC authentication, SHA-256 timed security tokens, and device session revocation.

---

## 2. Hexagonal Modular Architecture

```
                    ┌────────────────────────────┐
                    │      Next.js Frontend      │
                    │   (React 18 / Tailwind)    │
                    └─────────────┬──────────────┘
                                  │ HTTPS / REST (4 Locales)
                    ┌─────────────▼──────────────┐
                    │     Platform App Gate      │
                    │      (Spring Boot 3)       │
                    └──────┬───────┬───────┬─────┘
           ┌───────────────┘       │       └──────────────┐
           ▼                       ▼                      ▼
┌───────────────────┐    ┌───────────────────┐   ┌───────────────────┐
│   Platform IAM    │    │ Platform TestOps  │   │ Platform Registry │
│ Google OIDC, JWT  │    │  Plans, Sessions, │   │ SSRF Guard, Safe  │
│  Security Tokens  │    │ Exam Scorer (DDD) │   │ Target Discovery  │
└──────────┬────────┘    └─────────┬─────────┘   └─────────┬─────────┘
           │                       │                       │
           ▼                       ▼                       ▼
┌───────────────────┐    ┌───────────────────┐   ┌───────────────────┐
│    Platform AI    │    │Platform Automation│   │  Platform Audit   │
│ Gemini 1.5 Pro/   │    │Playwright Sandbox │   │ Immutable Events  │
│ Flash + OpenAI    │    │ SSO Cookie Inject │   │  Compliance Trail │
└───────────────────┘    └───────────────────┘   └───────────────────┘
```

---

## 3. High-Integrity Engineering Constraints

1. **Deterministic Test Scoring**:
   - Official test scores and accuracy percentages are computed exclusively via deterministic server-side arithmetic (`BigDecimal` with `RoundingMode.HALF_UP`).
   - AI is strictly advisory (confidence rating, explanations, heuristics) and NEVER directly alters official numeric test scores.
2. **Student Answer Protection**:
   - When active practice or exam sessions are retrieved, `isCorrect` and detailed rationales are scrubbed to prevent client-side DOM or network inspection cheating.
3. **SSRF Guard & Network Isolation**:
   - Every target website URL submitted is validated against private/loopback/cloud-metadata IP ranges (`127.0.0.1`, `10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`, `169.254.169.254`).
   - Only publicly resolvable DNS records with HTTP/HTTPS schemes are permitted into browser sandboxes.
4. **Idempotent Autosave & Server-Side Timer**:
   - Answer submissions and autosaves include idempotency keys (`sessionId:sequenceNumber`).
   - Exam durations are enforced via server-side database timestamps (`started_at`, `expires_at`).
