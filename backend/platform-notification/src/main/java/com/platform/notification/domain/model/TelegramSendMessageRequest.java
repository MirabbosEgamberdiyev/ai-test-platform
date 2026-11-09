package com.platform.notification.domain.model;

import java.io.Serializable;

public record TelegramSendMessageRequest(
        String chatId,
        String text,
        String parseMode
) implements Serializable {
    public static TelegramSendMessageRequest markdown(String chatId, String text) {
        return new TelegramSendMessageRequest(chatId, text, "Markdown");
    }

    public static TelegramSendMessageRequest html(String chatId, String text) {
        return new TelegramSendMessageRequest(chatId, text, "HTML");
    }
}
