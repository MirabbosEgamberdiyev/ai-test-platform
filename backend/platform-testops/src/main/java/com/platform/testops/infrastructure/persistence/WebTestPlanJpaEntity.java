package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.testops.domain.model.WebTestPlan;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "web_test_plans")
public class WebTestPlanJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "natural_language_goal", nullable = false, columnDefinition = "TEXT")
    private String naturalLanguageGoal;

    @Column(name = "detected_scope", length = 200)
    private String detectedScope;

    @Column(name = "language", nullable = false, length = 20)
    private String language;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "discovery_summary", columnDefinition = "TEXT")
    private String discoverySummary;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public WebTestPlanJpaEntity() {}

    public static WebTestPlanJpaEntity fromDomain(WebTestPlan domain) {
        WebTestPlanJpaEntity entity = new WebTestPlanJpaEntity();
        entity.id = domain.getId();
        entity.projectId = domain.getProjectId();
        entity.tenantId = domain.getTenantId().value();
        entity.naturalLanguageGoal = domain.getNaturalLanguageGoal();
        entity.detectedScope = domain.getDetectedScope();
        entity.language = domain.getLanguage();
        entity.status = domain.getStatus();
        entity.discoverySummary = domain.getDiscoverySummary();
        entity.version = domain.getVersion();
        entity.createdAt = domain.getCreatedAt();
        entity.updatedAt = domain.getUpdatedAt();
        return entity;
    }

    public WebTestPlan toDomain() {
        return new WebTestPlan(
                this.id,
                this.projectId,
                TenantId.of(this.tenantId),
                this.naturalLanguageGoal,
                this.detectedScope,
                this.language,
                this.status,
                this.discoverySummary,
                this.version,
                this.createdAt,
                this.updatedAt
        );
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public String getNaturalLanguageGoal() { return naturalLanguageGoal; }
    public void setNaturalLanguageGoal(String naturalLanguageGoal) { this.naturalLanguageGoal = naturalLanguageGoal; }
    public String getDetectedScope() { return detectedScope; }
    public void setDetectedScope(String detectedScope) { this.detectedScope = detectedScope; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDiscoverySummary() { return discoverySummary; }
    public void setDiscoverySummary(String discoverySummary) { this.discoverySummary = discoverySummary; }
    public long getVersion() { return version; }
    public void setVersion(long version) { this.version = version; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
