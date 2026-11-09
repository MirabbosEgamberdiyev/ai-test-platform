package com.platform.ai.application.port;

import com.platform.ai.domain.model.TokenSpendRecord;
import com.platform.common.domain.model.TenantId;

public interface TokenTrackerPort {
    void recordSpend(TokenSpendRecord record);
    long getTenantMonthlyUsage(TenantId tenantId);
}
