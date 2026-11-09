package com.platform.iam.domain.model;

import com.platform.common.domain.model.TenantId;

import java.time.Instant;
import java.util.Objects;

public class Tenant {
    private final TenantId id;
    private String name;
    private String slug;
    private TenantStatus status;
    private TenantTier tier;
    private long version;
    private final Instant createdAt;
    private Instant updatedAt;

    public Tenant(TenantId id, String name, String slug, TenantStatus status, TenantTier tier, long version, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.slug = Objects.requireNonNull(slug);
        this.status = Objects.requireNonNull(status);
        this.tier = Objects.requireNonNull(tier);
        this.version = version;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static Tenant create(String name, String slug, TenantTier tier) {
        Instant now = Instant.now();
        return new Tenant(
                TenantId.generate(),
                name,
                slug.toLowerCase().trim(),
                TenantStatus.ACTIVE,
                tier != null ? tier : TenantTier.FREE,
                0L,
                now,
                now
        );
    }

    public TenantId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public TenantTier getTier() {
        return tier;
    }

    public long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void updateTier(TenantTier newTier) {
        this.tier = Objects.requireNonNull(newTier);
        this.updatedAt = Instant.now();
    }

    public void suspend() {
        this.status = TenantStatus.SUSPENDED;
        this.updatedAt = Instant.now();
    }
}
