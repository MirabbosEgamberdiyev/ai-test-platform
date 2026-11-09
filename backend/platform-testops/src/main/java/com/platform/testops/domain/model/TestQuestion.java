package com.platform.testops.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class TestQuestion {
    private final UUID id;
    private final UUID sessionId;
    private final int sequenceNumber;
    private final String questionText;
    private final String questionType;
    private final String optionsJson;
    private final String imageUrl;
    private String userAnswer;
    private Boolean isCorrect;
    private final Instant createdAt;

    public TestQuestion(UUID id, UUID sessionId, int sequenceNumber, String questionText,
                        String questionType, String optionsJson, String imageUrl,
                        String userAnswer, Boolean isCorrect, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.sessionId = Objects.requireNonNull(sessionId);
        this.sequenceNumber = sequenceNumber;
        this.questionText = Objects.requireNonNull(questionText);
        this.questionType = Objects.requireNonNull(questionType);
        this.optionsJson = optionsJson;
        this.imageUrl = imageUrl;
        this.userAnswer = userAnswer;
        this.isCorrect = isCorrect;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static TestQuestion create(UUID sessionId, int sequenceNumber, String questionText,
                                      String questionType, String optionsJson, String imageUrl) {
        return new TestQuestion(
                UUID.randomUUID(),
                sessionId,
                sequenceNumber,
                questionText,
                questionType,
                optionsJson,
                imageUrl,
                null,
                null,
                Instant.now()
        );
    }

    public void recordUserAnswer(String answer, boolean correct) {
        this.userAnswer = answer;
        this.isCorrect = correct;
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
