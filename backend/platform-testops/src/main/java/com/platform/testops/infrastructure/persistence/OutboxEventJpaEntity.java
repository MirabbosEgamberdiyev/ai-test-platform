package com.platform.testops.infrastructure.persistence;

import com.platform.testops.domain.model.OutboxEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEventJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 100)
    private String aggregateId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "payload_json", nullable = false, columnDefinition = "TEXT")
    private String payloadJson;

    @Column(name = "processed", nullable = false)
    private boolean processed;

    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public OutboxEventJpaEntity() {}

    public static OutboxEventJpaEntity fromDomain(OutboxEvent domain) {
        OutboxEventJpaEntity entity = new OutboxEventJpaEntity();
        entity.id = domain.getId();
        entity.aggregateType = domain.getAggregateType();
        entity.aggregateId = domain.getAggregateId();
        entity.eventType = domain.getEventType();
        entity.payloadJson = domain.getPayloadJson();
        entity.processed = domain.isProcessed();
        entity.scheduledAt = domain.getScheduledAt();
        entity.createdAt = domain.getCreatedAt();
        return entity;
    }

    public OutboxEvent toDomain() {
        return new OutboxEvent(
                this.id,
                this.aggregateType,
                this.aggregateId,
                this.eventType,
                this.payloadJson,
                this.processed,
                this.scheduledAt,
                this.createdAt
        );
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
