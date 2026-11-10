package com.platform.testops.application.dto;

import com.platform.testops.domain.model.UrlSolvingSession;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UrlSolvingSessionResponse(
        UUID id,
        String targetUrl,
        String selectedTicketNumbers,
        String language,
        String status,
        String executionMode,
        String submissionStatus,
        String screenshotPath,
        String executionEvidenceJson,
        String browserLogs,
        int totalQuestions,
        int solvedQuestionsCount,
        int verifiedQuestionsCount,
        int reviewNeededCount,
        long executionTimeMs,
        Instant createdAt,
        Instant completedAt,
        List<SolvedQuestionResponse> questions
) {
    public static UrlSolvingSessionResponse fromDomain(UrlSolvingSession domain) {
        List<SolvedQuestionResponse> questionResponses = domain.getSolvedQuestions() != null
                ? domain.getSolvedQuestions().stream().map(SolvedQuestionResponse::fromDomain).toList()
                : List.of();

        return new UrlSolvingSessionResponse(
                domain.getId(),
                domain.getTargetUrl(),
                domain.getSelectedTicketNumbers(),
                domain.getLanguage(),
                domain.getStatus(),
                domain.getExecutionMode() != null ? domain.getExecutionMode().name() : "ANSWER_MODE",
                domain.getSubmissionStatus() != null ? domain.getSubmissionStatus().name() : "NOT_STARTED",
                domain.getScreenshotPath(),
                domain.getExecutionEvidenceJson(),
                domain.getBrowserLogs(),
                domain.getTotalQuestions(),
                domain.getSolvedQuestionsCount(),
                domain.getVerifiedQuestionsCount(),
                domain.getReviewNeededCount(),
                domain.getExecutionTimeMs(),
                domain.getCreatedAt(),
                domain.getCompletedAt(),
                questionResponses
        );
    }
}
