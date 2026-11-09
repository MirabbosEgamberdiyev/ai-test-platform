package com.platform.testops.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AnalyzeCurrentQuestionCommand(
        @NotNull(message = "Session ID is required")
        UUID sessionId
) {}
