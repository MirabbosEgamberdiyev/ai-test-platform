# AI Test Platform — System Architecture & Hexagonal Design

**Architecture Style:** Clean Modular Hexagonal Architecture (Ports and Adapters)  
**Backend Framework:** Spring Boot 3.3.5 / Java 21 LTS  
**Frontend Framework:** Next.js 14 / React 18 / TypeScript 5 / TailwindCSS  

---

## 1. High-Level System Architecture

```
                       ┌─────────────────────────┐
                       │  Next.js 14 Frontend    │
                       │  (App Router, React 18) │
                       └────────────┬────────────┘
                                    │ HTTP / REST / JSON
                                    ▼
       ┌────────────────────────────────────────────────────────┐
       │             Spring Boot 3.3.5 Gateway                  │
       │       (Stateless JWT, CORS, ProblemDetail)             │
       └───────┬──────────────┬──────────────┬───────────┬──────┘
               │              │              │           │
               ▼              ▼              ▼           ▼
        ┌─────────────┐┌─────────────┐┌────────────┐┌───────────┐
        │platform-iam ││platform-reg ││platform-ai ││platform-  │
        │(Auth, JWT,  ││(SSRF Guard, ││(Gemini 1.5 ││automation│
        │ Google SSO) ││ AES-GCM)    ││ & OpenAI)  ││(Playwright│
        └──────┬──────┘└──────┬──────┘└─────┬──────┘│ Chromium) │
               │              │             │       └─────┬─────┘
               ▼              ▼             ▼             │
        ┌──────────────────────────────────────────┐      │
        │               platform-testops           │◄─────┘
        │   (UrlDiscoveryService, SolvingService)  │
        └─────────────────────┬────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    ▼                   ▼
           ┌────────────────┐   ┌───────────────┐
           │ PostgreSQL 16  │   │  Redis 7.4    │
           │ (Port 5433)    │   │  (Port 6389)  │
           └────────────────┘   └───────────────┘
```

---

## 2. Bounded Contexts & Hexagonal Modules

1. **`platform-common`**: Shared value objects (`TenantId`, `UserId`), domain exceptions (`SsrfSecurityException`, `BusinessRuleViolationException`), ProblemDetail mapper (`GlobalExceptionHandler`), and `CorrelationIdFilter`.
2. **`platform-iam`**: Multi-tenant identity and access management, Argon2id password hashing, HMAC-SHA256 JWT generation/validation, Google OAuth2 ID token verification, and session lifecycle.
3. **`platform-registry`**: External target website management, AES-GCM credential vault, and `DefaultSsrfGuardAdapter` (RFC 1918, loopback, cloud metadata, and proctoring domain blacklist).
4. **`platform-ai`**: Polyglot AI inference abstraction (`AiInferencePort`), Gemini 1.5 Flash/Pro integration, OpenAI fallback, multimodal image analysis, and rule citation generation.
5. **`platform-automation`**: Isolated Playwright browser orchestration, Chromium headless worker, session cookie & Bearer token injection, DOM evaluation, and screenshot capture.
6. **`platform-testops`**: Core business domain managing URL discovery sessions, ticket catalogs, adaptive solving sessions, answer verification, and multi-format exports.
7. **`platform-audit`**: Immutable compliance audit trail logging security events, tenant logins, and browser automation executions.
8. **`platform-app`**: Application entrypoint packaging the executable Spring Boot Uber-JAR, static screenshot mapping (`WebMvcConfig`), and Flyway database migration runner.

---

## 3. Communication Patterns

- **Synchronous Inbound:** REST API with OpenAPI/Swagger documentation (`/v3/api-docs`).
- **Synchronous Outbound:** Resilient HTTP clients with connection timeouts (8s - 15s) and DNS resolution verification.
- **Asynchronous Messaging:** RabbitMQ event pub/sub for decoupling background test runs and notifications.
- **State Management:** Stateless JWT bearer tokens with distributed caching in Redis for session invalidation and rate limiting.
