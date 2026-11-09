package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.testops.application.port.PracticeResultRepositoryPort;
import com.platform.testops.domain.model.PracticeResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface SpringDataPracticeResultRepository extends JpaRepository<PracticeResultJpaEntity, UUID> {
    Optional<PracticeResultJpaEntity> findBySessionId(UUID sessionId);
    List<PracticeResultJpaEntity> findByTenantIdAndUserId(UUID tenantId, UUID userId);
}

@Component
public class PracticeResultRepositoryAdapter implements PracticeResultRepositoryPort {

    private final SpringDataPracticeResultRepository repository;

    public PracticeResultRepositoryAdapter(SpringDataPracticeResultRepository repository) {
        this.repository = repository;
    }

    @Override
    public PracticeResult save(PracticeResult result) {
        PracticeResultJpaEntity entity = PracticeResultJpaEntity.fromDomain(result);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<PracticeResult> findById(UUID id) {
        return repository.findById(id).map(PracticeResultJpaEntity::toDomain);
    }

    @Override
    public Optional<PracticeResult> findBySessionId(UUID sessionId) {
        return repository.findBySessionId(sessionId).map(PracticeResultJpaEntity::toDomain);
    }

    @Override
    public List<PracticeResult> findByTenantIdAndUserId(TenantId tenantId, UserId userId) {
        return repository.findByTenantIdAndUserId(tenantId.value(), userId.value()).stream()
                .map(PracticeResultJpaEntity::toDomain)
                .toList();
    }
}
