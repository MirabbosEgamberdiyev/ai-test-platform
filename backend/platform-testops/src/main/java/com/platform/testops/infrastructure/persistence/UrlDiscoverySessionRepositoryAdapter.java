package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.testops.application.port.UrlDiscoverySessionRepositoryPort;
import com.platform.testops.domain.model.DiscoveredTicket;
import com.platform.testops.domain.model.UrlDiscoverySession;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UrlDiscoverySessionRepositoryAdapter implements UrlDiscoverySessionRepositoryPort {

    private final SpringDataUrlDiscoverySessionRepository sessionRepository;
    private final SpringDataDiscoveredTicketRepository ticketRepository;

    public UrlDiscoverySessionRepositoryAdapter(SpringDataUrlDiscoverySessionRepository sessionRepository,
                                                SpringDataDiscoveredTicketRepository ticketRepository) {
        this.sessionRepository = sessionRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public UrlDiscoverySession save(UrlDiscoverySession session) {
        UrlDiscoverySessionJpaEntity entity = sessionRepository.save(UrlDiscoverySessionJpaEntity.fromDomain(session));
        return entity.toDomain();
    }

    @Override
    public Optional<UrlDiscoverySession> findById(UUID id) {
        return sessionRepository.findById(id).map(entity -> {
            UrlDiscoverySession session = entity.toDomain();
            List<DiscoveredTicket> tickets = ticketRepository.findByDiscoverySessionIdOrderByTicketNumberAsc(id)
                    .stream()
                    .map(DiscoveredTicketJpaEntity::toDomain)
                    .toList();
            session.setTickets(tickets);
            return session;
        });
    }

    @Override
    public List<UrlDiscoverySession> findByTenantId(TenantId tenantId) {
        return sessionRepository.findByTenantIdOrderByCreatedAtDesc(tenantId.value())
                .stream()
                .map(UrlDiscoverySessionJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<UrlDiscoverySession> findByUserId(UserId userId) {
        return sessionRepository.findByUserIdOrderByCreatedAtDesc(userId.value())
                .stream()
                .map(UrlDiscoverySessionJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void saveTickets(List<DiscoveredTicket> tickets) {
        List<DiscoveredTicketJpaEntity> entities = tickets.stream()
                .map(DiscoveredTicketJpaEntity::fromDomain)
                .toList();
        ticketRepository.saveAll(entities);
    }

    @Override
    public List<DiscoveredTicket> findTicketsBySessionId(UUID sessionId) {
        return ticketRepository.findByDiscoverySessionIdOrderByTicketNumberAsc(sessionId)
                .stream()
                .map(DiscoveredTicketJpaEntity::toDomain)
                .toList();
    }
}
