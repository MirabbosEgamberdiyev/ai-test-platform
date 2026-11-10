package com.platform.testops.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class SolvedQuestion {
    private final UUID id;
    private final UUID solvingSessionId;
    private final int ticketNumber;
    private final String ticketTitle;
    private final int questionIndex;
    private final String questionText;
    private final String imageUrl;
    private final String optionsJson;
    private final int selectedAnswerIndex;
    private final String selectedAnswerText;
    private final double aiConfidenceScore;
    private final String verificationStatus; // SOLVED, VERIFIED, NEEDS_REVIEW, AMBIGUOUS
    private final String reasoningExplanation;
    private final String ruleReference;
    private final String ambiguityNote;
    private final boolean arithmeticVerified;
    private final Instant createdAt;

    public SolvedQuestion(UUID id, UUID solvingSessionId, int ticketNumber, String ticketTitle,
                          int questionIndex, String questionText, String imageUrl, String optionsJson,
                          int selectedAnswerIndex, String selectedAnswerText, double aiConfidenceScore,
                          String verificationStatus, String reasoningExplanation, String ruleReference,
                          String ambiguityNote, boolean arithmeticVerified, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.solvingSessionId = Objects.requireNonNull(solvingSessionId, "solvingSessionId must not be null");
        this.ticketNumber = ticketNumber;
        this.ticketTitle = Objects.requireNonNull(ticketTitle, "ticketTitle must not be null");
        this.questionIndex = questionIndex;
        this.questionText = Objects.requireNonNull(questionText, "questionText must not be null");
        this.imageUrl = imageUrl;
        this.optionsJson = Objects.requireNonNull(optionsJson, "optionsJson must not be null");
        this.selectedAnswerIndex = selectedAnswerIndex;
        this.selectedAnswerText = Objects.requireNonNull(selectedAnswerText, "selectedAnswerText must not be null");
        this.aiConfidenceScore = aiConfidenceScore;
        this.verificationStatus = verificationStatus != null ? verificationStatus : "SOLVED";
        this.reasoningExplanation = Objects.requireNonNull(reasoningExplanation, "reasoningExplanation must not be null");
        this.ruleReference = ruleReference;
        this.ambiguityNote = ambiguityNote;
        this.arithmeticVerified = arithmeticVerified;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static SolvedQuestion create(UUID solvingSessionId, int ticketNumber, String ticketTitle,
                                        int questionIndex, String questionText, String imageUrl,
                                        String optionsJson, int selectedAnswerIndex, String selectedAnswerText,
                                        double aiConfidenceScore, String verificationStatus,
                                        String reasoningExplanation, String ruleReference,
                                        String ambiguityNote, boolean arithmeticVerified) {
        return new SolvedQuestion(
                UUID.randomUUID(), solvingSessionId, ticketNumber, ticketTitle,
                questionIndex, questionText, imageUrl, optionsJson,
                selectedAnswerIndex, selectedAnswerText, aiConfidenceScore,
                verificationStatus, reasoningExplanation, ruleReference,
                ambiguityNote, arithmeticVerified, Instant.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getSolvingSessionId() { return solvingSessionId; }
    public int getTicketNumber() { return ticketNumber; }
    public String getTicketTitle() { return ticketTitle; }
    public int getQuestionIndex() { return questionIndex; }
    public String getQuestionText() { return questionText; }
    public String getImageUrl() { return imageUrl; }
    public String getOptionsJson() { return optionsJson; }
    public int getSelectedAnswerIndex() { return selectedAnswerIndex; }
    public String getSelectedAnswerText() { return selectedAnswerText; }
    public double getAiConfidenceScore() { return aiConfidenceScore; }
    public String getVerificationStatus() { return verificationStatus; }
    public String getReasoningExplanation() { return reasoningExplanation; }
    public String getRuleReference() { return ruleReference; }
    public String getAmbiguityNote() { return ambiguityNote; }
    public boolean isArithmeticVerified() { return arithmeticVerified; }
    public Instant getCreatedAt() { return createdAt; }
}
