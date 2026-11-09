package com.platform.ai.application.service;

import com.platform.ai.application.port.AIModelClientPort;
import com.platform.ai.application.port.TokenTrackerPort;
import com.platform.ai.domain.model.AIAnalysisRequest;
import com.platform.ai.domain.model.AIAnalysisResult;
import com.platform.ai.domain.model.AIProviderType;
import com.platform.ai.domain.model.TokenSpendRecord;
import com.platform.common.domain.model.ConfidenceScore;
import com.platform.common.domain.model.ExtractedOption;
import com.platform.common.domain.model.TenantId;
import com.platform.common.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIRoutingEngineServiceTest {

    @Mock
    private AIModelClientPort geminiClient;
    @Mock
    private AIModelClientPort openAIClient;
    @Mock
    private TokenTrackerPort tokenTracker;

    private AIRoutingEngineService routingEngine;

    @BeforeEach
    void setUp() {
        when(geminiClient.getProviderType()).thenReturn(AIProviderType.GEMINI);
        when(openAIClient.getProviderType()).thenReturn(AIProviderType.OPENAI);

        routingEngine = new AIRoutingEngineService(
                List.of(geminiClient, openAIClient),
                tokenTracker,
                "GEMINI",
                "OPENAI"
        );
    }

    @Test
    @DisplayName("Routes request to Primary provider (Gemini) when healthy")
    void routesToPrimaryProvider() {
        AIAnalysisRequest request = AIAnalysisRequest.ofText(
                "What is the capital of France?",
                List.of(new ExtractedOption("A", "Paris"), new ExtractedOption("B", "Rome"))
        );

        AIAnalysisResult expectedResult = new AIAnalysisResult(
                "A", "Paris is the capital of France",
                ConfidenceScore.of(0.99), 50, 30, 150, "GEMINI", "gemini-1.5-pro"
        );

        when(geminiClient.analyzeQuestion(request)).thenReturn(expectedResult);

        AIAnalysisResult actual = routingEngine.analyze(TenantId.generate(), UserId.generate(), request);

        assertThat(actual).isEqualTo(expectedResult);
        verify(geminiClient).analyzeQuestion(request);
        verify(openAIClient, never()).analyzeQuestion(any());
        verify(tokenTracker).recordSpend(any(TokenSpendRecord.class));
    }

    @Test
    @DisplayName("Gracefully falls back to OpenAI when Primary provider fails")
    void fallbackToSecondaryOnFailure() {
        AIAnalysisRequest request = AIAnalysisRequest.ofText(
                "Solve 2 + 2",
                List.of(new ExtractedOption("A", "4"), new ExtractedOption("B", "5"))
        );

        when(geminiClient.analyzeQuestion(request)).thenThrow(new RuntimeException("Rate limit 429"));

        AIAnalysisResult fallbackResult = new AIAnalysisResult(
                "A", "2 + 2 equals 4",
                ConfidenceScore.of(0.98), 40, 20, 210, "OPENAI", "gpt-4o"
        );
        when(openAIClient.analyzeQuestion(request)).thenReturn(fallbackResult);

        AIAnalysisResult actual = routingEngine.analyze(TenantId.generate(), UserId.generate(), request);

        assertThat(actual).isEqualTo(fallbackResult);
        verify(geminiClient).analyzeQuestion(request);
        verify(openAIClient).analyzeQuestion(request);
        verify(tokenTracker).recordSpend(any(TokenSpendRecord.class));
    }
}
