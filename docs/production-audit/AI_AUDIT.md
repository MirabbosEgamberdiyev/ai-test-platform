# Enterprise AI Test Platform — AI Architecture Audit

## 1. Multi-Provider AI Topology

The platform integrates a resilient, provider-agnostic AI routing layer (`platform-ai`):
- **Primary Engine**: Google Gemini 1.5 Flash / Pro via REST API / SDK
- **Secondary Fallback**: OpenAI GPT-4o / GPT-3.5-Turbo via REST API
- **Local Fallback**: Deterministic algorithmic generator (`buildDeterministicPlan`) ensuring zero downtime when external APIs are unreachable or offline.

## 2. AI Capabilities & Workflows

1. **AI Question Generator (`POST /api/v1/exams/ai-generate`)**:
   - Generates questions according to subject, topic, grade, difficulty, and question type.
   - Outputs strict structured JSON: question prompt, 4 distinct options, correct answer index, and pedagogical explanation.
   - Review workflow: AI-generated questions are flagged as `DRAFT` and require teacher/moderator approval before publishing to active question banks.
2. **AI Learning Assistant & Root-Cause Analysis**:
   - Analyzes incorrect answers, provides tailored explanations in the user's selected language (`uz-Latn`, `uz-Cyrl`, `ru`, `en`).
   - For web test defects: analyzes browser console logs, DOM state, and network failures to generate actionable root-cause hypotheses and CSS/JS code fixes.
3. **Deterministic Official Scoring Rule**:
   - In accordance with enterprise ethics, AI is **strictly prohibited from independently assigning official grades or scores**. Official exam scores are evaluated deterministically using server-side exact match and `BigDecimal`.

## 3. AI Safety & Prompt Injection Mitigation

- **Prompt Sanitization**: User-supplied natural language prompts are escaped and bounded. System prompt anchors cannot be overridden by user input.
- **Output Validation**: All AI responses are validated against strict JSON schemas before being accepted. Malformed responses trigger an automatic retry or fallback to deterministic templates.
- **Cost & Quota Controls**: Token usage and API latencies are tracked in logs with correlation IDs.

## 4. AI Audit Findings

### [FINDING-AI-01] [MEDIUM] Multilingual AI Output Alignment
- **Observation**: When user language is `uz-Cyrl` or `ru`, AI explanations must strictly output the corresponding script without mixed Latin/Cyrillic characters.
- **Remediation**: Enforce explicit language constraints in system prompts and test outputs across all four supported locales.
