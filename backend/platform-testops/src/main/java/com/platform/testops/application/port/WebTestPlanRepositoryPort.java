package com.platform.testops.application.port;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.domain.model.WebTestPlan;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebTestPlanRepositoryPort {
    WebTestPlan save(WebTestPlan plan);
    Optional<WebTestPlan> findById(UUID id);
    List<WebTestPlan> findByProjectId(UUID projectId);
    List<WebTestPlan> findByTenantId(TenantId tenantId);
}
