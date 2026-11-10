package com.platform.testops.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record SolveTicketsCommand(
        UUID discoverySessionId,

        @NotBlank(message = "Target URL is required")
        String targetUrl,

        @NotBlank(message = "Selected ticket numbers are required (e.g. '1', '1,2', '1-10', 'ALL')")
        String selectedTicketNumbers,

        String language, // uz-Latn, uz-Cyrl, ru, en
        String executionMode, // ANSWER_MODE, AUTHORIZED_AUTOMATION_MODE, GUIDED_MODE
        Boolean userConfirmedSubmission
) {}
