package com.platform.billing.domain.model;

import com.platform.common.domain.exception.QuotaExceededException;
import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.TokenBudget;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class TenantSubscription {
    private final UUID id;
    private final TenantId tenantId;
    private String planTier;
    private String status;
    private long monthlyTokenLimit;
    private long usedTokens;
    private int concurrentSessionsLimit;
    private final Instant createdAt;

    public TenantSubscription(UUID id, TenantId tenantId, String planTier, String status,
                              long monthlyTokenLimit, long usedTokens, int concurrentSessionsLimit, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.tenantId = Objects.requireNonNull(tenantId);
        this.planTier = Objects.requireNonNull(planTier);
        this.status = Objects.requireNonNull(status);
        this.monthlyTokenLimit = monthlyTokenLimit;
        this.usedTokens = usedTokens;
        this.concurrentSessionsLimit = concurrentSessionsLimit;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static TenantSubscription createDefault(TenantId tenantId) {
        return new TenantSubscription(
                UUID.randomUUID(),
                tenantId,
                "FREE",
                "ACTIVE",
                50_000L, // 50k tokens free tier
                0L,
                2,       // 2 concurrent sessions
                Instant.now()
        );
    }

    public void checkAndConsumeTokens(long tokens) {
        if (usedTokens + tokens > monthlyTokenLimit) {
            throw new QuotaExceededException(String.format(
                    "Monthly AI token limit reached (%d / %d). Please upgrade subscription.",
                    usedTokens, monthlyTokenLimit
            ));
        }
        this.usedTokens += tokens;
    }

    public TokenBudget toTokenBudget() {
        return new TokenBudget(monthlyTokenLimit, usedTokens);
    }

    public UUID getId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public String getPlanTier() { return planTier; }
    public String getStatus() { return status; }
    public long getMonthlyTokenLimit() { return monthlyTokenLimit; }
    public long getUsedTokens() { return usedTokens; }
    public int getConcurrentSessionsLimit() { return concurrentSessionsLimit; }
    public Instant getCreatedAt() { return createdAt; }
}
