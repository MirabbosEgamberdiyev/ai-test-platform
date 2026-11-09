package com.platform.automation.domain.model;

import java.io.Serializable;
import java.time.Instant;

public record SubmissionFeedback(
        boolean isSubmitted,
        String detectedResultText,
        Instant submittedAt
) implements Serializable {
    public static SubmissionFeedback success(String resultText) {
        return new SubmissionFeedback(true, resultText, Instant.now());
    }

    public static SubmissionFeedback failure(String reason) {
        return new SubmissionFeedback(false, reason, Instant.now());
    }
}
