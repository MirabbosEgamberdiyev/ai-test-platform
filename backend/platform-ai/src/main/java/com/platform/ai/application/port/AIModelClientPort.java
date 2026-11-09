package com.platform.ai.application.port;

import com.platform.ai.domain.model.AIAnalysisRequest;
import com.platform.ai.domain.model.AIAnalysisResult;
import com.platform.ai.domain.model.AIProviderType;

public interface AIModelClientPort {
    AIProviderType getProviderType();
    AIAnalysisResult analyzeQuestion(AIAnalysisRequest request);
}
