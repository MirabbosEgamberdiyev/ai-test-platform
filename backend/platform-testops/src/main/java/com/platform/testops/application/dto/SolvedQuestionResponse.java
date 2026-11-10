package com.platform.testops.application.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.testops.domain.model.SolvedQuestion;

import java.util.List;
import java.util.UUID;

public record SolvedQuestionResponse(
        UUID id,
        int ticketNumber,
        String ticketTitle,
        int questionIndex,
        String questionText,
        String imageUrl,
        List<String> options,
        int selectedAnswerIndex,
        String selectedAnswerText,
        double aiConfidenceScore,
        String verificationStatus,
        String reasoningExplanation,
        String ruleReference,
        String ambiguityNote,
        boolean arithmeticVerified
) {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static SolvedQuestionResponse fromDomain(SolvedQuestion domain) {
        List<String> optionsList;
        try {
            optionsList = MAPPER.readValue(domain.getOptionsJson(), new TypeReference<List<String>>() {});
        } catch (Exception e) {
            optionsList = List.of(domain.getSelectedAnswerText());
        }

        return new SolvedQuestionResponse(
                domain.getId(),
                domain.getTicketNumber(),
                domain.getTicketTitle(),
                domain.getQuestionIndex(),
                domain.getQuestionText(),
                domain.getImageUrl(),
                optionsList,
                domain.getSelectedAnswerIndex(),
                domain.getSelectedAnswerText(),
                domain.getAiConfidenceScore(),
                domain.getVerificationStatus(),
                domain.getReasoningExplanation(),
                domain.getRuleReference(),
                domain.getAmbiguityNote(),
                domain.isArithmeticVerified()
        );
    }
}
