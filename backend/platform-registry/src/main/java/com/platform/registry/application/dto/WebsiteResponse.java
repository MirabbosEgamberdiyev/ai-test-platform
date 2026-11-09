package com.platform.registry.application.dto;

import com.platform.registry.domain.model.RegisteredWebsite;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record WebsiteResponse(
        UUID id,
        String domainName,
        String displayName,
        String baseUrl,
        String platformType,
        String complianceStatus,
        boolean ssrfVerified,
        Instant createdAt
) implements Serializable {

    public static WebsiteResponse fromDomain(RegisteredWebsite site) {
        return new WebsiteResponse(
                site.getId(),
                site.getDomainName(),
                site.getDisplayName(),
                site.getBaseUrl(),
                site.getPlatformType().name(),
                site.getComplianceStatus().name(),
                site.isSsrfVerified(),
                site.getCreatedAt()
        );
    }
}
