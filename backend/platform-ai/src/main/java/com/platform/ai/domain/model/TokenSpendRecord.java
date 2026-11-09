package com.platform.ai.domain.model;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TokenSpendRecord(
        UUID id,
        TenantId tenantId,
        UserId userId,
        String provider,
        String model,
        int inputTokens,
        int outputTokens,
        BigDecimal estimatedCostUsd,
        Instant timestamp
) implements Serializable {

    public static TokenSpendRecord create(TenantId tenantId, UserId userId, String provider, String model,
                                          int inputTokens, int outputTokens, BigDecimal costPer1kInput, BigDecimal costPer1kOutput) {
        BigDecimal inputCost = BigDecimal.valueOf(inputTokens).multiply(costPer1kInput).divide(BigDecimal.valueOf(1000));
        BigDecimal outputCost = BigDecimal.valueOf(outputTokens).multiply(costPer1kOutput).divide(BigDecimal.valueOf(1000));
        BigDecimal totalCost = inputCost.add(outputCost);

        return new TokenSpendRecord(
                UUID.randomUUID(),
                tenantId,
                userId,
                provider,
                model,
                inputTokens,
                outputTokens,
                totalCost,
                Instant.now()
        );
    }
}
