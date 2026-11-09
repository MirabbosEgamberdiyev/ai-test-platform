package com.platform.testops.infrastructure.persistence;

import com.platform.testops.application.port.OutboxEventRepositoryPort;
import com.platform.testops.domain.model.OutboxEvent;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface SpringDataOutboxEventRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {
    @Query("SELECT e FROM OutboxEventJpaEntity e WHERE e.processed = false ORDER BY e.scheduledAt ASC")
    List<OutboxEventJpaEntity> findPending(PageRequest pageRequest);
}

@Component
public class OutboxEventRepositoryAdapter implements OutboxEventRepositoryPort {

    private final SpringDataOutboxEventRepository repository;

    public OutboxEventRepositoryAdapter(SpringDataOutboxEventRepository repository) {
        this.repository = repository;
    }

    @Override
    public OutboxEvent save(OutboxEvent event) {
        OutboxEventJpaEntity entity = OutboxEventJpaEntity.fromDomain(event);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<OutboxEvent> findById(UUID id) {
        return repository.findById(id).map(OutboxEventJpaEntity::toDomain);
    }

    @Override
    public List<OutboxEvent> findPendingEvents(int limit) {
        return repository.findPending(PageRequest.of(0, limit)).stream()
                .map(OutboxEventJpaEntity::toDomain)
                .toList();
    }
}
