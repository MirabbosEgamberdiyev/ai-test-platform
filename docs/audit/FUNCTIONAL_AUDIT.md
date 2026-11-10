# Autonomous Project Audit: Functional Audit

## 1. Core Workflow Verification
The required core workflow is:
`REGISTER → LOGIN → SUBMIT URL → DISCOVER TESTS → SELECT TESTS → CONFIRM → EXTRACT QUESTIONS → AI SOLVING → VALIDATE ANSWERS → PRESENT RESULTS → SAVE HISTORY`

## 2. Functional Capabilities & Gap Assessment

| Feature | Required Behavior | Current Status | Audit Finding & Action Required |
| :--- | :--- | :--- | :--- |
| **Authentication** | Google OAuth & Email/Password (Admin/Admin.123$) | **VERIFIED** | Default Admin/Admin.123$ active. Google OAuth OIDC pipeline ready. |
| **URL Input** | Prominent URL bar on dashboard, scheme & domain validation | **PARTIAL** | Move URL input to main hero banner on dashboard and dedicated `/dashboard/solve` page. |
| **Test Discovery** | Discovers all available tickets (e.g. Bilet 1..70 on pravaonline) | **GAP** | Build `UrlTestDiscoveryService` using Playwright DOM ticket parser. |
| **Ticket Selection** | Single, multiple, range ("1–10"), select all ("Barcha"), search | **GAP** | Implement multi-mode ticket selector with server-side catalog pagination and confirmation prompt. |
| **Confirmation** | "Saytda bitta test topildi..." or "Saytda {count} ta bilet topildi..." | **GAP** | Implement confirmation step before launching extraction or AI tokens. |
| **Question Extraction**| Extracts questions, options, images, question types per ticket | **GAP** | Implement `UrlQuestionExtractor` with Playwright isolated sandbox. |
| **AI Solving Engine** | Multimodal solving, step-by-step rationale, confidence score | **PARTIAL** | Implement `UrlTestSolvingService` with Gemini/OpenAI prompt templates and offline validation. |
| **Answer Validation** | Flag ambiguity, recalculate arithmetic, verify option mapping | **GAP** | Add `AnswerValidator` with math re-evaluation and ambiguity warnings. |
| **Results & Export** | Clean answer review, PDF/Excel/JSON export, Copy Answers | **GAP** | Build interactive results page with ticket filtering and download buttons. |
| **Persistent History** | Save sessions, reopen past results, delete history | **GAP** | Add `url_solving_sessions` history viewer. |

## 3. Mandatory Business Invariants
1. **The user decides which test to select**: AI never auto-selects or assumes a ticket.
2. **Never mix tickets**: Questions remain grouped under their specific ticket identifier.
3. **Traceability**: Every answer maps directly to its extracted question text and sequence.
4. **Honest reporting**: If a site is unsupported or gated by CAPTCHA/MFA, report `BLOCKED_BY_EXTERNAL_ACCESS`.
