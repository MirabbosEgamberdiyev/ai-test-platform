package com.platform.testops.infrastructure.persistence;

import com.platform.testops.domain.model.DiscoveredTicket;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "url_discovered_tickets")
public class DiscoveredTicketJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "discovery_session_id", nullable = false)
    private UUID discoverySessionId;

    @Column(name = "ticket_number", nullable = false)
    private int ticketNumber;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "ticket_url", nullable = false, length = 1000)
    private String ticketUrl;

    @Column(name = "question_count", nullable = false)
    private int questionCount;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public DiscoveredTicketJpaEntity() {}

    public static DiscoveredTicketJpaEntity fromDomain(DiscoveredTicket domain) {
        DiscoveredTicketJpaEntity entity = new DiscoveredTicketJpaEntity();
        entity.id = domain.getId();
        entity.discoverySessionId = domain.getDiscoverySessionId();
        entity.ticketNumber = domain.getTicketNumber();
        entity.title = domain.getTitle();
        entity.ticketUrl = domain.getTicketUrl();
        entity.questionCount = domain.getQuestionCount();
        entity.category = domain.getCategory();
        entity.isAvailable = domain.isAvailable();
        entity.metadataJson = domain.getMetadataJson();
        entity.createdAt = domain.getCreatedAt();
        return entity;
    }

    public DiscoveredTicket toDomain() {
        return new DiscoveredTicket(
                id,
                discoverySessionId,
                ticketNumber,
                title,
                ticketUrl,
                questionCount,
                category,
                isAvailable,
                metadataJson,
                createdAt
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
