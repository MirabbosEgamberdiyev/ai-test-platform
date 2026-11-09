package com.platform.testops.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class OutboxEvent {
    private final UUID id;
    private final String aggregateType;
    private final String aggregateId;
    private final String eventType;
    private final String payloadJson;
    private boolean processed;
    private final Instant scheduledAt;
    private final Instant createdAt;

    public OutboxEvent(UUID id, String aggregateType, String aggregateId, String eventType,
                       String payloadJson, boolean processed, Instant scheduledAt, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.aggregateType = Objects.requireNonNull(aggregateType);
        this.aggregateId = Objects.requireNonNull(aggregateId);
        this.eventType = Objects.requireNonNull(eventType);
        this.payloadJson = Objects.requireNonNull(payloadJson);
        this.processed = processed;
        this.scheduledAt = Objects.requireNonNull(scheduledAt);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static OutboxEvent create(String aggregateType, String aggregateId, String eventType, String payloadJson) {
        Instant now = Instant.now();
        return new OutboxEvent(
                UUID.randomUUID(),
                aggregateType,
                aggregateId,
                eventType,
                payloadJson,
                false,
                now,
                now
        );
    }

    public void markProcessed() {
        this.processed = true;
    }

    public UUID getId() { return id; }
    public String getAggregateType() { return aggregateType; }
    public String getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public String getPayloadJson() { return payloadJson; }
    public boolean isProcessed() { return processed; }
    public Instant getScheduledAt() { return scheduledAt; }
    public Instant getCreatedAt() { return createdAt; }
}
