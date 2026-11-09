package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.application.port.WebDefectRepositoryPort;
import com.platform.testops.domain.model.WebDefect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface SpringDataWebDefectRepository extends JpaRepository<WebDefectJpaEntity, UUID> {
    List<WebDefectJpaEntity> findByRunIdOrderByCreatedAtDesc(UUID runId);
    List<WebDefectJpaEntity> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
    long countByTenantId(UUID tenantId);
}

@Component
public class WebDefectRepositoryAdapter implements WebDefectRepositoryPort {

    private final SpringDataWebDefectRepository repository;

    public WebDefectRepositoryAdapter(SpringDataWebDefectRepository repository) {
        this.repository = repository;
    }

    @Override
    public WebDefect save(WebDefect defect) {
        WebDefectJpaEntity entity = WebDefectJpaEntity.fromDomain(defect);
        return repository.save(entity).toDomain();
    }

    @Override
    public List<WebDefect> saveAll(List<WebDefect> defects) {
        List<WebDefectJpaEntity> entities = defects.stream().map(WebDefectJpaEntity::fromDomain).toList();
        return repository.saveAll(entities).stream().map(WebDefectJpaEntity::toDomain).toList();
    }

    @Override
    public Optional<WebDefect> findById(UUID id) {
        return repository.findById(id).map(WebDefectJpaEntity::toDomain);
    }

    @Override
    public List<WebDefect> findByRunId(UUID runId) {
        return repository.findByRunIdOrderByCreatedAtDesc(runId)
                .stream()
                .map(WebDefectJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<WebDefect> findByTenantId(TenantId tenantId) {
        return repository.findByTenantIdOrderByCreatedAtDesc(tenantId.value())
                .stream()
                .map(WebDefectJpaEntity::toDomain)
                .toList();
    }

    @Override
    public long countByTenantId(TenantId tenantId) {
        return repository.countByTenantId(tenantId.value());
    }
}
