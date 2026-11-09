# ADR-003: Event-Driven Messaging with RabbitMQ and DLQ

## Status
Accepted

## Context
Long-running and unpredictable tasks such as browser page rendering, DOM question parsing, multimodal AI image processing, and Telegram alert dispatch must not block incoming HTTP request threads. We evaluated Apache Kafka vs RabbitMQ.

## Decision
We select **RabbitMQ 3.13** as the primary message broker:
1. RabbitMQ excels at discrete, task-oriented background job distribution with flexible routing keys, per-message TTL, and Dead Letter Queues (DLQ).
2. Kafka's log-streaming overhead and partition-key constraints are unnecessary for discrete session/job orchestration.
3. Every exchange is configured with dead-letter exchange (`dlx.platform`) and exponential backoff retry policies for poison message quarantine.

## Consequences
- **Positive**: Lightweight operational footprint; built-in web management UI; native AMQP support in Spring Boot; robust queue-based job distribution to worker pools.
- **Negative**: Long-term message retention is limited compared to Kafka, but persistent domain state is stored in PostgreSQL, making RabbitMQ purely an operational task/event transport.
