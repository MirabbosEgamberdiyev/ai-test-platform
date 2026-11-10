package com.platform.testops.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataDiscoveredTicketRepository extends JpaRepository<DiscoveredTicketJpaEntity, UUID> {
    List<DiscoveredTicketJpaEntity> findByDiscoverySessionIdOrderByTicketNumberAsc(UUID discoverySessionId);
}
