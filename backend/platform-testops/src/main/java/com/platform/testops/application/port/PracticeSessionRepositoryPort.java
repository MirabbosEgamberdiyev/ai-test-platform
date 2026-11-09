package com.platform.testops.application.port;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.testops.domain.model.PracticeSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PracticeSessionRepositoryPort {
    PracticeSession save(PracticeSession session);
    Optional<PracticeSession> findById(UUID id);
    List<PracticeSession> findByTenantIdAndUserId(TenantId tenantId, UserId userId);
}
