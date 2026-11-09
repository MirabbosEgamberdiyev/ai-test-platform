package com.platform.billing.infrastructure.persistence;

import com.platform.billing.domain.model.TenantSubscription;
import com.platform.common.domain.model.TenantId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "billing_subscriptions")
public class SubscriptionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, unique = true)
    private UUID tenantId;

    @Column(name = "plan_tier", nullable = false, length = 50)
    private String planTier;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "monthly_token_limit", nullable = false)
    private long monthlyTokenLimit;

    @Column(name = "used_tokens", nullable = false)
    private long usedTokens;

    @Column(name = "concurrent_sessions_limit", nullable = false)
    private int concurrentSessionsLimit;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public SubscriptionJpaEntity() {}

    public static SubscriptionJpaEntity fromDomain(TenantSubscription domain) {
        SubscriptionJpaEntity entity = new SubscriptionJpaEntity();
        entity.id = domain.getId();
        entity.tenantId = domain.getTenantId().value();
        entity.planTier = domain.getPlanTier();
        entity.status = domain.getStatus();
        entity.monthlyTokenLimit = domain.getMonthlyTokenLimit();
        entity.usedTokens = domain.getUsedTokens();
        entity.concurrentSessionsLimit = domain.getConcurrentSessionsLimit();
        entity.createdAt = domain.getCreatedAt();
        return entity;
    }

    public TenantSubscription toDomain() {
        return new TenantSubscription(
                this.id,
                TenantId.of(this.tenantId),
                this.planTier,
                this.status,
                this.monthlyTokenLimit,
                this.usedTokens,
                this.concurrentSessionsLimit,
                this.createdAt
        );
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public String getPlanTier() { return planTier; }
    public String getStatus() { return status; }
    public long getMonthlyTokenLimit() { return monthlyTokenLimit; }
    public long getUsedTokens() { return usedTokens; }
    public int getConcurrentSessionsLimit() { return concurrentSessionsLimit; }
    public Instant getCreatedAt() { return createdAt; }
}
