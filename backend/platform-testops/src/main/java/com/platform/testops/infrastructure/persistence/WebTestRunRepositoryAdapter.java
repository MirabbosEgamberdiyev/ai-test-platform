package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.application.port.WebTestRunRepositoryPort;
import com.platform.testops.domain.model.WebTestRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface SpringDataWebTestRunRepository extends JpaRepository<WebTestRunJpaEntity, UUID> {
    List<WebTestRunJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    List<WebTestRunJpaEntity> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
    long countByTenantId(UUID tenantId);
}

@Component
public class WebTestRunRepositoryAdapter implements WebTestRunRepositoryPort {

    private final SpringDataWebTestRunRepository repository;

    public WebTestRunRepositoryAdapter(SpringDataWebTestRunRepository repository) {
        this.repository = repository;
    }

    @Override
    public WebTestRun save(WebTestRun run) {
        WebTestRunJpaEntity entity = WebTestRunJpaEntity.fromDomain(run);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<WebTestRun> findById(UUID id) {
        return repository.findById(id).map(WebTestRunJpaEntity::toDomain);
    }

    @Override
    public List<WebTestRun> findByProjectId(UUID projectId) {
        return repository.findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(WebTestRunJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<WebTestRun> findByTenantId(TenantId tenantId) {
        return repository.findByTenantIdOrderByCreatedAtDesc(tenantId.value())
                .stream()
                .map(WebTestRunJpaEntity::toDomain)
                .toList();
    }

    @Override
    public long countByTenantId(TenantId tenantId) {
        return repository.countByTenantId(tenantId.value());
    }
}
