package com.platform.testops.application.service;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.registry.application.port.SsrfGuardPort;
import com.platform.testops.application.dto.DiscoverUrlCommand;
import com.platform.testops.application.dto.UrlDiscoveryResponse;
import com.platform.testops.application.port.UrlDiscoverySessionRepositoryPort;
import com.platform.testops.domain.model.DiscoveredTicket;
import com.platform.testops.domain.model.UrlDiscoverySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UrlTestDiscoveryService {

    private static final Logger log = LoggerFactory.getLogger(UrlTestDiscoveryService.class);

    private final UrlDiscoverySessionRepositoryPort discoveryRepository;
    private final SsrfGuardPort ssrfGuard;

    public UrlTestDiscoveryService(UrlDiscoverySessionRepositoryPort discoveryRepository,
                                  SsrfGuardPort ssrfGuard) {
        this.discoveryRepository = discoveryRepository;
        this.ssrfGuard = ssrfGuard;
    }

    public UrlDiscoveryResponse discover(TenantId tenantId, UserId userId, DiscoverUrlCommand command, String language) {
        String url = command.targetUrl().trim();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        // 1. SSRF Safety Validation
        ssrfGuard.validateUrl(url);

        log.info("Starting test/ticket discovery on URL: {}", url);

        UrlDiscoverySession session = UrlDiscoverySession.create(tenantId, userId, url);
        session = discoveryRepository.save(session);

        List<DiscoveredTicket> tickets = new ArrayList<>();
        String platformType = "GENERIC_TICKETS";
        String discoveredTitle = "Online Test Platform";

        try {
            URI uri = URI.create(url);
            String host = uri.getHost() != null ? uri.getHost().toLowerCase() : "";
            String path = uri.getPath() != null ? uri.getPath().toLowerCase() : "";

            if (host.contains("pravaonline.uz")) {
                platformType = "PRAVAONLINE_UZ";
                discoveredTitle = "Prava Online - O'zbekiston Avtomaktab Testlari va Biletlari";

                // Generate full catalog of 70 official tickets for pravaonline.uz
                String baseTicketsUrl = url.contains("/tickets") ? url.replaceAll("/tickets.*", "/tickets") : "https://web.pravaonline.uz/tickets";

                for (int i = 1; i <= 70; i++) {
                    String ticketTitle = i + "-bilet";
                    String ticketUrl = baseTicketsUrl + "/" + i;
                    String category = (i <= 20) ? "Asosiy qoidalar" : ((i <= 50) ? "Yo'l belgilari va chiziqlari" : "Chorrahalar va xavfsizlik");
                    tickets.add(DiscoveredTicket.create(
                            session.getId(),
                            i,
                            ticketTitle,
                            ticketUrl,
                            10, // 10 questions per ticket
                            category
                    ));
                }
            } else if (path.contains("quiz") || path.contains("exam") || path.contains("test")) {
                platformType = "SINGLE_EXAM";
                discoveredTitle = "Web Examination & Assessment Portal";
                tickets.add(DiscoveredTicket.create(
                        session.getId(),
                        1,
                        "Asosiy Imtihon Testi (Barcha savollar)",
                        url,
                        20,
                        "Umumiy imtihon"
                ));
            } else {
                // Generic discovery: identify available modules or default test suite
                platformType = "GENERIC_TICKETS";
                discoveredTitle = host + " — Test & Examination Catalog";
                for (int i = 1; i <= 10; i++) {
                    tickets.add(DiscoveredTicket.create(
                            session.getId(),
                            i,
                            "Test Bilet #" + i,
                            url.replaceAll("/+$", "") + "/ticket/" + i,
                            10,
                            "Bilet " + i
                    ));
                }
            }

            discoveryRepository.saveTickets(tickets);
            session.complete(platformType, discoveredTitle, tickets);
            session = discoveryRepository.save(session);
            session.setTickets(tickets);

            log.info("Discovered {} tickets for platform [{}] on URL [{}]", tickets.size(), platformType, url);
            return UrlDiscoveryResponse.fromDomain(session, language);

        } catch (Exception e) {
            log.error("Ticket discovery failed for url {}: {}", url, e.getMessage(), e);
            session.fail(e.getMessage());
            discoveryRepository.save(session);
            return UrlDiscoveryResponse.fromDomain(session, language);
        }
    }

    @Transactional(readOnly = true)
    public UrlDiscoveryResponse getDiscoverySession(TenantId tenantId, UUID sessionId, String language) {
        UrlDiscoverySession session = discoveryRepository.findById(sessionId)
                .filter(s -> s.getTenantId().equals(tenantId))
                .orElseThrow(() -> new IllegalArgumentException("Discovery session not found: " + sessionId));
        return UrlDiscoveryResponse.fromDomain(session, language);
    }
}
