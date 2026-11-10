# AI Test Platform — AI Inference Engine & Prompt Architecture

**Primary AI Engine:** Google Gemini 1.5 Flash / Pro (via `google-genai` SDK / Spring AI)  
**Fallback Engine:** OpenAI GPT-4o / GPT-4o-mini  
**Multimodal Input Support:** Raw text, road signs, traffic diagrams, DOM snapshot structures  

---

## 1. AI Inference Pipeline

The platform uses a polyglot AI inference port (`AiInferencePort`) providing model agnosticism, deterministic JSON output enforcement, and fallback redundancy.

```
       Incoming Questions
              │
              ▼
   ┌───────────────────────┐
   │ Domain Prompt Builder │  <-- Context: Official Road Traffic Regulations (YHQ)
   └──────────┬────────────┘
              │
              ▼
   ┌───────────────────────┐     Primary fails
   │ Gemini 1.5 Flash/Pro  │ ─────────────────────► ┌──────────────────────┐
   └──────────┬────────────┘                        │ OpenAI GPT-4o / mini │
              │ Primary succeeds                    └──────────┬───────────┘
              │                                                │
              ▼◄───────────────────────────────────────────────┘
   ┌───────────────────────┐
   │ JSON Schema Validator │
   └──────────┬────────────┘
              │
              ▼
   ┌───────────────────────┐
   │ Arithmetic & Ambiguity│  <-- Cross-checks numerical parameters and
   │ Verification Engine   │      detects conflicting multiple choice options
   └──────────┬────────────┘
              │
              ▼
     Verified Solved Questions (Confidence Score, Rule Citation, Explanation)
```

---

## 2. Invariant Rules Enforcement

1. **Zero Guessing Policy:** The AI is strictly prohibited from guessing which ticket the user intends to solve. The user must provide explicit ticket numbers.
2. **Legal & Regulatory Citation:** Every driver exam answer must include an authoritative rule reference (e.g., `YHQ 13.11-band` — "Tartibga solinmagan teng ahamiyatli chorrahalarda o'ngdan kelayotgan transport vositasiga yo'l berish").
3. **Traceability:** AI confidence score (0.0 to 1.0) and status (`VERIFIED`, `NEEDS_REVIEW`, `AMBIGUOUS`) are calculated for every single question.
4. **Language Localization Parity:** Reasoning explanations and notes are generated natively in the target locale (`uz-Latn`, `uz-Cyrl`, `ru`, `en`).
