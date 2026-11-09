package com.platform.testops.application.port;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.domain.model.WebTestCase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebTestCaseRepositoryPort {
    WebTestCase save(WebTestCase testCase);
    List<WebTestCase> saveAll(List<WebTestCase> testCases);
    Optional<WebTestCase> findById(UUID id);
    List<WebTestCase> findByPlanId(UUID planId);
    List<WebTestCase> findByTenantId(TenantId tenantId);
    void deleteByPlanId(UUID planId);
}
