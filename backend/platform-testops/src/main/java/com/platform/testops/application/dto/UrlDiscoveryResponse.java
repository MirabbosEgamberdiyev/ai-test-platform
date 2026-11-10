package com.platform.testops.application.dto;

import com.platform.testops.domain.model.UrlDiscoverySession;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UrlDiscoveryResponse(
        UUID id,
        String targetUrl,
        String platformType,
        String discoveredTitle,
        int ticketCount,
        String status,
        String errorMessage,
        List<DiscoveredTicketResponse> tickets,
        String confirmationMessage,
        Instant createdAt
) {
    public static UrlDiscoveryResponse fromDomain(UrlDiscoverySession domain, String language) {
        List<DiscoveredTicketResponse> ticketResponses = domain.getTickets() != null
                ? domain.getTickets().stream().map(DiscoveredTicketResponse::fromDomain).toList()
                : List.of();

        String confirmationMsg;
        int count = domain.getTicketCount();
        String lang = language != null ? language : "uz-Latn";

        if (count == 1) {
            confirmationMsg = switch (lang) {
                case "uz-Cyrl" -> "Сайтда битта тест топилди. Шу тестни ечишни бошлаймизми?";
                case "ru" -> "На сайте найден 1 тест. Начать решение этого теста?";
                case "en" -> "1 test was found on this website. Do you want to start solving it?";
                default -> "Saytda bitta test topildi. Shu testni yechishni boshlaymizmi?";
            };
        } else if (count > 1) {
            confirmationMsg = switch (lang) {
                case "uz-Cyrl" -> String.format("Сайтда %d та билет топилди. Қайси билетни ечмоқчисиз?", count);
                case "ru" -> String.format("На сайте обнаружено %d билетов. Какой билет вы хотите решить?", count);
                case "en" -> String.format("%d tickets found on the website. Which ticket do you want to solve?", count);
                default -> String.format("Saytda %d ta bilet topildi. Qaysi biletni yechmoqchisiz?", count);
            };
        } else {
            confirmationMsg = switch (lang) {
                case "uz-Cyrl" -> "Сайтда тестлар ёки билетлар топилмади.";
                case "ru" -> "На странице не обнаружено доступных тестов или билетов.";
                case "en" -> "No tests or examination tickets were discovered on this page.";
                default -> "Saytda testlar yoki biletlar topilmadi.";
            };
        }

        return new UrlDiscoveryResponse(
                domain.getId(),
                domain.getTargetUrl(),
                domain.getPlatformType(),
                domain.getDiscoveredTitle(),
                domain.getTicketCount(),
                domain.getStatus(),
                domain.getErrorMessage(),
                ticketResponses,
                confirmationMsg,
                domain.getCreatedAt()
        );
    }
}
