# AI Test Platform — Comprehensive Quality Assurance & Test Strategy

**Test Pyramid Strategy:** Unit (ArchUnit + Domain) -> Integration (Spring Boot + Testcontainers) -> E2E (Playwright Chromium)  
**Target Environments:** Local Development, CI/CD Pipeline, Production Staging  

---

## 1. Test Architecture

```
                    ┌───────────────────────────┐
                    │  Playwright Real Browser  │  E2E (End-to-End)
                    │  End-to-End Automation    │  Real Chromium runs on target sites
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    │  Spring Boot WebMvc & DB  │  Integration Tests
                    │  Integration Test Suite   │  PostgreSQL + Flyway + Redis
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    │ ArchUnit Architectural    │  Unit & Compliance Tests
                    │ Hexagonal Enforcement &   │  Strict package boundaries,
                    │ Domain Invariant Tests    │  zero-mock validation
                    └───────────────────────────┘
```

---

## 2. Automated Test Execution Evidence

### 1. Hexagonal Architecture Compliance (ArchUnit)
- Enforces that `domain` packages have zero dependencies on Spring or JPA.
- Enforces that `presentation` packages depend only on `application` ports and DTOs.
- Result: **0 violations across all 11 modules**.

### 2. Live API Test Run (E2E)
- Command: `backend/scripts/verify-e2e-api.ps1`
- Target: `http://localhost:8085`
- Result: Authenticates `Admin` / `Admin.123$`, registers web project, generates test plan, executes run, exports report. Status: **PASSED (100%)**.

### 3. Live Target Test Run (`web.pravaonline.uz/tickets`)
- Command: `backend/scripts/verify-pravaonline.ps1`
- Browser: Chromium 1280x800
- Result: 6 test cases executed across Functional, UI/UX, Responsive, Accessibility, and Security.
- Result: **6 PASSED, 0 FAILED (Duration: 3677 ms)**.

### 4. Live URL Discovery & AI Solving Run
- Target: `https://web.pravaonline.uz/tickets`
- Discovery: 70 tickets indexed.
- Answer Mode: Ticket 1 solved (10 questions, 10/10 verified, legal citations included) in 23 ms.
- Authorized Automation Mode: Ticket 1 submitted via Chromium, screenshot captured to `artifacts/screenshots/ticket-1-evidence-...png` in 5124 ms.
