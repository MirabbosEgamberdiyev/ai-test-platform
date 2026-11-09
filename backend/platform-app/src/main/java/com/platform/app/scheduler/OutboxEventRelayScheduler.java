package com.platform.app.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.iam.application.port.UserRepositoryPort;
import com.platform.iam.domain.model.User;
import com.platform.notification.application.service.TelegramBotService;
import com.platform.testops.application.dto.PracticeResultResponse;
import com.platform.testops.application.port.OutboxEventRepositoryPort;
import com.platform.testops.domain.model.OutboxEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxEventRelayScheduler {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventRelayScheduler.class);

    private final OutboxEventRepositoryPort outboxRepository;
    private final TelegramBotService telegramBotService;
    private final UserRepositoryPort userRepository;
    private final ObjectMapper objectMapper;

    public OutboxEventRelayScheduler(
            OutboxEventRepositoryPort outboxRepository,
            TelegramBotService telegramBotService,
            UserRepositoryPort userRepository,
            ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.telegramBotService = telegramBotService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void processOutboxEvents() {
        List<OutboxEvent> pending = outboxRepository.findPendingEvents(20);
        if (pending.isEmpty()) return;

        log.debug("Found {} pending outbox domain events to relay", pending.size());

        for (OutboxEvent event : pending) {
            try {
                if ("PRACTICE_SESSION_COMPLETED".equals(event.getEventType())) {
                    PracticeResultResponse result = objectMapper.readValue(event.getPayloadJson(), PracticeResultResponse.class);
                    userRepository.findById(com.platform.common.domain.model.UserId.of(result.userId())).ifPresent(user -> {
                        if (user.getTelegramChatId() != null) {
                            telegramBotService.sendPracticeCompletionNotification(
                                    user.getTelegramChatId(),
                                    "Practice Session #" + result.sessionId().toString().substring(0, 8),
                                    result.totalQuestions(),
                                    result.correctAnswers(),
                                    result.accuracyPercentage().doubleValue()
                            );
                        }
                    });
                }

                event.markProcessed();
                outboxRepository.save(event);
                log.info("Relayed Outbox Event [{}] of type [{}]", event.getId(), event.getEventType());
            } catch (Exception e) {
                log.error("Failed to relay outbox event [{}]: {}", event.getId(), e.getMessage());
            }
        }
    }
}
