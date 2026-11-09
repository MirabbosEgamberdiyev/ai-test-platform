# ADR-002: Clean Hexagonal Architecture (Ports & Adapters)

## Status
Accepted

## Context
Framework dependencies (Spring, Hibernate, Jackson) frequently change, evolve, or undergo breaking updates. Furthermore, core business rules regarding practice evaluation, question scoring, and domain constraints should be testable without bootstrapping a Spring context or database.

## Decision
We enforce **Clean Hexagonal Architecture**:
1. **Domain Layer**: Contains pure business entities, value objects, domain events, and domain exceptions. Absolutely zero imports from `org.springframework.*`, `jakarta.persistence.*`, or any third-party framework.
2. **Application Layer**: Contains Use Cases / Command and Query Handlers, and defines **Inbound Ports** (driving interfaces) and **Outbound Ports** (driven interfaces for persistence, AI, notifications).
3. **Infrastructure Layer**: Implements outbound ports (Spring Data JPA repositories, Redis caches, RabbitMQ publishers, AI API clients, Playwright drivers).
4. **Presentation Layer**: Implements inbound ports (REST controllers, Telegram webhook handlers).

## Consequences
- **Positive**: Domain logic is 100% unit-testable in milliseconds using plain JUnit 5 without mocks or containers.
- **Positive**: Easy swapping of external technologies (e.g. changing DB provider, or switching from OpenAI to Gemini) without touching business logic.
- **Negative**: Requires additional mapper classes (MapStruct) to translate between JPA entities, Domain models, and DTOs.
