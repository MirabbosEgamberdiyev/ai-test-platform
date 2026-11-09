package com.platform.registry.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.registry.application.port.IntegrationRepositoryPort;
import com.platform.registry.domain.model.TenantIntegration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface SpringDataIntegrationRepository extends JpaRepository<TenantIntegrationJpaEntity, UUID> {
    Optional<TenantIntegrationJpaEntity> findByTenantIdAndWebsiteId(UUID tenantId, UUID websiteId);
    List<TenantIntegrationJpaEntity> findByTenantId(UUID tenantId);
}

@Component
public class IntegrationRepositoryAdapter implements IntegrationRepositoryPort {

    private final SpringDataIntegrationRepository repository;

    public IntegrationRepositoryAdapter(SpringDataIntegrationRepository repository) {
        this.repository = repository;
    }

    @Override
    public TenantIntegration save(TenantIntegration integration) {
        TenantIntegrationJpaEntity entity = TenantIntegrationJpaEntity.fromDomain(integration);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<TenantIntegration> findById(UUID id) {
        return repository.findById(id).map(TenantIntegrationJpaEntity::toDomain);
    }

    @Override
    public Optional<TenantIntegration> findByTenantIdAndWebsiteId(TenantId tenantId, UUID websiteId) {
        return repository.findByTenantIdAndWebsiteId(tenantId.value(), websiteId).map(TenantIntegrationJpaEntity::toDomain);
    }

    @Override
    public List<TenantIntegration> findByTenantId(TenantId tenantId) {
        return repository.findByTenantId(tenantId.value()).stream().map(TenantIntegrationJpaEntity::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
