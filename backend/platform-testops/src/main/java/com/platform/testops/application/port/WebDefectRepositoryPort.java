package com.platform.testops.application.port;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.domain.model.WebDefect;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebDefectRepositoryPort {
    WebDefect save(WebDefect defect);
    List<WebDefect> saveAll(List<WebDefect> defects);
    Optional<WebDefect> findById(UUID id);
    List<WebDefect> findByRunId(UUID runId);
    List<WebDefect> findByTenantId(TenantId tenantId);
    long countByTenantId(TenantId tenantId);
}
