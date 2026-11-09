package com.platform.ai.application.service;

import com.platform.ai.application.port.AIModelClientPort;
import com.platform.ai.application.port.TokenTrackerPort;
import com.platform.ai.domain.model.AIAnalysisRequest;
import com.platform.ai.domain.model.AIAnalysisResult;
import com.platform.ai.domain.model.AIProviderType;
import com.platform.ai.domain.model.TokenSpendRecord;
import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AIRoutingEngineService {

    private static final Logger log = LoggerFactory.getLogger(AIRoutingEngineService.class);

    private final Map<AIProviderType, AIModelClientPort> providers;
    private final TokenTrackerPort tokenTracker;
    private final AIProviderType primaryProvider;
    private final AIProviderType fallbackProvider;

    public AIRoutingEngineService(
            List<AIModelClientPort> clientPorts,
            TokenTrackerPort tokenTracker,
            @Value("${app.ai.primary-provider:GEMINI}") String primaryProviderName,
            @Value("${app.ai.fallback-provider:OPENAI}") String fallbackProviderName) {
        this.providers = clientPorts.stream()
                .collect(Collectors.toMap(AIModelClientPort::getProviderType, Function.identity()));
        this.tokenTracker = tokenTracker;
        this.primaryProvider = AIProviderType.valueOf(primaryProviderName.toUpperCase());
        this.fallbackProvider = AIProviderType.valueOf(fallbackProviderName.toUpperCase());
    }

    public AIAnalysisResult analyze(TenantId tenantId, UserId userId, AIAnalysisRequest request) {
        AIModelClientPort primaryClient = providers.get(primaryProvider);
        if (primaryClient != null) {
            try {
                log.info("Dispatching question analysis to primary AI provider: {}", primaryProvider);
                AIAnalysisResult result = primaryClient.analyzeQuestion(request);
                recordUsage(tenantId, userId, result);
                return result;
            } catch (Exception e) {
                log.warn("Primary AI provider [{}] failed with error: {}. Triggering fallback to [{}]",
                        primaryProvider, e.getMessage(), fallbackProvider);
            }
        }

        AIModelClientPort fallbackClient = providers.get(fallbackProvider);
        if (fallbackClient != null) {
            try {
                log.info("Dispatching question analysis to fallback AI provider: {}", fallbackProvider);
                AIAnalysisResult result = fallbackClient.analyzeQuestion(request);
                recordUsage(tenantId, userId, result);
                return result;
            } catch (Exception e) {
                log.error("Fallback AI provider [{}] also failed: {}", fallbackProvider, e.getMessage());
                throw new IllegalStateException("All AI inference providers failed to analyze question", e);
            }
        }

        throw new IllegalStateException("No suitable AI provider client configured for execution");
    }

    private void recordUsage(TenantId tenantId, UserId userId, AIAnalysisResult result) {
        try {
            BigDecimal inputRate = result.providerName().equalsIgnoreCase("GEMINI")
                    ? new BigDecimal("0.00035") : new BigDecimal("0.005");
            BigDecimal outputRate = result.providerName().equalsIgnoreCase("GEMINI")
                    ? new BigDecimal("0.00105") : new BigDecimal("0.015");

            TokenSpendRecord record = TokenSpendRecord.create(
                    tenantId,
                    userId,
                    result.providerName(),
                    result.modelName(),
                    result.promptTokens(),
                    result.completionTokens(),
                    inputRate,
                    outputRate
            );
            tokenTracker.recordSpend(record);
        } catch (Exception e) {
            log.error("Failed to record AI token usage metrics", e);
        }
    }
}
