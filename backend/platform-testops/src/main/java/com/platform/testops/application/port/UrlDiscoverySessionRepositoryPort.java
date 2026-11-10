package com.platform.testops.application.port;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.testops.domain.model.DiscoveredTicket;
import com.platform.testops.domain.model.UrlDiscoverySession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UrlDiscoverySessionRepositoryPort {
    UrlDiscoverySession save(UrlDiscoverySession session);
    Optional<UrlDiscoverySession> findById(UUID id);
    List<UrlDiscoverySession> findByTenantId(TenantId tenantId);
    List<UrlDiscoverySession> findByUserId(UserId userId);
    void saveTickets(List<DiscoveredTicket> tickets);
    List<DiscoveredTicket> findTicketsBySessionId(UUID sessionId);
}
