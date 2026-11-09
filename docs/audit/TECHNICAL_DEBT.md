# TECHNICAL DEBT & ARCHITECTURAL REFACTORING PLAN
**Document Version:** 2.0  
**Updated:** November 9, 2026

---

## 1. Overview
The transition from an isolated multiple-choice practice engine to a universal web quality SaaS requires addressing technical debt and upgrading domain boundaries without breaking existing stability.

---

## 2. Identified Technical Debt & Action Items

### 2.1 Domain Model Isolation
- **Item:** The existing `platform-testops` module contained entities solely tied to `PracticeSession`, `TestQuestion`, and `PracticeResult`.
- **Debt Impact:** Attempting to force full-suite web testing concepts into a question-based structure introduces impedance mismatch and brittle code.
- **Refactoring:** Keep existing practice entities intact for backward compatibility, while introducing first-class Web Testing domain models:
  - `WebTestProject`
  - `WebTestPlan`
  - `WebTestCase`
  - `WebTestRun`
  - `WebDefect`
  - `WebTestReport`

### 2.2 Database Schema Migration
- **Item:** Flyway schema `V1__init_schema.sql` lacks tables for web testing projects, plans, cases, runs, defects, and reports.
- **Remediation:** Introduce `V2__universal_web_testing_schema.sql` with proper indexes, cascade rules, tenant ID columns, and foreign keys.

### 2.3 Browser Automation Extensibility
- **Item:** `PlaywrightBrowserWorkerAdapter` had hardcoded logic seeking question cards and radio buttons.
- **Remediation:** Implement a clean, modular abstraction:
  - `UniversalWebsiteDiscoveryService`: Generic web crawler and interactive element extractor.
  - `UniversalBrowserTestExecutor`: Generic action interpreter (`GOTO`, `CLICK`, `FILL`, `SELECT`, `ASSERT_TEXT`, `ASSERT_VISIBLE`, `CHECK_RESPONSIVE`, `CHECK_ACCESSIBILITY`).

### 2.4 AI Routing & Prompt Management
- **Item:** Prompt templates in `AIRoutingEngineService` were coupled to multiple-choice question solving.
- **Remediation:** Add `UniversalTestPlanGeneratorService` in `platform-ai` with structured JSON schema output, multi-lingual prompt support (`uz-Latn`, `uz-Cyrl`, `ru`, `en`), and fallback between Gemini 1.5 and OpenAI models.

### 2.5 Frontend API and State Consolidation
- **Item:** Frontend had static placeholders for several dashboard statistics and lacked navigation routes for website management and live test monitoring.
- **Remediation:**
  - Build typed API client methods in `frontend/src/lib/api.ts` for all web testing endpoints.
  - Add 4-language i18n translation keys in `frontend/src/lib/i18n/{en,uz-Latn,uz-Cyrl,ru}.ts`.
  - Connect real metrics to Dashboard, Websites, New Test Wizard, Live Execution, and Reports.
