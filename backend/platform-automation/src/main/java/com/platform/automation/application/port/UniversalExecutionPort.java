package com.platform.automation.application.port;

import com.platform.automation.domain.model.ExecutableTestCase;
import com.platform.automation.domain.model.ExecutionAuthConfig;
import com.platform.automation.domain.model.TestRunExecutionSummary;

import java.util.List;
import java.util.UUID;

public interface UniversalExecutionPort {
    TestRunExecutionSummary executeTests(UUID runId, String targetUrl, String browserType,
                                        int width, int height, List<ExecutableTestCase> testCases,
                                        ExecutionAuthConfig authConfig);

    default TestRunExecutionSummary executeTests(UUID runId, String targetUrl, String browserType,
                                                int width, int height, List<ExecutableTestCase> testCases) {
        return executeTests(runId, targetUrl, browserType, width, height, testCases, ExecutionAuthConfig.none());
    }
}
