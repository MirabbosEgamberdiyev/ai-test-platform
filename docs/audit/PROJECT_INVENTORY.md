# PROJECT INVENTORY — ENTERPRISE AI TEST AUTOMATION PLATFORM
**Audit Date:** November 9, 2026  
**Auditor:** Principal Software Architect & QA Engineering Audit Team  
**Repository Path:** `d:\My-project\ai-test-platform`

---

## 1. Directory Structure

```
d:\My-project\ai-test-platform/
├── .github/
│   └── workflows/
│       └── ci.yml                   # GitHub Actions CI pipeline (Build, Unit, ArchUnit, Docker)
├── backend/
│   ├── pom.xml                      # Maven parent multi-module POM (Java 21, Spring Boot 3.3.5)
│   ├── platform-common/             # Shared primitives, Result<T,E>, strongly-typed IDs, GlobalExceptionHandler
│   ├── platform-iam/                # Identity & Access Management (Tenant, User, JJWT 0.12.5, BCrypt, RBAC)
│   ├── platform-registry/           # Website Registry, SSRF Guard (RFC1918/Metadata/Blacklist), AES-256-GCM Vault
│   ├── platform-ai/                 # Multimodal AI Engine (Gemini 1.5 primary, OpenAI fallback, TokenBudget)
│   ├── platform-automation/         # Playwright isolated browser workers, ephemeral sandboxes, DOM extraction
│   ├── platform-testops/            # Practice sessions, test questions, outbox event relay, analytics
│   ├── platform-billing/            # Tenant subscriptions, tiers, quotas, token limits
│   ├── platform-notification/       # Telegram Bot SDK adapter, webhook endpoints, alert routing
│   ├── platform-audit/              # Immutable audit trail, compliance logs
│   └── platform-app/                # Executable Spring Boot application, Flyway V1 schema, ArchUnit rules
├── frontend/
│   ├── package.json                 # Next.js 14.2.15, React 18.3, TanStack Query, Tailwind CSS, Lucide icons
│   ├── tsconfig.json                # TypeScript compiler config (strict mode)
│   ├── tailwind.config.ts           # Design tokens, slate/indigo color palettes
│   ├── postcss.config.js            # PostCSS tailwind & autoprefixer
│   ├── next.config.mjs              # Next.js config with reverse proxy to backend
│   └── src/
│       ├── app/                     # Next.js App Router (page.tsx, login, register, dashboard routes)
│       ├── components/              # UI components (Button, Card, Input, Badge, Navbar, Sidebar, Footer)
│       └── lib/                     # API client, token management, utils
├── infrastructure/
│   ├── docker/
│   │   ├── docker-compose.yml       # Postgres 16, Redis 7, RabbitMQ 3.13, Prometheus, Grafana, Loki, OTel
│   │   ├── prometheus.yml           # Prometheus scrape configuration
│   │   ├── otel-collector-config.yaml # OpenTelemetry Collector pipelines
│   │   └── loki-config.yaml         # Grafana Loki logging config
│   ├── k8s/                         # Kubernetes manifests (namespace, deployments, ingress)
│   └── terraform/                   # IaC modules (main.tf, variables.tf, outputs.tf)
├── docs/
│   ├── architecture/                # Blueprints, 55 Principles compliance, C4 models, ADRs, Threat model
│   ├── audit/                       # Phase 1-13 Audit reports
│   └── final/                       # Phase 16 Production readiness final reports
├── artifacts/
│   └── responsive/                  # Viewport screenshots and responsive verification evidence
├── .env.example                     # Environment configuration template
└── .gitignore                       # Repository ignore rules
```

---

## 2. Backend Modules Inventory

