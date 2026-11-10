package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.testops.domain.model.UrlDiscoverySession;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "url_discovery_sessions")
public class UrlDiscoverySessionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "target_url", nullable = false, length = 1000)
    private String targetUrl;

    @Column(name = "platform_type", nullable = false, length = 100)
    private String platformType;

    @Column(name = "discovered_title", length = 500)
    private String discoveredTitle;

    @Column(name = "ticket_count", nullable = false)
    private int ticketCount;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UrlDiscoverySessionJpaEntity() {}

    public static UrlDiscoverySessionJpaEntity fromDomain(UrlDiscoverySession domain) {
        UrlDiscoverySessionJpaEntity entity = new UrlDiscoverySessionJpaEntity();
        entity.id = domain.getId();
        entity.tenantId = domain.getTenantId().value();
        entity.userId = domain.getUserId().value();
        entity.targetUrl = domain.getTargetUrl();
        entity.platformType = domain.getPlatformType();
        entity.discoveredTitle = domain.getDiscoveredTitle();
        entity.ticketCount = domain.getTicketCount();
        entity.status = domain.getStatus();
        entity.errorMessage = domain.getErrorMessage();
        entity.createdAt = domain.getCreatedAt();
        entity.updatedAt = domain.getUpdatedAt();
        return entity;
    }

    public UrlDiscoverySession toDomain() {
        return new UrlDiscoverySession(
                id,
                TenantId.of(tenantId),
                UserId.of(userId),
                targetUrl,
                platformType,
                discoveredTitle,
                ticketCount,
                status,
                errorMessage,
                createdAt,
                updatedAt
        );
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public UUID getUserId() { return userId; }
    public String getTargetUrl() { return targetUrl; }
    public String getPlatformType() { return platformType; }
    public String getDiscoveredTitle() { return discoveredTitle; }
    public int getTicketCount() { return ticketCount; }
    public String getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
