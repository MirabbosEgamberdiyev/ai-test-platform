# INITIAL PROJECT AUDIT — UNIVERSAL AI WEB TESTING PLATFORM
**Audit Date:** November 9, 2026  
**Auditor:** Principal Software Architect & Enterprise QA Engineering Team  
**Scope:** Universal AI Web Testing SaaS Transformation of `D:\My-project\ai-test-platform`

---

## 1. Executive Summary

This audit evaluates the codebase at `D:\My-project\ai-test-platform` against the requirements of a **Universal AI Web Testing Platform**.

The core premise of the platform:
> **The user describes WHAT should be tested in natural language (Uzbek Latin, Uzbek Cyrillic, Russian, or English). The AI understands HOW to test it in a real browser sandbox, completely independent of the target website's programming language, framework, CMS, or database.**

The existing foundation possesses robust architectural building blocks:
- **Clean Hexagonal Architecture (Ports and Adapters)** across 10 modular Maven artifacts.
- **Java 21 LTS** with Virtual Threads enabled on **Spring Boot 3.3.5**.
- **PostgreSQL 16**, **Redis 7**, **RabbitMQ 3.13**, and **Playwright Java 1.44.0**.
- **Next.js 14.2.15 (App Router)**, **TypeScript**, and a complete **4-language localization store (`uz-Latn`, `uz-Cyrl`, `ru`, `en`)**.
- **SSRF Guard** (`DefaultSsrfGuardAdapter`) and **AES-256-GCM Credential Vault**.

However, the previous implementation was tailored specifically to multiple-choice test practice. To become the Universal AI Web Testing SaaS specified in the Master Prompt, the domain models, AI prompt engines, browser automation orchestrators, and frontend user workflows must be expanded into an end-to-end web quality engineering platform.

---

## 2. Technology Stack & Component Inventory

| Component | Architecture Role | Current State | Required Target State | Gap / Action |
|---|---|---|---|---|
| `platform-common` | Shared Kernel & Core DTOs | Stable (Result, ErrorCode, SSRF models) | Universal Web Testing DTOs, Event types | Add Web Test Event definitions |
| `platform-iam` | Authentication & Multi-Tenancy | Production-grade JWT & Tenant Isolation | Retain as-is | Verified & active |
| `platform-registry` | Website Registration & Security | Basic website records & SSRF | Add Environment configs, Testing Instructions, Authorization flags | Expand entity & REST endpoints |
| `platform-ai` | LLM Orchestrator | Gemini & OpenAI routing for question solving | Universal Web Test Plan Generator & AI Defect Root Cause Analyzer | Add `UniversalTestPlanGeneratorService` |
| `platform-automation` | Browser Sandbox Engine | Single-page question selector extraction | Universal Website Discovery (crawler) & Deterministic Test Execution Engine | Add `UniversalDiscoveryService` & `DeterministicTestExecutor` |
| `platform-testops` | Test Operations Core | Multiple-choice practice sessions | Web Test Projects, Test Plans, Test Cases, Test Runs, Defects, Reports | Implement full Web Testing domain & entities |
| `platform-billing` | SaaS Subscriptions | Basic subscription tracking | Plan limits on parallel test runs & retention | Retain & integrate with runs |
| `platform-notification` | Alerts & Webhooks | Notification ports | Email/Webhook alerts on test completion | Retain |
| `platform-audit` | Audit Logging | Immutable audit repository | Audit all test runs, target URLs, and credential usages | Retain & log web runs |
| `frontend` | Next.js SaaS UI | Practice dashboard & auth | Universal Testing Wizard, Live Test Monitor, Defect Viewer, Report Exports | Build 5 dedicated SaaS pages |

---

## 3. Detailed Gap Analysis & Defect Inventory

### 3.1 Domain & Data Model Gap
- **Deficiency:** Existing tables (`test_questions`, `practice_sessions`, `practice_results`) only support single-question practice sessions.
- **Remediation:** Introduce Flyway migration `V2__universal_web_testing_schema.sql` creating:
  - `web_test_projects`: Website links, environment, natural language instructions.
  - `web_test_plans`: AI-generated structured test plans with versioning and approval state (`DRAFT`, `APPROVED`, `REJECTED`).
  - `web_test_cases`: Test steps, category (`FUNCTIONAL`, `UI_UX`, `RESPONSIVE`, `ACCESSIBILITY`, `API`, `SECURITY`, `PERFORMANCE`), expected assertions.
  - `web_test_runs`: Execution lifecycle, duration, pass/fail counts, execution status.
  - `web_defects`: Defects with severity (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`, `INFO`), reproduction steps, screenshots, console logs, and AI root cause.
  - `web_test_reports`: Aggregated reports with multi-format export capability (`HTML`, `JSON`, `CSV`, `PDF`).

### 3.2 Universal Website Discovery Gap
- **Deficiency:** Current browser automation only attempts to scrape question containers on predetermined structures.
- **Remediation:** Build `UniversalWebsiteDiscoveryService`:
  - Navigates to any target URL in headless Playwright Chromium.
  - Recursively crawls and maps public internal routes.
  - Extracts interactive elements (forms, inputs, buttons, select dropdowns, search bars).
  - Captures landing screenshot.
  - Detects authentication forms, navigation menus, and semantic ARIA tree.

### 3.3 AI Test Generation Gap
- **Deficiency:** AI engine only supported answering questions and generating hints.
- **Remediation:** Implement `WebTestPlanGeneratorService`:
  - Parses natural language requests in `uz-Latn`, `uz-Cyrl`, `ru`, and `en`.
  - Blends user goals with discovered DOM structure to emit executable step sequences.
  - Allows human-in-the-loop review, editing, and explicit approval before execution.

### 3.4 Deterministic Browser Execution & Evidence Capture
- **Deficiency:** Lacked multi-step workflow execution, automated defect classification, and screenshot artifact archiving.
- **Remediation:** Build `UniversalBrowserTestExecutor`:
  - Step runners: `GOTO`, `CLICK`, `FILL`, `SELECT`, `ASSERT_TEXT`, `ASSERT_VISIBLE`, `CHECK_RESPONSIVE`, `CHECK_ACCESSIBILITY`.
  - Captures uncaught browser console errors (`page.onConsoleMessage`) and network 4xx/5xx failures.
  - Screenshots every failure and archives to `artifacts/screenshots/`.
  - Asynchronous execution with live event streaming.

### 3.5 Frontend User Experience
- **Deficiency:** Frontend lacked the 5-step guided wizard and live execution observation view.
- **Remediation:**
  - Build Guided Wizard (`/dashboard/tests/new`).
  - Build Live Execution Monitor (`/dashboard/tests/[id]`).
  - Build Interactive Report & Defect Viewer (`/dashboard/reports/[id]`).
  - Build Website Management (`/dashboard/websites`).

---

## 4. Verification Baseline
- All 10 backend modules compile cleanly (`mvn test-compile` passes).
- Architecture tests confirm Hexagonal boundaries.
- PostgreSQL 16 (port 5433), Redis 7 (port 6379), RabbitMQ (port 5672) are active.
- Frontend builds cleanly on Next.js 14.2.15.
