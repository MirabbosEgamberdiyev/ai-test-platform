package com.platform.common.domain.event;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
