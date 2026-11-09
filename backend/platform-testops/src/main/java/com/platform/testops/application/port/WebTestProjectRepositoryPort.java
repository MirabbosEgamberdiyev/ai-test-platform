package com.platform.testops.application.port;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.domain.model.WebTestProject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebTestProjectRepositoryPort {
    WebTestProject save(WebTestProject project);
    Optional<WebTestProject> findById(UUID id);
    List<WebTestProject> findByTenantId(TenantId tenantId);
    void deleteById(UUID id);
}
