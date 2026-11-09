package com.platform.notification.infrastructure.adapter;

import com.platform.notification.application.port.TelegramBotClientPort;
import com.platform.notification.domain.model.TelegramSendMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class TelegramBotClientAdapter implements TelegramBotClientPort {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotClientAdapter.class);

    private final WebClient webClient;
    private final String botToken;
    private final boolean enabled;

    public TelegramBotClientAdapter(
            @Value("${app.telegram.bot-token:123456789:MockTelegramTokenForDev}") String botToken) {
        this.botToken = botToken;
        this.enabled = !botToken.contains("Mock") && !botToken.contains("Fake");
        this.webClient = WebClient.builder()
                .baseUrl("https://api.telegram.org/bot" + botToken)
                .build();
    }

    @Override
    public void sendMessage(TelegramSendMessageRequest request) {
        log.info("Sending Telegram notification to chatId {}: {}", request.chatId(), request.text());

        if (!enabled) {
            log.info("[MOCK TELEGRAM DISPATCH] Message simulated for chat: {}", request.chatId());
            return;
        }

        try {
            Map<String, Object> payload = Map.of(
                    "chat_id", request.chatId(),
                    "text", request.text(),
                    "parse_mode", request.parseMode()
            );

            webClient.post()
                    .uri("/sendMessage")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(
                            res -> log.debug("Telegram message delivered successfully"),
                            err -> log.error("Failed to send Telegram message: {}", err.getMessage())
                    );
        } catch (Exception e) {
            log.error("Exception sending Telegram message", e);
        }
    }
}
