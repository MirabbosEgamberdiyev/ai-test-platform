# IMPLEMENTATION PLAN — UNIVERSAL AI WEB TESTING PLATFORM
**Document Version:** 2.0  
**Target:** 6 Stages of Complete SaaS Implementation  
**Execution Order:** AUDIT → ARCHITECT → IMPLEMENT → INTEGRATE → BUILD → RUN → TEST → FIX → VERIFY → DEPLOY

---

## 1. Roadmap Overview

```
[ STAGE 1: Foundation ]
   ├── Database Schema V2 (Projects, Plans, Cases, Runs, Defects, Reports)
   ├── Domain Models & Repositories
   └── Web Testing REST APIs & Tenant Boundaries
             │
             ▼
[ STAGE 2: Universal Discovery ]
   ├── Playwright Browser Worker Context Setup
   ├── Route Crawling & Navigation Tree Discovery
   └── DOM, Form & Interactive Component Extraction
             │
             ▼
[ STAGE 3: AI Test Generation ]
   ├── Polyglot Prompt Parser (uz-Latn, uz-Cyrl, ru, en)
   ├── LLM Routing Engine (Gemini / OpenAI)
   └── Test Plan Construction & Human-in-the-Loop Approval
             │
             ▼
[ STAGE 4: Browser Execution ]
   ├── Deterministic Browser Test Runner (GOTO, CLICK, FILL, ASSERT)
   ├── Multi-Viewport Responsive Validation (320px - 1920px)
   ├── Accessibility WCAG 2.2 AA Automated Checks
   ├── Network 4xx/5xx & Console Error Interception
   └── Screenshot & Evidence Persistence
             │
             ▼
[ STAGE 5: Reports & Defect Analytics ]
   ├── AI Root-Cause Hypothesis Generator
   ├── Defect Severity Classification (CRITICAL, HIGH, MEDIUM, LOW)
   └── Multi-Format Export Engine (PDF, HTML, CSV, JSON)
             │
             ▼
[ STAGE 6: Enterprise Frontend & Verification ]
   ├── Guided 5-Step Test Creation Wizard
   ├── Real-Time Live Execution Monitor
   ├── Full 4-Language Translation & Persistence
   └── Automated Playwright & E2E API Verification Suite
```

---

## 2. Detailed Stage Breakdown

### Stage 1: Foundation (Backend & Database)
- **Flyway:** `V2__universal_web_testing_schema.sql` creates tables: `web_test_projects`, `web_test_plans`, `web_test_cases`, `web_test_runs`, `web_defects`, `web_test_reports`.
- **Domain Entities & Value Objects:**
  - `WebTestProject` (URL, name, environment: DEV/STAGE/PROD, instructions).
  - `WebTestPlan` (natural language prompt, detected scope, status: DRAFT/APPROVED).
  - `WebTestCase` (category: FUNCTIONAL, UI_UX, RESPONSIVE, ACCESSIBILITY, API, SECURITY, steps).
  - `WebTestRun` (status: CREATED -> DISCOVERING -> GENERATING -> APPROVED -> RUNNING -> COMPLETED).
  - `WebDefect` (severity, title, expected, actual, reproduction steps, screenshot, aiAnalysis).
  - `WebTestReport` (metrics, pass rate, failure insights, multi-format export).
- **Repositories & REST Controllers:**
  - `WebTestProjectController`: `/api/v1/web-projects`
  - `WebTestRunController`: `/api/v1/web-test-runs`
  - `WebTestReportController`: `/api/v1/web-reports`

### Stage 2: Universal Discovery Engine
- **Service:** `UniversalWebsiteDiscoveryService` in `platform-automation`.
- **Capabilities:**
  - Navigates to target URL safely via `DefaultSsrfGuardAdapter`.
  - Discovers internal links, routes, page metadata, title.
  - Extracts form fields: inputs, textareas, selects, buttons, labels.
  - Detects authentication forms, search inputs, CTA elements.
  - Takes discovery screenshot.
  - Returns `WebsiteDiscoveryResult`.

### Stage 3: AI Test Generation Engine
- **Service:** `WebTestPlanGeneratorService` in `platform-ai`.
- **Capabilities:**
  - Accepts natural language goal in Uzbek Latin, Uzbek Cyrillic, Russian, or English.
  - Infers user intent (e.g., authentication, checkout, search, responsive, form validation).
  - Matches intent with discovered elements to generate deterministic test cases.
  - Returns structured `WebTestPlanDto` for user review and approval.

### Stage 4: Deterministic Browser Execution & Evidence Capture
- **Service:** `UniversalBrowserTestExecutor` in `platform-automation`.
- **Capabilities:**
  - Launches ephemeral Playwright browser context per run.
  - Interprets steps:
    - `GOTO`: Navigates to URL, waits for load state.
    - `CLICK`: Locates element and clicks.
    - `FILL`: Types into text/input element.
    - `ASSERT_TEXT` / `ASSERT_VISIBLE`: Asserts expected element presence/content.
    - `CHECK_RESPONSIVE`: Tests viewport sizing and horizontal overflow.
    - `CHECK_ACCESSIBILITY`: Validates alt attributes, form labels.
  - Intercepts JS errors and network failures.
  - Saves screenshots on failure to `artifacts/screenshots/`.
  - Updates `WebTestRun` status and creates `WebDefect` entities.

### Stage 5: Reports & AI Bug Analysis
- **Service:** `WebTestReportService` in `platform-testops`.
- **Capabilities:**
  - Aggregates pass/fail metrics, duration, defect counts by severity.
  - Generates AI root-cause hypothesis explaining failure reasons based on console errors, network status, and DOM state.
  - Generates export files: JSON, HTML, CSV, PDF.

### Stage 6: Enterprise Frontend SaaS & Final Verification
- **Frontend Pages:**
  - `/dashboard`: Real metrics, recent test runs, quick actions.
  - `/dashboard/websites`: My Websites list, add website modal, environment toggles.
  - `/dashboard/tests/new`: Guided 5-step wizard (URL -> Prompt -> Scope -> Auth -> Plan Approval -> Run).
  - `/dashboard/tests/[id]`: Live execution monitor with real-time logs, step progression, screenshot view.
  - `/dashboard/reports/[id]`: Comprehensive report with metrics, defects list, AI analysis, export buttons.
- **Localization:** All new text keys in `uz-Latn.ts`, `uz-Cyrl.ts`, `ru.ts`, `en.ts`.
- **Verification:** Run E2E test script testing against real websites (`https://example.com`), verify responsive design, verify build.
