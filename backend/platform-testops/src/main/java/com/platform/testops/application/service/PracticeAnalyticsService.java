package com.platform.testops.application.service;

import com.platform.common.domain.exception.EntityNotFoundException;
import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.testops.application.dto.PracticeResultResponse;
import com.platform.testops.application.port.PracticeResultRepositoryPort;
import com.platform.testops.domain.model.PracticeResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PracticeAnalyticsService {

    private final PracticeResultRepositoryPort resultRepository;

    public PracticeAnalyticsService(PracticeResultRepositoryPort resultRepository) {
        this.resultRepository = resultRepository;
    }

    public PracticeResultResponse getResultForSession(UUID sessionId) {
        PracticeResult result = resultRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("PracticeResult for session", sessionId));
        return PracticeResultResponse.fromDomain(result);
    }

    public List<PracticeResultResponse> listResultsForUser(TenantId tenantId, UserId userId) {
        return resultRepository.findByTenantIdAndUserId(tenantId, userId).stream()
                .map(PracticeResultResponse::fromDomain)
                .toList();
    }
}
