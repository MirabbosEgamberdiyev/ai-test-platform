package com.platform.registry.application.dto;

import com.platform.registry.domain.model.TenantIntegration;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record IntegrationResponse(
        UUID id,
        UUID tenantId,
        UUID websiteId,
        String authType,
        String status,
        String configJson,
        Instant createdAt
) implements Serializable {

    public static IntegrationResponse fromDomain(TenantIntegration integration) {
        return new IntegrationResponse(
                integration.getId(),
                integration.getTenantId().value(),
                integration.getWebsiteId(),
                integration.getAuthType().name(),
                integration.getStatus(),
                integration.getConfigJson(),
                integration.getCreatedAt()
        );
    }
}
