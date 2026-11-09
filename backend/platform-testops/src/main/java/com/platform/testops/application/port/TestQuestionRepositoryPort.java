package com.platform.testops.application.port;

import com.platform.testops.domain.model.TestQuestion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestQuestionRepositoryPort {
    TestQuestion save(TestQuestion question);
    Optional<TestQuestion> findById(UUID id);
    List<TestQuestion> findBySessionId(UUID sessionId);
}
