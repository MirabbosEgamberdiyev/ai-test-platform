package com.platform.registry.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.registry.domain.model.IntegrationAuthType;
import com.platform.registry.domain.model.TenantIntegration;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "registry_integrations")
public class TenantIntegrationJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "website_id", nullable = false)
    private UUID websiteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", nullable = false, length = 50)
    private IntegrationAuthType authType;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "config_json", columnDefinition = "TEXT")
    private String configJson;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public TenantIntegrationJpaEntity() {}

    public static TenantIntegrationJpaEntity fromDomain(TenantIntegration domain) {
        TenantIntegrationJpaEntity entity = new TenantIntegrationJpaEntity();
        entity.id = domain.getId();
        entity.tenantId = domain.getTenantId().value();
        entity.websiteId = domain.getWebsiteId();
        entity.authType = domain.getAuthType();
        entity.status = domain.getStatus();
        entity.configJson = domain.getConfigJson();
        entity.version = domain.getVersion();
        entity.createdAt = domain.getCreatedAt();
        entity.updatedAt = domain.getUpdatedAt();
        return entity;
    }

    public TenantIntegration toDomain() {
        return new TenantIntegration(
                this.id,
                TenantId.of(this.tenantId),
                this.websiteId,
                this.authType,
                this.status,
                this.configJson,
                this.version != null ? this.version : 0L,
                this.createdAt,
                this.updatedAt
        );
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public UUID getWebsiteId() { return websiteId; }
    public IntegrationAuthType getAuthType() { return authType; }
    public String getStatus() { return status; }
    public String getConfigJson() { return configJson; }
    public Long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
