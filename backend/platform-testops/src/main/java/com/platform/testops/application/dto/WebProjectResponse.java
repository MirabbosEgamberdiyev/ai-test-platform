package com.platform.testops.application.dto;

import com.platform.testops.domain.model.WebTestProject;

import java.time.Instant;
import java.util.UUID;

public record WebProjectResponse(
        UUID id,
        UUID tenantId,
        UUID websiteId,
        String name,
        String targetUrl,
        String environment,
        String description,
        String instructions,
        boolean isAuthorized,
        Instant createdAt,
        Instant updatedAt
) {
    public static WebProjectResponse fromDomain(WebTestProject domain) {
        return new WebProjectResponse(
                domain.getId(),
                domain.getTenantId().value(),
                domain.getWebsiteId(),
                domain.getName(),
                domain.getTargetUrl(),
                domain.getEnvironment(),
                domain.getDescription(),
                domain.getInstructions(),
                domain.isAuthorized(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
