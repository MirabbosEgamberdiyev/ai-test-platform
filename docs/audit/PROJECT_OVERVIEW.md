# Autonomous Project Audit: Project Overview

## 1. Executive Summary
- **Platform Name**: AI Test Platform (`AiTestOps`)
- **Repository Path**: `D:\My-project\ai-test-platform`
- **Core Mission**: Transform into a URL-based AI Test Solving Platform (`REGISTER → LOGIN → SUBMIT URL → DISCOVER TESTS → SELECT TESTS → CONFIRM → EXTRACT QUESTIONS → AI SOLVING → VALIDATE ANSWERS → PRESENT RESULTS → SAVE HISTORY`).
- **Audit Date**: 2026-11-10
- **Auditor Role**: Principal Software Architect & Senior Autonomous Engineering Team.

## 2. Technology Stack & Component Inventory

| Layer | Component / Tool | Version | Verification State |
| :--- | :--- | :--- | :--- |
| **Backend Runtime** | OpenJDK 21 LTS | 21.0.1 64-bit | VERIFIED (Active JVM) |
| **Backend Framework**| Spring Boot | 3.3.5 | VERIFIED (Hexagonal Architecture) |
| **Persistence** | PostgreSQL | 16.15 Alpine | VERIFIED (Container on port 5433) |
| **Migration** | Flyway | 10.x | VERIFIED (V1, V2, V3 migrations applied) |
| **Cache & Session** | Redis | 7.x Alpine | VERIFIED (Container on port 6389) |
| **Automation** | Microsoft Playwright | 1.48.0 | VERIFIED (Chromium headless worker) |
| **AI Inference** | Google Gemini 1.5 Pro/Flash + OpenAI + Offline Rule Engine | Hybrid | VERIFIED |
| **Frontend Framework**| Next.js App Router | 14.2.15 | VERIFIED (Port 3000) |
| **Styling & UI** | Tailwind CSS + Lucide Icons | 3.4.1 | VERIFIED (Responsive 320px–3840px) |
| **Localization** | Custom i18n Context | 4 Locales | VERIFIED (`uz-Latn`, `uz-Cyrl`, `ru`, `en`) |

## 3. High-Level Architecture
The project adheres to Clean Architecture with strict modular boundaries:
- `platform-common`: Shared kernel, domain primitives, correlation filters, and SSRF security validators.
- `platform-iam`: Identity, BCrypt hashing, JWT tokens, Google OAuth 2.0 OIDC, and password recovery.
- `platform-registry`: Target website registry, CIDR-based SSRF blocklist, platform capability registry.
- `platform-automation`: Playwright browser sandboxing, isolated workers, and DOM extractors.
- `platform-ai`: Multimodal AI inference client, prompt templates, and arithmetic validators.
- `platform-testops`: Test discovery, ticket catalogs, question solving sessions, and result persistence.
- `platform-app`: Executable Spring Boot application, Flyway runner, and global exception handlers.

## 4. Key Findings Summary
1. **CRITICAL**: The primary product workflow requires prominent URL input on dashboard, automated discovery of all available tickets (e.g., tickets on `web.pravaonline.uz/tickets`), and strict mandatory user ticket selection prior to solving.
2. **HIGH**: Dedicated database persistence needed for `url_discovery_sessions`, `url_discovered_tickets`, `url_solving_sessions`, and `url_solved_questions` to ensure zero mixing of questions across tickets.
3. **MEDIUM**: Frontend needs a dedicated interactive URL Test Solver interface (`/dashboard/solve`) offering single, multiple, range ("1–10"), and "select all" ticket selection.
4. **LOW**: Localization strings must be expanded for all new ticket discovery and solving interaction steps.