| Module | Responsibilities | Key Ports / Interfaces | Key Adapters / Implementations | Test Coverage |
|---|---|---|---|---|
| `platform-common` | Shared domain primitives, Result monad, strong IDs, RFC 7807 handler | `Result<T,E>`, `TenantId`, `UserId` | `GlobalExceptionHandler`, `CorrelationIdFilter` | 4 unit tests (PASS) |
| `platform-iam` | Authentication, Tenant isolation, User registration, JWT rotation | `UserRepositoryPort`, `TenantRepositoryPort`, `JwtTokenProviderPort` | `JwtTokenProviderAdapter`, `BCryptPasswordEncoderAdapter`, `SecurityFilterChain` | 4 unit tests (PASS) |
| `platform-registry` | Website Registry, SSRF validation, AES-256-GCM Vault | `SsrfGuardPort`, `CredentialVaultPort`, `RegisteredWebsiteRepositoryPort` | `DefaultSsrfGuardAdapter`, `AesGcmCredentialVaultAdapter`, `WebsiteRegistryController` | 6 unit tests (PASS) |
| `platform-ai` | Multimodal AI inference, routing engine, cost & budget tracking | `AIModelClientPort`, `TokenTrackerPort` | `AIRoutingEngineService`, `GeminiAIClientAdapter`, `OpenAIAIClientAdapter` | 2 unit tests (PASS) |
| `platform-automation` | Playwright isolated contexts, safe DOM extraction | `BrowserWorkerPort` | `PlaywrightBrowserWorkerAdapter`, `BrowserAutomationService` | 3 unit tests (PASS) |
| `platform-testops` | Practice sessions, answer scoring, transactional outbox | `PracticeSessionRepositoryPort`, `OutboxEventRepositoryPort` | `PracticeOrchestrationService`, `PracticeSessionController`, `AnalyticsController` | 4 unit tests (PASS) |
| `platform-billing` | Tenant subscriptions, token limits, quota enforcement | `SubscriptionRepositoryPort` | `SubscriptionJpaEntity`, `SubscriptionService` | Module compiled & verified |
| `platform-notification`| Telegram Bot notifications, Webhook reception | `TelegramBotPort` | `TelegramBotClientAdapter`, `TelegramWebhookController` | Module compiled & verified |
| `platform-audit` | Audit records, compliance history | `AuditRepositoryPort` | `AuditService`, `AuditJpaEntity` | Module compiled & verified |
| `platform-app` | Spring Boot bootstrap, Flyway V1 migration, ArchUnit fitness | Clean architecture validation rules | `EnterpriseAiTestPlatformApplication`, `ArchitectureComplianceTest` | 5 unit/arch tests (PASS) |

---

## 3. Frontend Inventory

| Route | Purpose | Key Components Used | Backend Integration Status |
|---|---|---|---|
| `/` | Landing page, Hero, Principles showcase, Live simulator | `Navbar`, `Footer`, `Card`, `Badge`, `Button` | Interactive preview with live AI reasoning simulation |
| `/login` | Tenant login with email/password and Tenant ID | `Input`, `Button`, `Card`, `authApi.login` | Full JWT storage in localStorage with Bearer header |
| `/register` | Organization & Admin user registration | `Input`, `Button`, `Card`, `authApi.register` | Direct creation of Tenant & Admin user |
| `/dashboard` | Tenant overview, token meters, quick actions | `Sidebar`, `Card`, `Badge`, metrics grid | Integrated with session telemetry |
| `/dashboard/practice` | Live interactive AI practice studio | Question renderer, AI reasoning card, feedback toast | Full `analyzeQuestion`, `submitAnswer`, `finishSession` |
| `/dashboard/integrations`| Website Registry with SSRF Guard indicators | `Card`, `Input`, form modal, status table | `listWebsites`, `registerWebsite` with SSRF verification |
| `/dashboard/analytics` | Category mastery breakdown, session logs | `Card`, `Badge`, accuracy progress meters | `getSessionResult`, historical breakdown |
| `/robots.txt` & `/sitemap.xml` | SEO crawlers and indexation | Next.js Metadata Route handlers | Production verified |

---

## 4. Infrastructure & Telemetry Inventory

- **PostgreSQL 16:** Running in Docker container `ai_platform_postgres` on mapped host port `5433:5432`. Schema version 1 managed by Flyway (`V1__init_platform_schema.sql`).
- **Redis 7:** Running in Docker container `ai_platform_redis` on port `6379` with password authentication.
- **RabbitMQ 3.13:** Running in Docker container `ai_platform_rabbitmq` on port `5672` (AMQP) and `15672` (Management UI).
- **OpenTelemetry Collector:** Running on ports `4317` (gRPC) and `4318` (HTTP).
- **Prometheus:** Running on port `9090` scraping `/actuator/prometheus`.
- **Grafana:** Running on port `3001` with provisioned Prometheus & Loki data sources.
- **Grafana Loki:** Running on port `3100` for log aggregation.
