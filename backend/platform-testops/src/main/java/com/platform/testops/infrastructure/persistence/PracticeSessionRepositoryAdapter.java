package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.testops.application.port.PracticeSessionRepositoryPort;
import com.platform.testops.domain.model.PracticeSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface SpringDataPracticeSessionRepository extends JpaRepository<PracticeSessionJpaEntity, UUID> {
    List<PracticeSessionJpaEntity> findByTenantIdAndUserId(UUID tenantId, UUID userId);
}

@Component
public class PracticeSessionRepositoryAdapter implements PracticeSessionRepositoryPort {

    private final SpringDataPracticeSessionRepository repository;

    public PracticeSessionRepositoryAdapter(SpringDataPracticeSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public PracticeSession save(PracticeSession session) {
        PracticeSessionJpaEntity entity = PracticeSessionJpaEntity.fromDomain(session);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<PracticeSession> findById(UUID id) {
        return repository.findById(id).map(PracticeSessionJpaEntity::toDomain);
    }

    @Override
    public List<PracticeSession> findByTenantIdAndUserId(TenantId tenantId, UserId userId) {
        return repository.findByTenantIdAndUserId(tenantId.value(), userId.value()).stream()
                .map(PracticeSessionJpaEntity::toDomain)
                .toList();
    }
}
