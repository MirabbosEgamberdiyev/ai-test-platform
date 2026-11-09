# Enterprise AI Test Automation Platform — Architecture Blueprint

## 1. Executive Summary & Vision

The **AI Test Automation Platform** is an enterprise-grade, multi-tenant SaaS engineered to empower learners, engineers, and educational institutions to practice, analyze, and automate interaction with test platforms ethically and securely.

The platform provides:
- Secure, authorized integration with user-registered testing and practice portals.
- AI-driven question comprehension, multi-modal OCR, reasoning, solution explanations, and difficulty scoring.
- Automated practice execution inside isolated, ephemeral Playwright browser sessions.
- Multi-channel interfaces: Modern Next.js Responsive Web Application and interactive Telegram Bot.
- Enterprise-grade resilience, telemetry, auditability, and bank-grade credential security (AES-256-GCM encryption, SSRF guards, strict domain allowlisting).

> [!IMPORTANT]
> **Ethical Boundary & Non-Circumvention Policy**:
> As mandated by engineering and legal specifications, this platform strictly forbids:
> 1. Automated impersonation during proctored/monitored high-stakes exams.
> 2. Bypassing anti-bot, captcha, or MFA protections without user explicit interaction.
> 3. Stealing or scraping unauthorized assessment pools.
> All browser interactions require explicit user authorization, and high-stakes/proctored exam platforms are blacklisted by default.

---

## 2. High-Level Architectural Topology

The system is structured as a **Modular Clean Architecture (Hexagonal / Ports & Adapters)**, built on Java 21 LTS and Spring Boot 3.3+. The design adheres strictly to Domain-Driven Design (DDD) bounded contexts, enabling zero-coupling modular deployment today and seamless extraction into standalone microservices when horizontal scale demands it.

```mermaid
graph TD
    subgraph Clients["Clients Layer"]
        WebApp["Next.js 14+ SPA / SSR Web Client"]
        TgBot["Telegram Bot Client (Webhook)"]
        ExternalAPI["Third-Party CI/CD & API Integrations"]
    end

    subgraph Edge["Edge & Security Layer"]
        CDN["Cloudflare CDN / DDoS Shield"]
        Nginx["Reverse Proxy / SSL Termination / Rate Limiter"]
        SSRFGuard["SSRF & Domain Egress Filter"]
    end

    subgraph BackendCore["Core Modular Monolith / Microservices (Java 21 Spring Boot)"]
        subgraph Ports["Driving Adapters (Presentation)"]
            RESTControllers["REST Controllers (OpenAPI 3.1)"]
            TelegramAdapter["Telegram Webhook Handler"]
            EventConsumers["RabbitMQ Event Listeners"]
        end

        subgraph ApplicationLayer["Application Use Cases & Orchestration"]
            CommandBus["Command Handlers (CQRS)"]
            QueryBus["Query Handlers (CQRS)"]
            SagaOrchestrator["Distributed Saga Orchestrators"]
            OutboxRelay["Transactional Outbox Relay"]
        end

        subgraph DomainLayer["Domain Layer (Pure Java, Zero Framework Dependencies)"]
            IAMDomain["IAM & Tenant Aggregates"]
            RegistryDomain["Website & Domain Aggregates"]
            TestOpsDomain["Test & Practice Session Aggregates"]
            AIDomain["AI Prompt & Analysis Aggregates"]
            BillingDomain["Subscription & Quota Aggregates"]
        end

        subgraph DrivenAdapters["Driven Adapters (Infrastructure)"]
            JPAAdapter["PostgreSQL 16 (Spring Data JPA / Flyway)"]
            RedisAdapter["Redis 7 (Distributed Cache & Redlock)"]
            RabbitMQAdapter["RabbitMQ 3.13 (Event Exchange & DLQ)"]
            AIAdapter["AI Engine (Gemini 2.5/Pro & OpenAI GPT-4o)"]
            BrowserAdapter["Playwright Browser Worker Service"]
            S3Adapter["S3 / MinIO Object Storage"]
            VaultAdapter["AES-256-GCM Credential Vault"]
        end
    end

    subgraph ObservabilityStack["Observability & Telemetry"]
        OTel["OpenTelemetry Collector (Traces & Metrics)"]
        Prometheus["Prometheus Time Series DB"]
        Grafana["Grafana Dashboards & Alertmanager"]
        Loki["Loki Distributed Log Aggregation"]
    end

    WebApp --> CDN
    TgBot --> Nginx
    ExternalAPI --> Nginx
    CDN --> Nginx
    Nginx --> RESTControllers
    Nginx --> TelegramAdapter

    RESTControllers --> CommandBus
    RESTControllers --> QueryBus
    TelegramAdapter --> CommandBus

    CommandBus --> DomainLayer
    QueryBus --> DomainLayer

    DomainLayer --> DrivenAdapters
    ApplicationLayer --> OutboxRelay
    OutboxRelay --> RabbitMQAdapter
    RabbitMQAdapter --> EventConsumers
    EventConsumers --> CommandBus

    AIAdapter --> SSRFGuard
    BrowserAdapter --> SSRFGuard

    BackendCore --> OTel
    OTel --> Prometheus
    OTel --> Loki
    Prometheus --> Grafana
    Loki --> Grafana
```

