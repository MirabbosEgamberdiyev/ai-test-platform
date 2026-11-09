package com.platform.testops.application.dto;

import com.platform.testops.domain.model.PracticeMode;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record StartPracticeCommand(
        UUID integrationId,

        @NotBlank(message = "Target URL is required")
        String targetUrl,

        PracticeMode practiceMode
) {}
