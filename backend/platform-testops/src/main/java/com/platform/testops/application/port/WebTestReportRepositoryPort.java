package com.platform.testops.application.port;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.domain.model.WebTestReport;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebTestReportRepositoryPort {
    WebTestReport save(WebTestReport report);
    Optional<WebTestReport> findById(UUID id);
    Optional<WebTestReport> findByRunId(UUID runId);
    List<WebTestReport> findByTenantId(TenantId tenantId);
}
