package com.platform.testops.infrastructure.persistence;

import com.platform.testops.domain.model.TestQuestion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "testops_questions")
public class TestQuestionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "sequence_number", nullable = false)
    private int sequenceNumber;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "question_type", nullable = false, length = 50)
    private String questionType;

    @Column(name = "options_json", columnDefinition = "TEXT")
    private String optionsJson;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "user_answer", length = 500)
    private String userAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public TestQuestionJpaEntity() {}

    public static TestQuestionJpaEntity fromDomain(TestQuestion domain) {
        TestQuestionJpaEntity entity = new TestQuestionJpaEntity();
        entity.id = domain.getId();
        entity.sessionId = domain.getSessionId();
        entity.sequenceNumber = domain.getSequenceNumber();
        entity.questionText = domain.getQuestionText();
        entity.questionType = domain.getQuestionType();
        entity.optionsJson = domain.getOptionsJson();
        entity.imageUrl = domain.getImageUrl();
        entity.userAnswer = domain.getUserAnswer();
        entity.isCorrect = domain.getIsCorrect();
        entity.createdAt = domain.getCreatedAt();
        return entity;
    }

    public TestQuestion toDomain() {
        return new TestQuestion(
                this.id,
                this.sessionId,
                this.sequenceNumber,
                this.questionText,
                this.questionType,
                this.optionsJson,
                this.imageUrl,
                this.userAnswer,
                this.isCorrect,
                this.createdAt
        );
    }

    public UUID getId() { return id; }
    public UUID getSessionId() { return sessionId; }
    public int getSequenceNumber() { return sequenceNumber; }
    public String getQuestionText() { return questionText; }
    public String getQuestionType() { return questionType; }
    public String getOptionsJson() { return optionsJson; }
    public String getImageUrl() { return imageUrl; }
    public String getUserAnswer() { return userAnswer; }
    public Boolean getIsCorrect() { return isCorrect; }
    public Instant getCreatedAt() { return createdAt; }
}
