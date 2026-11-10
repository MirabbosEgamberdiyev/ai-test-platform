package com.platform.testops.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataSolvedQuestionRepository extends JpaRepository<SolvedQuestionJpaEntity, UUID> {
    List<SolvedQuestionJpaEntity> findBySolvingSessionIdOrderByTicketNumberAscQuestionIndexAsc(UUID solvingSessionId);
}
