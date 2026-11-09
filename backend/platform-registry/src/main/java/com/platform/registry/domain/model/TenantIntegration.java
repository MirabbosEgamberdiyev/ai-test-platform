package com.platform.registry.domain.model;

import com.platform.common.domain.model.TenantId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class TenantIntegration {
    private final UUID id;
    private final TenantId tenantId;
    private final UUID websiteId;
    private IntegrationAuthType authType;
    private String status;
    private String configJson;
    private long version;
    private final Instant createdAt;
    private Instant updatedAt;

    public TenantIntegration(UUID id, TenantId tenantId, UUID websiteId, IntegrationAuthType authType,
                             String status, String configJson, long version, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.tenantId = Objects.requireNonNull(tenantId);
        this.websiteId = Objects.requireNonNull(websiteId);
        this.authType = Objects.requireNonNull(authType);
        this.status = Objects.requireNonNull(status);
        this.configJson = configJson;
        this.version = version;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static TenantIntegration create(TenantId tenantId, UUID websiteId, IntegrationAuthType authType, String configJson) {
        Instant now = Instant.now();
        return new TenantIntegration(
                UUID.randomUUID(),
                tenantId,
                websiteId,
                authType != null ? authType : IntegrationAuthType.USER_ASSISTED,
                "ACTIVE",
                configJson != null ? configJson : "{}",
                0L,
                now,
                now
        );
    }

    public UUID getId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public UUID getWebsiteId() { return websiteId; }
    public IntegrationAuthType getAuthType() { return authType; }
    public String getStatus() { return status; }
    public String getConfigJson() { return configJson; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void updateConfig(String newConfigJson) {
        this.configJson = newConfigJson;
        this.updatedAt = Instant.now();
    }
}
