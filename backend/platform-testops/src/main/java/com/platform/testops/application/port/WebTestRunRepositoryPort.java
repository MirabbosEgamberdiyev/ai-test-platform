package com.platform.testops.application.port;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.domain.model.WebTestRun;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebTestRunRepositoryPort {
    WebTestRun save(WebTestRun run);
    Optional<WebTestRun> findById(UUID id);
    List<WebTestRun> findByProjectId(UUID projectId);
    List<WebTestRun> findByTenantId(TenantId tenantId);
    long countByTenantId(TenantId tenantId);
}
