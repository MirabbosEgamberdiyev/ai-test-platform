package com.platform.testops.domain.model;

import com.platform.common.domain.model.TenantId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class WebTestPlan {
    private final UUID id;
    private final UUID projectId;
    private final TenantId tenantId;
    private final String naturalLanguageGoal;
    private String detectedScope;
    private String language;
    private String status; // DRAFT, APPROVED, REJECTED
    private String discoverySummary;
    private long version;
    private final Instant createdAt;
    private Instant updatedAt;

    public WebTestPlan(UUID id, UUID projectId, TenantId tenantId, String naturalLanguageGoal,
                       String detectedScope, String language, String status, String discoverySummary,
                       long version, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId must not be null");
        this.naturalLanguageGoal = Objects.requireNonNull(naturalLanguageGoal, "naturalLanguageGoal must not be null");
        this.detectedScope = detectedScope;
        this.language = language != null ? language : "en";
        this.status = status != null ? status : "DRAFT";
        this.discoverySummary = discoverySummary;
        this.version = version;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    public static WebTestPlan create(UUID projectId, TenantId tenantId, String naturalLanguageGoal,
                                     String detectedScope, String language, String discoverySummary) {
        Instant now = Instant.now();
        return new WebTestPlan(UUID.randomUUID(), projectId, tenantId, naturalLanguageGoal,
                detectedScope, language, "DRAFT", discoverySummary, 0L, now, now);
    }

    public UUID getId() { return id; }
    public UUID getProjectId() { return projectId; }
    public TenantId getTenantId() { return tenantId; }
    public String getNaturalLanguageGoal() { return naturalLanguageGoal; }
    public String getDetectedScope() { return detectedScope; }
    public String getLanguage() { return language; }
    public String getStatus() { return status; }
    public String getDiscoverySummary() { return discoverySummary; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void approve() {
        this.status = "APPROVED";
        this.updatedAt = Instant.now();
    }

    public void reject() {
        this.status = "REJECTED";
        this.updatedAt = Instant.now();
    }
}
