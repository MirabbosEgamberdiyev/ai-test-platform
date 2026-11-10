package com.platform.testops.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataUrlSolvingSessionRepository extends JpaRepository<UrlSolvingSessionJpaEntity, UUID> {
    List<UrlSolvingSessionJpaEntity> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
    List<UrlSolvingSessionJpaEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
