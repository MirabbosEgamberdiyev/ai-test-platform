# ADR-001: Modular Monolith with Microservice Readiness

## Status
Accepted

## Context
The platform requires rich domain separation across 16 Bounded Contexts (IAM, Practice, AI, Browser, Billing, etc.). Deploying 16 separate distributed microservices from day one introduces severe operational overhead (distributed network hops, complex deployment choreography, network latency, distributed transaction orchestration overhead, and high cloud hosting costs). However, tight coupling inside a poorly structured monolith would prevent independent scaling of heavy workloads like Browser Automation and AI inference.

## Decision
We adopt a **Modular Clean Monolith Architecture** with strict Bounded Context isolation:
1. Each domain module has its own discrete package/Maven module hierarchy (`domain`, `application`, `infrastructure`, `presentation`).
2. Modules must **not** directly import or reference internal classes of other modules; they interact exclusively via published application service ports or asynchronous Domain Events.
3. Database isolation: Each context maintains independent database tables without cross-schema foreign keys.
4. Heavy background workloads (Playwright Browser Automation) are encapsulated behind an asynchronous worker interface that can run in-process or be deployed as independent worker pods consuming from RabbitMQ.

## Consequences
- **Positive**: Blazing fast in-memory execution during development and initial production; simple single-artifact CI/CD; zero distributed query penalties for standard user flows.
- **Positive**: Seamless extraction into standalone microservices whenever a specific domain (e.g. `platform-automation` or `platform-ai`) requires independent horizontal scaling.
- **Negative**: Requires strict discipline and automated architecture enforcement (ArchUnit) to avoid accidental coupling across modules.
