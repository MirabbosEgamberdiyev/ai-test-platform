package com.platform.notification.application.port;

import com.platform.notification.domain.model.TelegramSendMessageRequest;

public interface TelegramBotClientPort {
    void sendMessage(TelegramSendMessageRequest request);
}
