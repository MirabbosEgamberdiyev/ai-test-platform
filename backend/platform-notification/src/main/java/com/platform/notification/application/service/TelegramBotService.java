package com.platform.notification.application.service;

import com.platform.notification.application.port.TelegramBotClientPort;
import com.platform.notification.domain.model.TelegramSendMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TelegramBotService {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotService.class);

    private final TelegramBotClientPort botClient;

    public TelegramBotService(TelegramBotClientPort botClient) {
        this.botClient = botClient;
    }

    public void processWebhookUpdate(Map<String, Object> update) {
        log.info("Received Telegram webhook update payload: {}", update);

        @SuppressWarnings("unchecked")
        Map<String, Object> message = (Map<String, Object>) update.get("message");
        if (message == null) return;

        @SuppressWarnings("unchecked")
        Map<String, Object> chat = (Map<String, Object>) message.get("chat");
        if (chat == null) return;

        String chatId = String.valueOf(chat.get("id"));
        String text = (String) message.get("text");

        if (text == null) return;

        handleCommand(chatId, text.trim());
    }

    public void sendPracticeCompletionNotification(String chatId, String targetUrl, int totalQuestions, int correctAnswers, double accuracy) {
        if (chatId == null || chatId.isBlank()) return;

        String message = String.format(
                """
                🎯 *Practice Session Completed!*
                
                🌐 *Platform:* `%s`
                📊 *Total Questions:* %d
                ✅ *Correct Answers:* %d
                📈 *Accuracy Score:* %.1f%%
                
                💡 *AI Recommendation:* Focus on reinforcing foundational patterns to increase consistency!
                """,
                targetUrl, totalQuestions, correctAnswers, accuracy
        );

        botClient.sendMessage(TelegramSendMessageRequest.markdown(chatId, message));
    }

    private void handleCommand(String chatId, String text) {
        if (text.startsWith("/start")) {
            String welcome = """
                    🤖 *Welcome to the Enterprise AI Test Automation Assistant!*
                    
                    Available commands:
                    /status - View your connected platform status
                    /help - View operational guide
                    """;
            botClient.sendMessage(TelegramSendMessageRequest.markdown(chatId, welcome));
        } else if (text.startsWith("/status")) {
            String status = "✅ *Platform Connection Active.* Ready for practice evaluation.";
            botClient.sendMessage(TelegramSendMessageRequest.markdown(chatId, status));
        } else {
            botClient.sendMessage(TelegramSendMessageRequest.markdown(chatId, "Command recognized. Use the web dashboard for interactive practice sessions."));
        }
    }
}
