package com.platform.testops.application.port;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.testops.domain.model.PracticeResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PracticeResultRepositoryPort {
    PracticeResult save(PracticeResult result);
    Optional<PracticeResult> findById(UUID id);
    Optional<PracticeResult> findBySessionId(UUID sessionId);
    List<PracticeResult> findByTenantIdAndUserId(TenantId tenantId, UserId userId);
}
