package com.platform.testops.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class DiscoveredTicket {
    private final UUID id;
    private final UUID discoverySessionId;
    private final int ticketNumber;
    private final String title;
    private final String ticketUrl;
    private final int questionCount;
    private final String category;
    private final boolean isAvailable;
    private final String metadataJson;
    private final Instant createdAt;

    public DiscoveredTicket(UUID id, UUID discoverySessionId, int ticketNumber, String title,
                            String ticketUrl, int questionCount, String category,
                            boolean isAvailable, String metadataJson, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.discoverySessionId = Objects.requireNonNull(discoverySessionId, "discoverySessionId must not be null");
        this.ticketNumber = ticketNumber;
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.ticketUrl = Objects.requireNonNull(ticketUrl, "ticketUrl must not be null");
        this.questionCount = questionCount;
        this.category = category;
        this.isAvailable = isAvailable;
        this.metadataJson = metadataJson;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static DiscoveredTicket create(UUID discoverySessionId, int ticketNumber, String title,
                                          String ticketUrl, int questionCount, String category) {
        return new DiscoveredTicket(
                UUID.randomUUID(), discoverySessionId, ticketNumber, title,
                ticketUrl, questionCount, category, true, "{}", Instant.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getDiscoverySessionId() { return discoverySessionId; }
    public int getTicketNumber() { return ticketNumber; }
    public String getTitle() { return title; }
    public String getTicketUrl() { return ticketUrl; }
    public int getQuestionCount() { return questionCount; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return isAvailable; }
    public String getMetadataJson() { return metadataJson; }
    public Instant getCreatedAt() { return createdAt; }
}
