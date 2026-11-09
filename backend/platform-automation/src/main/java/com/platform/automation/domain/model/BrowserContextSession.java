package com.platform.automation.domain.model;

import com.platform.common.domain.model.TenantId;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class BrowserContextSession implements Serializable {
    private final UUID sessionId;
    private final TenantId tenantId;
    private final String targetUrl;
    private BrowserSessionStatus status;
    private final Instant createdAt;
    private Instant lastActivityAt;

    public BrowserContextSession(UUID sessionId, TenantId tenantId, String targetUrl, BrowserSessionStatus status, Instant createdAt, Instant lastActivityAt) {
        this.sessionId = Objects.requireNonNull(sessionId);
        this.tenantId = Objects.requireNonNull(tenantId);
        this.targetUrl = Objects.requireNonNull(targetUrl);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.lastActivityAt = Objects.requireNonNull(lastActivityAt);
    }

    public static BrowserContextSession create(UUID sessionId, TenantId tenantId, String targetUrl) {
        Instant now = Instant.now();
        return new BrowserContextSession(sessionId, tenantId, targetUrl, BrowserSessionStatus.INITIALIZING, now, now);
    }

    public UUID getSessionId() { return sessionId; }
    public TenantId getTenantId() { return tenantId; }
    public String getTargetUrl() { return targetUrl; }
    public BrowserSessionStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastActivityAt() { return lastActivityAt; }

    public void updateStatus(BrowserSessionStatus newStatus) {
        this.status = newStatus;
        this.lastActivityAt = Instant.now();
    }

    public boolean isExpired(long timeoutSeconds) {
        return Instant.now().isAfter(lastActivityAt.plusSeconds(timeoutSeconds));
    }
}
