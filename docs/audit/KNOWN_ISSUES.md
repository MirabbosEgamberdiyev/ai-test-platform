# KNOWN ISSUES & REMEDIATION PLAN — ENTERPRISE AI TEST AUTOMATION PLATFORM
**Audit Date:** November 9, 2026  
**Auditor:** Principal Software Architect & QA Engineering Audit Team

---

## 1. Identified Issues Register

### ISSUE-01: Full Multilingual Support Missing in Frontend UI (Priority: P1)
- **Description:** Mandatory acceptance requirement mandates 100% localization across 4 languages:
  1. Uzbek Latin (`uz-Latn`)
  2. Uzbek Cyrillic (`uz-Cyrl`)
  3. Russian (`ru`)
  4. English (`en`)
  The initial implementation was written purely in English.
- **Root Cause:** Phase 1 development prioritized backend domain engineering and architectural compliance before building the internationalization infrastructure.
- **Remediation Action:**
  - Create a lightweight, high-performance client-side i18n store (`useTranslation` hook & React Context) in `frontend/src/lib/i18n/`.
  - Author complete translation dictionaries for all 4 languages with authentic vocabulary and proper Uzbek special typography (`o‘`, `g‘`, `sh`, `ch`, `ng` in Latin, and `ў`, `ғ`, `ш`, `ч`, `қ`, `ҳ` in Cyrillic).
  - Add an interactive Language Switcher pill in the top navigation bar with persistent `localStorage` storage and seamless runtime switching.
  - Localize all static labels, buttons, forms, tooltips, cards, tables, and status badges across all pages.

---

### ISSUE-02: Dashboard Mobile Navigation Drawer Missing (Priority: P2)
- **Description:** On viewport widths `<768px` (mobile devices: 320px–430px), the dashboard sidebar was hidden, leaving mobile users unable to navigate between Overview, Practice, Integrations, and Analytics.
- **Root Cause:** Desktop sidebar used `w-64 sticky`, hiding on mobile without an accompanying slide-out hamburger menu.
- **Remediation Action:**
  - Implement a mobile navigation drawer with hamburger trigger in the dashboard top navigation bar.
  - Ensure touch targets are at least 44x44px.
  - Implement responsive table wrappers (`overflow-x-auto`) and card flex wrapping for mobile screens.

---

### ISSUE-03: Theme Mode Toggle (Light/Dark) Missing (Priority: P2)
- **Description:** Platform styling was statically fixed to dark mode (`dark` class on root HTML). A production SaaS platform must offer user-controllable Light / Dark theme modes conforming to WCAG 2.2 contrast standards.
- **Root Cause:** Fast prototyping with dark mode color schemes without dynamic theme context.
- **Remediation Action:**
  - Create a Theme Context / Hook supporting `light` and `dark` modes with persistent `localStorage` synchronization.
  - Add a Theme Toggle button in the header.
  - Ensure high-contrast ratios in both modes (>4.5:1 for normal text, >3:1 for large text).

---

### ISSUE-04: Port Collision on Host Machine (Priority: P1 - Resolved)
- **Description:** Default port 8080 was occupied by a pre-existing host background application.
- **Root Cause:** Environment port conflict on Windows.
- **Resolution:** Updated Spring Boot `application.yml` default port to `8085`, updated Next.js rewrites and frontend API client to target port `8085`.

---

### ISSUE-05: Real End-to-End Live Integration Verification (Priority: P1)
- **Description:** Automated proof is required demonstrating the entire user journey: registration, login, target website creation, AI question analysis, answer submission, and score analytics.
- **Remediation Action:**
  - Create an end-to-end integration test runner that boots the Spring Boot backend on port 8085, executes real HTTP requests against all endpoints, asserts status codes and response bodies, and logs evidence.
