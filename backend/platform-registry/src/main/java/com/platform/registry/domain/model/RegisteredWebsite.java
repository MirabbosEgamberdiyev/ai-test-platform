package com.platform.registry.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class RegisteredWebsite {
    private final UUID id;
    private final String domainName;
    private String displayName;
    private String baseUrl;
    private PlatformType platformType;
    private ComplianceStatus complianceStatus;
    private boolean ssrfVerified;
    private long version;
    private final Instant createdAt;
    private Instant updatedAt;

    public RegisteredWebsite(UUID id, String domainName, String displayName, String baseUrl,
                             PlatformType platformType, ComplianceStatus complianceStatus,
                             boolean ssrfVerified, long version, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.domainName = Objects.requireNonNull(domainName).toLowerCase().trim();
        this.displayName = Objects.requireNonNull(displayName).trim();
        this.baseUrl = Objects.requireNonNull(baseUrl).trim();
        this.platformType = Objects.requireNonNull(platformType);
        this.complianceStatus = Objects.requireNonNull(complianceStatus);
        this.ssrfVerified = ssrfVerified;
        this.version = version;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static RegisteredWebsite create(String domainName, String displayName, String baseUrl, PlatformType platformType) {
        Instant now = Instant.now();
        return new RegisteredWebsite(
                UUID.randomUUID(),
                domainName,
                displayName,
                baseUrl,
                platformType != null ? platformType : PlatformType.GENERIC_LMS,
                ComplianceStatus.APPROVED,
                true,
                0L,
                now,
                now
        );
    }

    public UUID getId() { return id; }
    public String getDomainName() { return domainName; }
    public String getDisplayName() { return displayName; }
    public String getBaseUrl() { return baseUrl; }
    public PlatformType getPlatformType() { return platformType; }
    public ComplianceStatus getComplianceStatus() { return complianceStatus; }
    public boolean isSsrfVerified() { return ssrfVerified; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public boolean isAllowed() {
        return complianceStatus == ComplianceStatus.APPROVED && ssrfVerified;
    }

    public void block(String reason) {
        this.complianceStatus = ComplianceStatus.BLOCKED;
        this.updatedAt = Instant.now();
    }
}
