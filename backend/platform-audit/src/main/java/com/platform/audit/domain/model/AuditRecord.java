package com.platform.audit.domain.model;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class AuditRecord implements Serializable {
    private final UUID id;
    private final TenantId tenantId;
    private final UserId actorId;
    private final String actionType;
    private final String resourceType;
    private final String resourceId;
    private final String clientIp;
    private final String detailsJson;
    private final Instant timestamp;

    public AuditRecord(UUID id, TenantId tenantId, UserId actorId, String actionType,
                       String resourceType, String resourceId, String clientIp,
                       String detailsJson, Instant timestamp) {
        this.id = Objects.requireNonNull(id);
        this.tenantId = Objects.requireNonNull(tenantId);
        this.actorId = actorId;
        this.actionType = Objects.requireNonNull(actionType);
        this.resourceType = Objects.requireNonNull(resourceType);
        this.resourceId = resourceId;
        this.clientIp = clientIp;
        this.detailsJson = detailsJson;
        this.timestamp = Objects.requireNonNull(timestamp);
    }

    public static AuditRecord create(TenantId tenantId, UserId actorId, String actionType,
                                     String resourceType, String resourceId, String clientIp, String detailsJson) {
        return new AuditRecord(
                UUID.randomUUID(),
                tenantId,
                actorId,
                actionType,
                resourceType,
                resourceId,
                clientIp,
                detailsJson,
                Instant.now()
        );
    }

    public UUID getId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public UserId getActorId() { return actorId; }
    public String getActionType() { return actionType; }
    public String getResourceType() { return resourceType; }
    public String getResourceId() { return resourceId; }
    public String getClientIp() { return clientIp; }
    public String getDetailsJson() { return detailsJson; }
    public Instant getTimestamp() { return timestamp; }
}
