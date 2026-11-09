package com.platform.testops.domain.model;

import com.platform.common.domain.model.TenantId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class WebTestProject {
    private final UUID id;
    private final TenantId tenantId;
    private final UUID websiteId;
    private String name;
    private String targetUrl;
    private String environment;
    private String description;
    private String instructions;
    private boolean isAuthorized;
    private long version;
    private final Instant createdAt;
    private Instant updatedAt;

    public WebTestProject(UUID id, TenantId tenantId, UUID websiteId, String name, String targetUrl,
                          String environment, String description, String instructions,
                          boolean isAuthorized, long version, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId must not be null");
        this.websiteId = websiteId;
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.targetUrl = Objects.requireNonNull(targetUrl, "targetUrl must not be null");
        this.environment = environment != null ? environment : "PRODUCTION";
        this.description = description;
        this.instructions = instructions;
        this.isAuthorized = isAuthorized;
        this.version = version;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    public static WebTestProject create(TenantId tenantId, UUID websiteId, String name, String targetUrl,
                                        String environment, String description, String instructions, boolean isAuthorized) {
        Instant now = Instant.now();
        return new WebTestProject(UUID.randomUUID(), tenantId, websiteId, name, targetUrl, environment,
                description, instructions, isAuthorized, 0L, now, now);
    }

    public UUID getId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public UUID getWebsiteId() { return websiteId; }
    public String getName() { return name; }
    public String getTargetUrl() { return targetUrl; }
    public String getEnvironment() { return environment; }
    public String getDescription() { return description; }
    public String getInstructions() { return instructions; }
    public boolean isAuthorized() { return isAuthorized; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void update(String name, String targetUrl, String environment, String description, String instructions, boolean isAuthorized) {
        this.name = name;
        this.targetUrl = targetUrl;
        this.environment = environment;
        this.description = description;
        this.instructions = instructions;
        this.isAuthorized = isAuthorized;
        this.updatedAt = Instant.now();
    }
}
