package com.platform.iam.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.iam.domain.model.Tenant;
import com.platform.iam.domain.model.TenantStatus;
import com.platform.iam.domain.model.TenantTier;
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
@Table(name = "iam_tenants")
public class TenantJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 50)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TenantStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false, length = 30)
    private TenantTier tier;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public TenantJpaEntity() {}

    public static TenantJpaEntity fromDomain(Tenant domain) {
        TenantJpaEntity entity = new TenantJpaEntity();
        entity.id = domain.getId().value();
        entity.name = domain.getName();
        entity.slug = domain.getSlug();
        entity.status = domain.getStatus();
        entity.tier = domain.getTier();
        entity.version = domain.getVersion();
        entity.createdAt = domain.getCreatedAt();
        entity.updatedAt = domain.getUpdatedAt();
        return entity;
    }

    public Tenant toDomain() {
        return new Tenant(
                TenantId.of(this.id),
                this.name,
                this.slug,
                this.status,
                this.tier,
                this.version != null ? this.version : 0L,
                this.createdAt,
                this.updatedAt
        );
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public TenantStatus getStatus() { return status; }
    public TenantTier getTier() { return tier; }
    public Long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
