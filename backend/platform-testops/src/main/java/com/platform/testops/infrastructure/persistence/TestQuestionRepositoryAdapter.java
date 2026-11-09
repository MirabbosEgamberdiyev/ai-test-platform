package com.platform.testops.infrastructure.persistence;

import com.platform.testops.application.port.TestQuestionRepositoryPort;
import com.platform.testops.domain.model.TestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface SpringDataTestQuestionRepository extends JpaRepository<TestQuestionJpaEntity, UUID> {
    List<TestQuestionJpaEntity> findBySessionIdOrderBySequenceNumberAsc(UUID sessionId);
}

@Component
public class TestQuestionRepositoryAdapter implements TestQuestionRepositoryPort {

    private final SpringDataTestQuestionRepository repository;

    public TestQuestionRepositoryAdapter(SpringDataTestQuestionRepository repository) {
        this.repository = repository;
    }

    @Override
    public TestQuestion save(TestQuestion question) {
        TestQuestionJpaEntity entity = TestQuestionJpaEntity.fromDomain(question);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<TestQuestion> findById(UUID id) {
        return repository.findById(id).map(TestQuestionJpaEntity::toDomain);
    }

    @Override
    public List<TestQuestion> findBySessionId(UUID sessionId) {
        return repository.findBySessionIdOrderBySequenceNumberAsc(sessionId).stream()
                .map(TestQuestionJpaEntity::toDomain)
                .toList();
    }
}