---

## 3. Core Business Workflow & Dataflow

```mermaid
sequenceDiagram
    autonumber
    actor User as User (Web / Telegram)
    participant API as API Gateway / IAM Service
    participant Registry as Website Registry Service
    participant Practice as Practice Orchestrator
    participant Browser as Browser Worker (Playwright)
    participant AI as AI Engine (Gemini / OpenAI)
    participant Result as Result & Analytics Service
    participant MQ as RabbitMQ (Outbox / Events)

    User->>API: 1. Authenticate (JWT + MFA)
    API-->>User: 2. Auth Tokens (Access + Refresh Rotation)

    User->>Registry: 3. Register Practice URL & Allowed Domain
    Registry->>Registry: 4. SSRF Validation & Domain Policy Check

    User->>Practice: 5. Start Practice Session
    Practice->>Browser: 6. Request Isolated Browser Context
    Browser->>Browser: 7. Launch Sandboxed Chromium Context
    Browser-->>User: 8. User-assisted Login / OTP Prompt (if required)
    User->>Browser: 9. Input OTP / Confirm Session Authorization

    Browser->>Practice: 10. Extract Question DOM, Text & Canvas/Images
    Practice->>MQ: 11. Publish `QuestionExtractedEvent`
    MQ->>AI: 12. Consume `QuestionExtractedEvent`

    AI->>AI: 13. Multimodal Analysis, OCR & Structured Reasoning
    AI->>MQ: 14. Publish `AIAnalysisCompletedEvent`
    MQ->>Practice: 15. Receive Solution, Explanations & Confidence Score

    Practice->>Browser: 16. Emulate Practice Action (Authorized Mode)
    Browser->>Practice: 17. Confirm Answer Submission & Captured Feedback

    Practice->>Result: 18. Persist Detailed Result & Metrics
    Result->>MQ: 19. Publish `PracticeSessionCompletedEvent`
    MQ->>User: 20. Instant Telegram / Webhook Notification with Summary Report
```

---

## 4. Key Architectural Decisions Summary

1. **Modular Monolith First, Microservices-Ready**:
   Packaged as clean decoupled modules with bounded domain contexts. Services communicate via strict Java API interfaces within JVM and asynchronous RabbitMQ domain events. Separation of database schemas guarantees zero shared-database coupling.
2. **Strict Hexagonal Ports and Adapters**:
   Domain business logic has 0 dependencies on Spring, Hibernate, or any third-party framework.
3. **Resilience & Fault Isolation**:
   Circuit breakers (Resilience4j), rate limiters, token bucket filters, and isolated worker pools prevent cascading failures.
4. **Zero-Trust Security**:
   AES-256-GCM encryption for all stored credentials, JWT with cryptographic key rotation, fine-grained RBAC/ABAC, and strict SSRF egress filters.
