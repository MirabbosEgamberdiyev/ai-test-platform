package com.platform.iam.application.port;

import com.platform.common.domain.model.TenantId;
import com.platform.iam.domain.model.Tenant;

import java.util.Optional;

public interface TenantRepositoryPort {
    Tenant save(Tenant tenant);
    Optional<Tenant> findById(TenantId id);
    Optional<Tenant> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
