package com.platform.testops.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SubmitPracticeAnswerCommand(
        @NotNull(message = "Session ID is required")
        UUID sessionId,

        @NotNull(message = "Question ID is required")
        UUID questionId,

        @NotBlank(message = "Selected option label or answer is required")
        String selectedOption
) {}
