# AI Test Platform — Product Requirements Document (PRD)

**Document Version:** 2.0.0  
**Product:** Universal AI-Powered Website Test Assistance & Authorized Automation Platform  
**Target URL Domain:** Practice websites, driver license test portals (e.g., `web.pravaonline.uz`), educational platforms, public assessment portals.

---

## 1. Product Vision & Mission

AI Test Platform is a specialized URL-driven test analysis and authorized browser automation system. It solves the friction users face when practicing tests across fragmented external web portals.

### Primary Workflow
```
REGISTER / LOGIN
       │
       ▼
SUBMIT TARGET URL (e.g., https://web.pravaonline.uz/tickets)
       │
       ▼
DISCOVER AVAILABLE TESTS & TICKETS (70 tickets indexed)
       │
       ▼
HUMAN-IN-THE-LOOP CONFIRMATION & SELECTION
       │
       ▼
SELECT EXECUTION MODE:
  ├─ Mode 1: ANSWER_MODE (Analysis, legal citations, arithmetic checks, exports)
  └─ Mode 2: AUTHORIZED_AUTOMATION_MODE (Playwright Chromium, answer injection, screenshot evidence)
       │
       ▼
AI INFERENCE & VERIFICATION
       │
       ▼
PRESENT VERIFIED RESULTS + SCREENSHOT + EXPORTS (CSV/JSON/Clipboard)
```

---

## 2. Core Product Principles (Invariants)

1. **User Decides Which Test to Select:** AI must NEVER guess or auto-select an exam or ticket without explicit user direction.
2. **Comprehensive Discovery:** Discovers all available tickets and categories supported by the target website.
3. **Strict Isolation:** Questions from different tickets or exams must never be blended or mixed.
4. **Traceable Explanations:** Every answer must provide a verifiable source citation (e.g., `YHQ 13.11-band`).
5. **Arithmetic & Ambiguity Checks:** Questions containing numerical or conflicting premises are verified and flagged.
6. **Ethical Non-Circumvention Policy:** Never bypass CAPTCHA, anti-bot protections, or high-stakes proctoring platforms (`proctorio.com`, `respondus.com`, `honorlock.com`, etc.).
7. **SSO & Session Injection:** Supports users authenticated via Google SSO or Telegram OAuth by accepting session cookies and Bearer tokens.

---

## 3. Execution Modes Specification

### Mode 1: Answer Mode (`ANSWER_MODE`)
- **Objective:** Deep question analysis, official rule citations, and learning reinforcement.
- **Operations:**
  - Extracts questions and options from target ticket.
  - Solves questions with multimodal AI inference.
  - Performs rule cross-referencing and arithmetic verification.
  - Generates clear explanations in the user's preferred language (`uz-Latn`, `uz-Cyrl`, `ru`, `en`).
  - Provides multi-format exports: Copy to Clipboard, CSV, and JSON.
- **Side Effects:** Zero modifications to external websites.

### Mode 2: Authorized Automation Mode (`AUTHORIZED_AUTOMATION_MODE`)
- **Objective:** Automated form entry for authorized practice exams.
- **Requirements:**
  - Explicit user confirmation checkbox (`userConfirmedSubmission: true`).
  - Target URL must pass SSRF safety validation.
- **Operations:**
  - Launches isolated Playwright Chromium instance.
  - Injects authenticated session cookies/Bearer headers if provided.
  - Navigates to ticket URL.
  - Interacts with form elements corresponding to the verified answers.
  - Captures full browser screenshot evidence.
  - Captures Chromium console logs and network responses.
  - Stores execution evidence JSON in the database.

### Mode 3: Guided Mode (`GUIDED_MODE`)
- **Objective:** Interactive manual action checkpoints where the user guides specific steps.

### Mode 4: Blocked Mode (`BLOCKED_MODE`)
- **Objective:** Safeguards the platform against prohibited proctored exams, malicious URLs, or unauthenticated sessions.

---

## 4. Multi-Language Parity

The platform mandates full feature and UI parity across 4 language locales:
1. **Uzbek (Latin)** (`uz-Latn`) — Primary default
2. **Uzbek (Cyrillic)** (`uz-Cyrl`)
3. **Russian** (`ru`)
4. **English** (`en`)

All translations cover navigation, test solver workflows, confirmation prompts, error states, and export headers.
