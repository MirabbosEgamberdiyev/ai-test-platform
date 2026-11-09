package com.platform.iam.application.dto;

import jakarta.validation.constraints.NotBlank;

public record LinkTelegramCommand(
        @NotBlank(message = "Telegram chat ID is required")
        String telegramChatId
) {}
