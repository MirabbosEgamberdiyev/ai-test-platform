package com.platform.testops.infrastructure.persistence;

import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import com.platform.testops.application.port.UrlSolvingSessionRepositoryPort;
import com.platform.testops.domain.model.SolvedQuestion;
import com.platform.testops.domain.model.UrlSolvingSession;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UrlSolvingSessionRepositoryAdapter implements UrlSolvingSessionRepositoryPort {

    private final SpringDataUrlSolvingSessionRepository sessionRepository;
    private final SpringDataSolvedQuestionRepository questionRepository;

    public UrlSolvingSessionRepositoryAdapter(SpringDataUrlSolvingSessionRepository sessionRepository,
                                              SpringDataSolvedQuestionRepository questionRepository) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public UrlSolvingSession save(UrlSolvingSession session) {
        UrlSolvingSessionJpaEntity entity = sessionRepository.save(UrlSolvingSessionJpaEntity.fromDomain(session));
        return entity.toDomain();
    }

    @Override
    public Optional<UrlSolvingSession> findById(UUID id) {
        return sessionRepository.findById(id).map(entity -> {
            UrlSolvingSession session = entity.toDomain();
            List<SolvedQuestion> questions = questionRepository.findBySolvingSessionIdOrderByTicketNumberAscQuestionIndexAsc(id)
                    .stream()
                    .map(SolvedQuestionJpaEntity::toDomain)
                    .toList();
            session.setSolvedQuestions(questions);
            return session;
        });
    }

    @Override
    public List<UrlSolvingSession> findByTenantId(TenantId tenantId) {
        return sessionRepository.findByTenantIdOrderByCreatedAtDesc(tenantId.value())
                .stream()
                .map(UrlSolvingSessionJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<UrlSolvingSession> findByUserId(UserId userId) {
        return sessionRepository.findByUserIdOrderByCreatedAtDesc(userId.value())
                .stream()
                .map(UrlSolvingSessionJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void saveQuestions(List<SolvedQuestion> questions) {
        List<SolvedQuestionJpaEntity> entities = questions.stream()
                .map(SolvedQuestionJpaEntity::fromDomain)
                .toList();
        questionRepository.saveAll(entities);
    }

    @Override
    public List<SolvedQuestion> findQuestionsBySessionId(UUID sessionId) {
        return questionRepository.findBySolvingSessionIdOrderByTicketNumberAscQuestionIndexAsc(sessionId)
                .stream()
                .map(SolvedQuestionJpaEntity::toDomain)
                .toList();
    }
}
