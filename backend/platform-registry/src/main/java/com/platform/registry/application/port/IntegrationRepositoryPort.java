package com.platform.registry.application.port;

import com.platform.common.domain.model.TenantId;
import com.platform.registry.domain.model.TenantIntegration;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IntegrationRepositoryPort {
    TenantIntegration save(TenantIntegration integration);
    Optional<TenantIntegration> findById(UUID id);
    Optional<TenantIntegration> findByTenantIdAndWebsiteId(TenantId tenantId, UUID websiteId);
    List<TenantIntegration> findByTenantId(TenantId tenantId);
    void deleteById(UUID id);
}
