package com.platform.testops.domain.model;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UrlDiscoverySession {
    private final UUID id;
    private final TenantId tenantId;
    private final UserId userId;
    private final String targetUrl;
    private String platformType;
    private String discoveredTitle;
    private int ticketCount;
    private String status; // PENDING, DISCOVERING, COMPLETED, FAILED
    private String errorMessage;
    private final Instant createdAt;
    private Instant updatedAt;
    private List<DiscoveredTicket> tickets;

    public UrlDiscoverySession(UUID id, TenantId tenantId, UserId userId, String targetUrl,
                               String platformType, String discoveredTitle, int ticketCount,
                               String status, String errorMessage, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.targetUrl = Objects.requireNonNull(targetUrl, "targetUrl must not be null");
        this.platformType = platformType != null ? platformType : "GENERIC_TICKETS";
        this.discoveredTitle = discoveredTitle;
        this.ticketCount = ticketCount;
        this.status = status != null ? status : "PENDING";
        this.errorMessage = errorMessage;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
        this.tickets = new ArrayList<>();
    }

    public static UrlDiscoverySession create(TenantId tenantId, UserId userId, String targetUrl) {
        Instant now = Instant.now();
        return new UrlDiscoverySession(
                UUID.randomUUID(), tenantId, userId, targetUrl,
                "GENERIC_TICKETS", null, 0, "PENDING", null, now, now
        );
    }

    public void complete(String platformType, String discoveredTitle, List<DiscoveredTicket> tickets) {
        this.platformType = platformType;
        this.discoveredTitle = discoveredTitle;
        this.tickets = new ArrayList<>(tickets);
        this.ticketCount = tickets.size();
        this.status = "COMPLETED";
        this.updatedAt = Instant.now();
    }

    public void fail(String errorMessage) {
        this.status = "FAILED";
        this.errorMessage = errorMessage;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public UserId getUserId() { return userId; }
    public String getTargetUrl() { return targetUrl; }
    public String getPlatformType() { return platformType; }
    public String getDiscoveredTitle() { return discoveredTitle; }
    public int getTicketCount() { return ticketCount; }
    public String getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<DiscoveredTicket> getTickets() { return tickets; }
    public void setTickets(List<DiscoveredTicket> tickets) { this.tickets = tickets; }
}
