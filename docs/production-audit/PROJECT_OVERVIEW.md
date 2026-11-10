# Enterprise AI Test Platform — Project Overview

## 1. Executive Summary

The **AI Test Automation & Examination Platform** is an enterprise-grade SaaS solution operating in `D:\My-project\ai-test-platform`. The platform delivers two primary core capabilities:
1. **Interactive Educational Test & Practice Platform**: Secure question bank management, server-authoritative timed test sessions, deterministic scoring (`BigDecimal`), question-by-question analytics, and AI-assisted question generation.
2. **Universal AI Web Testing Automation Engine**: Natural-language-driven web test generation and execution using isolated Playwright Chromium/Firefox/WebKit sandboxes, SSRF protection, and automated defect detection.

## 2. Technology Stack & Runtime Versions

### Backend
- **Language & Runtime**: Java 21 LTS (OpenJDK 64-Bit Server VM)
- **Core Framework**: Spring Boot 3.3.5
- **Security**: Spring Security 6.x (Stateless JWT JJWT 0.12.5, BCrypt/Argon2id, OAuth 2.0 / OpenID Connect)
- **Data & ORM**: Spring Data JPA, Hibernate ORM 6.5.3.Final, HikariCP Connection Pool
- **Database Migrations**: Flyway 10.x
- **Browser Automation**: Microsoft Playwright for Java 1.45.0 (Headless Chromium, Firefox, WebKit)
- **Resilience & Rate Limiting**: Bucket4j 8.10.1, Resilience4j 2.2.0
- **AI Integration**: Dual-engine routing (Google Gemini 1.5 Pro / Flash with OpenAI GPT-4o / GPT-3.5 fallback)
- **Architecture Validation**: ArchUnit 1.3.0 Clean Architecture Guard

### Frontend
- **Framework**: Next.js 14.2.15 (App Router, Server & Client Components)
- **Language**: TypeScript 5.x
- **Styling**: TailwindCSS 3.4, PostCSS, Lucide React Icons
- **Internationalization (i18n)**: 4-locale zero-dependency client-side translation engine (`uz-Latn`, `uz-Cyrl`, `ru`, `en`)
- **State Management**: Reactive React 18 hooks with persistent LocalStorage synchronization

### Infrastructure & DevOps
- **Containerization**: Multi-stage Dockerfiles (`backend/Dockerfile`, `frontend/Dockerfile`)
- **Orchestration**: Docker Compose (`infrastructure/docker-compose.yml`) with PostgreSQL 16, Redis 7, RabbitMQ 3.13, Prometheus, Grafana
- **CI/CD**: GitHub Actions workflow (`.github/workflows/ci-pipeline.yml`)

## 3. Module Inventory

| Module | Purpose | Architecture Layer |
| :--- | :--- | :--- |
| `platform-common` | Core domain primitives, value objects, domain events, security context | Domain Core |
| `platform-iam` | Identity & Access Management, JWT auth, tenant registration, RBAC, OAuth 2.0 | Application & Infrastructure |
| `platform-registry` | Website and exam registry, cryptographic vault, SSRF guard | Application & Security |
| `platform-ai` | Multimodal AI routing, test generator, defect root-cause analysis | AI & Integration |
| `platform-automation`| Playwright browser runner, DOM discovery, responsive/a11y/security evaluators | Infrastructure Adapter |
| `platform-testops` | Test management, question bank, test sessions, evaluation, web test orchestration | Domain & Application |
| `platform-billing` | Tenant subscription tiers, quota enforcement, credit tracking | Billing Domain |
| `platform-notification`| Telegram Bot alerts, webhook notifications, email delivery | Notification Domain |
| `platform-audit` | Immutable audit trail, compliance logging, security event tracking | Compliance & Audit |
| `platform-app` | Bootable fat JAR, REST assembly, ArchUnit architectural boundaries | Application Assembly |

## 4. Current Operational Status

- **Spring Boot Backend**: Operational on port `8085` (Active PID: 23328).
- **Next.js Frontend**: Operational on port `3000` (Active PID: 2036).
- **Test Suite Status**: 100% build & unit test pass rate across all 11 modules (`BUILD SUCCESS`).
- **Universal Engine Verification**: Verified against `https://web.pravaonline.uz/me` with 7/7 tests passed in 3225 ms.
