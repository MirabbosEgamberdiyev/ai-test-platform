# ADR-007: Transactional Outbox Pattern and Idempotent Consumers

## Status
Accepted

## Context
When business mutations occur (e.g. starting a practice session, completing an AI analysis), the database update and message publishing to RabbitMQ must be atomic. Publishing a message before DB commit risks phantom messages if the transaction rolls back; publishing after commit risks message loss if the process crashes before publishing. Furthermore, network retries can cause duplicate message consumption.

## Decision
1. **Transactional Outbox Pattern**:
   - Every domain event is serialized to JSON and persisted to the `outbox_events` table within the **same local database transaction** as the business entity update.
   - An asynchronous relay (`OutboxRelayScheduler`) polls pending outbox entries, publishes them to the RabbitMQ topic exchange, and flags them as `processed = true`.
2. **Idempotent Consumers**:
   - Every published event contains a unique `eventId` (UUID) and `correlationId`.
   - Consumers verify whether the event has already been handled by executing a Redis `SETNX` on key `idempotency:event:{eventId}` with a 24-hour TTL before executing business logic.

## Consequences
- **Positive**: Guaranteed at-least-once delivery; zero two-phase commit (2PC) or XA distributed transaction bottlenecks.
- **Positive**: Complete resistance against duplicate event processing.
- **Negative**: Polling interval adds small latency (configurable between 200ms - 1000ms), which can be upgraded to Debezium CDC when ultra-low latency is required.
