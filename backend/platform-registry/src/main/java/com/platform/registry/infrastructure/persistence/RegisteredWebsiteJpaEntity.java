package com.platform.registry.infrastructure.persistence;

import com.platform.registry.domain.model.ComplianceStatus;
import com.platform.registry.domain.model.PlatformType;
import com.platform.registry.domain.model.RegisteredWebsite;
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
@Table(name = "registry_websites")
public class RegisteredWebsiteJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "domain_name", nullable = false, unique = true, length = 150)
    private String domainName;

    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    @Column(name = "base_url", nullable = false, length = 500)
    private String baseUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform_type", nullable = false, length = 50)
    private PlatformType platformType;

    @Enumerated(EnumType.STRING)
    @Column(name = "compliance_status", nullable = false, length = 30)
    private ComplianceStatus complianceStatus;

    @Column(name = "ssrf_verified", nullable = false)
    private boolean ssrfVerified;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public RegisteredWebsiteJpaEntity() {}

    public static RegisteredWebsiteJpaEntity fromDomain(RegisteredWebsite domain) {
        RegisteredWebsiteJpaEntity entity = new RegisteredWebsiteJpaEntity();
        entity.id = domain.getId();
        entity.domainName = domain.getDomainName();
        entity.displayName = domain.getDisplayName();
        entity.baseUrl = domain.getBaseUrl();
        entity.platformType = domain.getPlatformType();
        entity.complianceStatus = domain.getComplianceStatus();
        entity.ssrfVerified = domain.isSsrfVerified();
        entity.version = domain.getVersion();
        entity.createdAt = domain.getCreatedAt();
        entity.updatedAt = domain.getUpdatedAt();
        return entity;
    }

    public RegisteredWebsite toDomain() {
        return new RegisteredWebsite(
                this.id,
                this.domainName,
                this.displayName,
                this.baseUrl,
                this.platformType,
                this.complianceStatus,
                this.ssrfVerified,
                this.version != null ? this.version : 0L,
                this.createdAt,
                this.updatedAt
        );
    }

    public UUID getId() { return id; }
    public String getDomainName() { return domainName; }
    public String getDisplayName() { return displayName; }
    public String getBaseUrl() { return baseUrl; }
    public PlatformType getPlatformType() { return platformType; }
    public ComplianceStatus getComplianceStatus() { return complianceStatus; }
    public boolean isSsrfVerified() { return ssrfVerified; }
    public Long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
