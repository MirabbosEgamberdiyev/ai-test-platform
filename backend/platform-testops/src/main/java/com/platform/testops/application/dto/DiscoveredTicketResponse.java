package com.platform.testops.application.dto;

import com.platform.testops.domain.model.DiscoveredTicket;

import java.util.UUID;

public record DiscoveredTicketResponse(
        UUID id,
        int ticketNumber,
        String title,
        String ticketUrl,
        int questionCount,
        String category,
        boolean isAvailable
) {
    public static DiscoveredTicketResponse fromDomain(DiscoveredTicket domain) {
        return new DiscoveredTicketResponse(
                domain.getId(),
                domain.getTicketNumber(),
                domain.getTitle(),
                domain.getTicketUrl(),
                domain.getQuestionCount(),
                domain.getCategory(),
                domain.isAvailable()
        );
    }
}
