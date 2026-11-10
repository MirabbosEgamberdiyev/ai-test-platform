package com.platform.iam.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataUserSecurityTokenRepository extends JpaRepository<UserSecurityTokenJpaEntity, UUID> {
    Optional<UserSecurityTokenJpaEntity> findByTokenHashAndTokenType(String tokenHash, String tokenType);
    void deleteByUserId(UUID userId);
}
