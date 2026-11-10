package com.platform.testops.infrastructure.persistence;

import com.platform.testops.domain.model.SolvedQuestion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "url_solved_questions")
public class SolvedQuestionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "solving_session_id", nullable = false)
    private UUID solvingSessionId;

    @Column(name = "ticket_number", nullable = false)
    private int ticketNumber;

    @Column(name = "ticket_title", nullable = false, length = 255)
    private String ticketTitle;

    @Column(name = "question_index", nullable = false)
    private int questionIndex;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "options_json", nullable = false, columnDefinition = "TEXT")
    private String optionsJson;

    @Column(name = "selected_answer_index", nullable = false)
    private int selectedAnswerIndex;

    @Column(name = "selected_answer_text", nullable = false, columnDefinition = "TEXT")
    private String selectedAnswerText;

    @Column(name = "ai_confidence_score", nullable = false)
    private double aiConfidenceScore;

    @Column(name = "verification_status", nullable = false, length = 50)
    private String verificationStatus;

    @Column(name = "reasoning_explanation", nullable = false, columnDefinition = "TEXT")
    private String reasoningExplanation;

    @Column(name = "rule_reference", length = 500)
    private String ruleReference;

    @Column(name = "ambiguity_note", columnDefinition = "TEXT")
    private String ambiguityNote;

    @Column(name = "arithmetic_verified", nullable = false)
    private boolean arithmeticVerified;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public SolvedQuestionJpaEntity() {}

    public static SolvedQuestionJpaEntity fromDomain(SolvedQuestion domain) {
        SolvedQuestionJpaEntity entity = new SolvedQuestionJpaEntity();
        entity.id = domain.getId();
        entity.solvingSessionId = domain.getSolvingSessionId();
        entity.ticketNumber = domain.getTicketNumber();
        entity.ticketTitle = domain.getTicketTitle();
        entity.questionIndex = domain.getQuestionIndex();
        entity.questionText = domain.getQuestionText();
        entity.imageUrl = domain.getImageUrl();
        entity.optionsJson = domain.getOptionsJson();
        entity.selectedAnswerIndex = domain.getSelectedAnswerIndex();
        entity.selectedAnswerText = domain.getSelectedAnswerText();
        entity.aiConfidenceScore = domain.getAiConfidenceScore();
        entity.verificationStatus = domain.getVerificationStatus();
        entity.reasoningExplanation = domain.getReasoningExplanation();
        entity.ruleReference = domain.getRuleReference();
        entity.ambiguityNote = domain.getAmbiguityNote();
        entity.arithmeticVerified = domain.isArithmeticVerified();
        entity.createdAt = domain.getCreatedAt();
        return entity;
    }

    public SolvedQuestion toDomain() {
        return new SolvedQuestion(
                id,
                solvingSessionId,
                ticketNumber,
                ticketTitle,
                questionIndex,
                questionText,
                imageUrl,
                optionsJson,
                selectedAnswerIndex,
                selectedAnswerText,
                aiConfidenceScore,
                verificationStatus,
                reasoningExplanation,
                ruleReference,
                ambiguityNote,
                arithmeticVerified,
                createdAt
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
