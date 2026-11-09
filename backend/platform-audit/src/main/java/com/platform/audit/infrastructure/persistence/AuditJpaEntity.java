package com.platform.audit.infrastructure.persistence;

import com.platform.audit.domain.model.AuditRecord;
import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(name = "action_type", nullable = false, length = 100)
    private String actionType;

    @Column(name = "resource_type", nullable = false, length = 100)
    private String resourceType;

    @Column(name = "resource_id", length = 100)
    private String resourceId;

    @Column(name = "client_ip", length = 50)
    private String clientIp;

    @Column(name = "details_json", columnDefinition = "TEXT")
    private String detailsJson;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;

    public AuditJpaEntity() {}

    public static AuditJpaEntity fromDomain(AuditRecord domain) {
        AuditJpaEntity entity = new AuditJpaEntity();
        entity.id = domain.getId();
        entity.tenantId = domain.getTenantId().value();
        entity.actorId = domain.getActorId() != null ? domain.getActorId().value() : null;
        entity.actionType = domain.getActionType();
        entity.resourceType = domain.getResourceType();
        entity.resourceId = domain.getResourceId();
        entity.clientIp = domain.getClientIp();
        entity.detailsJson = domain.getDetailsJson();
        entity.timestamp = domain.getTimestamp();
        return entity;
    }

    public AuditRecord toDomain() {
        return new AuditRecord(
                this.id,
                TenantId.of(this.tenantId),
                this.actorId != null ? UserId.of(this.actorId) : null,
                this.actionType,
                this.resourceType,
                this.resourceId,
                this.clientIp,
                this.detailsJson,
                this.timestamp
        );
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public UUID getActorId() { return actorId; }
    public String getActionType() { return actionType; }
    public String getResourceType() { return resourceType; }
    public String getResourceId() { return resourceId; }
    public String getClientIp() { return clientIp; }
    public String getDetailsJson() { return detailsJson; }
    public Instant getTimestamp() { return timestamp; }
}
