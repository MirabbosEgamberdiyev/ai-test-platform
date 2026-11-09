package com.platform.common.domain.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public interface DomainEvent extends Serializable {
    UUID getEventId();
    Instant getOccurredAt();
    String getEventType();
    String getAggregateType();
    String getAggregateId();
}
