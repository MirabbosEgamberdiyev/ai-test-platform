package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.application.port.WebTestPlanRepositoryPort;
import com.platform.testops.domain.model.WebTestPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface SpringDataWebTestPlanRepository extends JpaRepository<WebTestPlanJpaEntity, UUID> {
    List<WebTestPlanJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    List<WebTestPlanJpaEntity> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
}

@Component
public class WebTestPlanRepositoryAdapter implements WebTestPlanRepositoryPort {

    private final SpringDataWebTestPlanRepository repository;

    public WebTestPlanRepositoryAdapter(SpringDataWebTestPlanRepository repository) {
        this.repository = repository;
    }

    @Override
    public WebTestPlan save(WebTestPlan plan) {
        WebTestPlanJpaEntity entity = WebTestPlanJpaEntity.fromDomain(plan);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<WebTestPlan> findById(UUID id) {
        return repository.findById(id).map(WebTestPlanJpaEntity::toDomain);
    }

    @Override
    public List<WebTestPlan> findByProjectId(UUID projectId) {
        return repository.findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(WebTestPlanJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<WebTestPlan> findByTenantId(TenantId tenantId) {
        return repository.findByTenantIdOrderByCreatedAtDesc(tenantId.value())
                .stream()
                .map(WebTestPlanJpaEntity::toDomain)
                .toList();
    }
}
