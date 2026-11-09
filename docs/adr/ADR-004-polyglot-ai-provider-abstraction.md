# ADR-004: Polyglot AI Provider Abstraction with Resilient Fallback

## Status
Accepted

## Context
AI services like Google Gemini and OpenAI have varying rate limits, availability outages, latency profiles, and cost structures. Directly hardcoding vendor SDKs creates vendor lock-in and leaves the application vulnerable to third-party outages.

## Decision
We implement a **Provider-Independent AI Abstraction**:
1. The domain interacts exclusively with `AIModelPort`, accepting a structured `AnalysisRequest` and returning a standardized `AnalysisResult`.
2. The infrastructure provides a **Strategy Pattern** implementation:
   - Primary: `GeminiAIClient` (high-throughput, native multimodal image + OCR comprehension, cost-effective).
   - Secondary / Fallback: `OpenAIAIClient` (high accuracy, structured JSON output fallback).
3. Resilience4j Circuit Breaker and Retry policies automatically switch traffic to the fallback provider when error rates exceed 30% or when rate limit (429) errors occur.
4. Token usage, latency, and estimated USD cost are tracked per request and recorded into `ai_token_usages`.

## Consequences
- **Positive**: Zero vendor lock-in; high availability even during single-provider downtime; precise financial cost accounting per tenant.
- **Negative**: Prompts must be designed to yield structured JSON outputs consistently across both model families.
