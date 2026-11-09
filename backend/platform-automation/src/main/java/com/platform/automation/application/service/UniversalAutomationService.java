package com.platform.automation.application.service;

import com.platform.automation.application.port.UniversalDiscoveryPort;
import com.platform.automation.application.port.UniversalExecutionPort;
import com.platform.automation.domain.model.ExecutableTestCase;
import com.platform.automation.domain.model.TestRunExecutionSummary;
import com.platform.automation.domain.model.WebsiteDiscoveryResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UniversalAutomationService {

    private final UniversalDiscoveryPort discoveryPort;
    private final UniversalExecutionPort executionPort;

    public UniversalAutomationService(UniversalDiscoveryPort discoveryPort, UniversalExecutionPort executionPort) {
        this.discoveryPort = discoveryPort;
        this.executionPort = executionPort;
    }

    public WebsiteDiscoveryResult discoverWebsite(String targetUrl) {
        return discoveryPort.discoverWebsite(targetUrl);
    }

    public TestRunExecutionSummary executeTests(UUID runId, String targetUrl, String browserType,
                                                int width, int height, List<ExecutableTestCase> testCases) {
        return executionPort.executeTests(runId, targetUrl, browserType, width, height, testCases);
    }
}
