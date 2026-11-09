package com.platform.testops.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.ai.application.service.UniversalTestPlanGeneratorService;
import com.platform.ai.domain.model.DefectAnalysisResult;
import com.platform.ai.domain.model.GeneratedTestCase;
import com.platform.ai.domain.model.GeneratedTestPlan;
import com.platform.automation.application.service.UniversalAutomationService;
import com.platform.automation.domain.model.ExecutableTestCase;
import com.platform.automation.domain.model.ExecutionDefectData;
import com.platform.automation.domain.model.TestRunExecutionSummary;
import com.platform.automation.domain.model.WebsiteDiscoveryResult;
import com.platform.common.domain.exception.EntityNotFoundException;
import com.platform.common.domain.model.TenantId;
import com.platform.testops.application.dto.CreateWebTestPlanCommand;
import com.platform.testops.application.dto.StartWebTestRunCommand;
import com.platform.testops.application.dto.WebDashboardStatsResponse;
import com.platform.testops.application.dto.WebDefectResponse;
import com.platform.testops.application.dto.WebTestCaseDto;
import com.platform.testops.application.dto.WebTestPlanResponse;
import com.platform.testops.application.dto.WebTestReportResponse;
import com.platform.testops.application.dto.WebTestRunResponse;
import com.platform.testops.application.port.WebDefectRepositoryPort;
import com.platform.testops.application.port.WebTestCaseRepositoryPort;
import com.platform.testops.application.port.WebTestPlanRepositoryPort;
import com.platform.testops.application.port.WebTestProjectRepositoryPort;
import com.platform.testops.application.port.WebTestReportRepositoryPort;
import com.platform.testops.application.port.WebTestRunRepositoryPort;
import com.platform.testops.domain.model.WebDefect;
import com.platform.testops.domain.model.WebDefectSeverity;
import com.platform.testops.domain.model.WebTestCase;
import com.platform.testops.domain.model.WebTestCaseCategory;
import com.platform.testops.domain.model.WebTestPlan;
import com.platform.testops.domain.model.WebTestProject;
import com.platform.testops.domain.model.WebTestReport;
import com.platform.testops.domain.model.WebTestRun;
import com.platform.testops.domain.model.WebTestRunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WebTestOrchestrationService {

    private static final Logger log = LoggerFactory.getLogger(WebTestOrchestrationService.class);

    private final WebTestProjectRepositoryPort projectRepository;
    private final WebTestPlanRepositoryPort planRepository;
    private final WebTestCaseRepositoryPort testCaseRepository;
    private final WebTestRunRepositoryPort testRunRepository;
    private final WebDefectRepositoryPort defectRepository;
    private final WebTestReportRepositoryPort reportRepository;
    private final UniversalAutomationService automationService;
    private final UniversalTestPlanGeneratorService aiPlanGenerator;
    private final ObjectMapper objectMapper;

    public WebTestOrchestrationService(
            WebTestProjectRepositoryPort projectRepository,
            WebTestPlanRepositoryPort planRepository,
            WebTestCaseRepositoryPort testCaseRepository,
            WebTestRunRepositoryPort testRunRepository,
            WebDefectRepositoryPort defectRepository,
            WebTestReportRepositoryPort reportRepository,
            UniversalAutomationService automationService,
            UniversalTestPlanGeneratorService aiPlanGenerator,
            ObjectMapper objectMapper) {
        this.projectRepository = projectRepository;
        this.planRepository = planRepository;
        this.testCaseRepository = testCaseRepository;
        this.testRunRepository = testRunRepository;
        this.defectRepository = defectRepository;
        this.reportRepository = reportRepository;
        this.automationService = automationService;
        this.aiPlanGenerator = aiPlanGenerator;
        this.objectMapper = objectMapper;
    }

    public WebsiteDiscoveryResult discoverWebsite(TenantId tenantId, UUID projectId) {
        WebTestProject project = projectRepository.findById(projectId)
                .filter(p -> p.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("Web test project not found: " + projectId));

        return automationService.discoverWebsite(project.getTargetUrl());
    }

    public WebTestPlanResponse generateTestPlan(TenantId tenantId, CreateWebTestPlanCommand command) {
        WebTestProject project = projectRepository.findById(command.projectId())
                .filter(p -> p.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("Web test project not found: " + command.projectId()));

        WebsiteDiscoveryResult discovery = automationService.discoverWebsite(project.getTargetUrl());

        GeneratedTestPlan generated = aiPlanGenerator.generatePlan(
                project.getTargetUrl(),
                command.naturalLanguageGoal(),
                command.language(),
                command.scopes(),
                discovery.accessibilitySummary()
        );

        WebTestPlan plan = WebTestPlan.create(
                project.getId(),
                tenantId,
                generated.naturalLanguageGoal(),
                generated.detectedScope(),
                generated.language(),
                generated.discoverySummary()
        );

        WebTestPlan savedPlan = planRepository.save(plan);

        List<WebTestCase> testCases = new ArrayList<>();
        for (GeneratedTestCase gtc : generated.testCases()) {
            WebTestCaseCategory category;
            try {
                category = WebTestCaseCategory.valueOf(gtc.category().toUpperCase());
            } catch (Exception e) {
                category = WebTestCaseCategory.FUNCTIONAL;
            }

            testCases.add(WebTestCase.create(
                    savedPlan.getId(),
                    tenantId,
                    gtc.title(),
                    category,
                    gtc.priority(),
                    gtc.stepsJson(),
                    gtc.expectedResult(),
                    gtc.orderIndex()
            ));
        }

        List<WebTestCase> savedCases = testCaseRepository.saveAll(testCases);
        List<WebTestCaseDto> dtoList = savedCases.stream().map(WebTestCaseDto::fromDomain).toList();

        return WebTestPlanResponse.fromDomain(savedPlan, dtoList);
    }

    public WebTestPlanResponse approveTestPlan(TenantId tenantId, UUID planId) {
        WebTestPlan plan = planRepository.findById(planId)
                .filter(p -> p.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("Web test plan not found: " + planId));

        plan.approve();
        WebTestPlan saved = planRepository.save(plan);
        List<WebTestCaseDto> cases = testCaseRepository.findByPlanId(planId)
                .stream()
                .map(WebTestCaseDto::fromDomain)
                .toList();

        return WebTestPlanResponse.fromDomain(saved, cases);
    }

    public WebTestRunResponse startTestRun(TenantId tenantId, StartWebTestRunCommand command) {
        WebTestPlan plan = planRepository.findById(command.planId())
                .filter(p -> p.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("Web test plan not found: " + command.planId()));

        WebTestProject project = projectRepository.findById(plan.getProjectId())
                .filter(p -> p.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("Web test project not found: " + plan.getProjectId()));

        String browser = command.browserType() != null ? command.browserType().toUpperCase() : "CHROMIUM";
        int width = command.viewportWidth() != null && command.viewportWidth() > 0 ? command.viewportWidth() : 1280;
        int height = command.viewportHeight() != null && command.viewportHeight() > 0 ? command.viewportHeight() : 800;

        WebTestRun run = WebTestRun.create(project.getId(), plan.getId(), tenantId, browser, width, height);
        run.start();
        WebTestRun savedRun = testRunRepository.save(run);

        List<WebTestCase> cases = testCaseRepository.findByPlanId(plan.getId());
        List<ExecutableTestCase> executableCases = cases.stream()
                .map(c -> new ExecutableTestCase(c.getId(), c.getTitle(), c.getCategory().name(), c.getStepsJson(), c.getExpectedResult()))
                .toList();

        // Execute deterministic tests
        TestRunExecutionSummary summary = automationService.executeTests(
                savedRun.getId(),
                project.getTargetUrl(),
                browser,
                width,
                height,
                executableCases
        );

        // Process Defects and AI Root-Cause Analysis
        List<WebDefect> savedDefects = new ArrayList<>();
        int criticalCount = 0;
        int highCount = 0;
        int mediumCount = 0;
        int lowCount = 0;

        for (ExecutionDefectData ed : summary.defects()) {
            DefectAnalysisResult analysis = aiPlanGenerator.analyzeDefect(
                    ed.title(),
                    ed.actualResult(),
                    ed.consoleLogs(),
                    ed.networkEvidence()
            );

            WebDefectSeverity severity;
            try {
                severity = WebDefectSeverity.valueOf(ed.severity().toUpperCase());
            } catch (Exception e) {
                severity = WebDefectSeverity.MEDIUM;
            }

            if (severity == WebDefectSeverity.CRITICAL) criticalCount++;
            else if (severity == WebDefectSeverity.HIGH) highCount++;
            else if (severity == WebDefectSeverity.MEDIUM) mediumCount++;
            else lowCount++;

            savedDefects.add(WebDefect.create(
                    savedRun.getId(),
                    ed.caseId(),
                    tenantId,
                    ed.defectCode(),
                    ed.title(),
                    severity,
                    ed.reproductionSteps(),
                    ed.expectedResult(),
                    ed.actualResult(),
                    ed.screenshotPath(),
                    ed.consoleLogs(),
                    ed.networkEvidence(),
                    analysis.rootCauseHypothesis() + " Suggested fix: " + analysis.recommendedFix(),
                    analysis.confidenceScore()
            ));
        }

        if (!savedDefects.isEmpty()) {
            defectRepository.saveAll(savedDefects);
        }

        // Complete the Run
        savedRun.complete(
                summary.totalCases(),
                summary.passedCases(),
                summary.failedCases(),
                summary.blockedCases(),
                summary.skippedCases(),
                summary.errorSummary(),
                summary.executionLogs()
        );
        WebTestRun finalizedRun = testRunRepository.save(savedRun);

        // Generate and Save Web Test Report
        double passPct = summary.totalCases() > 0 ? ((double) summary.passedCases() / summary.totalCases()) * 100.0 : 100.0;
        String aiExecSummary = String.format("Universal test run completed across %d test cases. %d passed, %d failed. Discovered %d critical, %d high, %d medium, and %d low severity defects.",
                summary.totalCases(), summary.passedCases(), summary.failedCases(), criticalCount, highCount, mediumCount, lowCount);
        String aiRecommendations = summary.failedCases() > 0
                ? "Address identified responsive layout overflows and accessibility label attributes before production release."
                : "All primary automated journeys, responsive checks, and accessibility standards validated successfully.";

        WebTestReport report = WebTestReport.create(
                finalizedRun.getId(),
                tenantId,
                project.getName(),
                project.getTargetUrl(),
                summary.durationMs(),
                passPct,
                criticalCount,
                highCount,
                mediumCount,
                lowCount,
                aiExecSummary,
                aiRecommendations
        );
        reportRepository.save(report);

        return WebTestRunResponse.fromDomain(finalizedRun);
    }

    @Transactional(readOnly = true)
    public WebTestRunResponse getTestRun(TenantId tenantId, UUID runId) {
        WebTestRun run = testRunRepository.findById(runId)
                .filter(r -> r.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("Web test run not found: " + runId));
        return WebTestRunResponse.fromDomain(run);
    }

    @Transactional(readOnly = true)
    public List<WebTestRunResponse> listTestRuns(TenantId tenantId) {
        return testRunRepository.findByTenantId(tenantId)
                .stream()
                .map(WebTestRunResponse::fromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WebDefectResponse> getDefects(TenantId tenantId, UUID runId) {
        return defectRepository.findByRunId(runId)
                .stream()
                .filter(d -> d.getTenantId().equals(tenantId))
                .map(WebDefectResponse::fromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public WebTestReportResponse getReport(TenantId tenantId, UUID runId) {
        WebTestReport report = reportRepository.findByRunId(runId)
                .filter(r -> r.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("Web test report not found for run: " + runId));

        List<WebDefectResponse> defects = getDefects(tenantId, runId);
        return WebTestReportResponse.fromDomain(report, defects);
    }

    @Transactional(readOnly = true)
    public WebDashboardStatsResponse getDashboardStats(TenantId tenantId) {
        long totalProjects = projectRepository.findByTenantId(tenantId).size();
        long totalRuns = testRunRepository.countByTenantId(tenantId);
        List<WebTestRun> runs = testRunRepository.findByTenantId(tenantId);
        long passedCount = runs.stream().mapToLong(WebTestRun::getPassedCases).sum();
        long failedCount = runs.stream().mapToLong(WebTestRun::getFailedCases).sum();
        long activeCount = runs.stream().filter(r -> r.getStatus() == WebTestRunStatus.RUNNING).count();
        long totalDefects = defectRepository.countByTenantId(tenantId);

        long totalTested = passedCount + failedCount;
        double successRate = totalTested > 0 ? ((double) passedCount / totalTested) * 100.0 : 100.0;

        return new WebDashboardStatsResponse(
                totalProjects,
                totalProjects, // each project maps to a registered test website
                totalRuns,
                passedCount,
                failedCount,
                activeCount,
                totalDefects,
                Math.round(successRate * 10.0) / 10.0
        );
    }

    @Transactional(readOnly = true)
    public String exportReport(TenantId tenantId, UUID runId, String format) {
        WebTestReportResponse report = getReport(tenantId, runId);
        if ("json".equalsIgnoreCase(format)) {
            try {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(report);
            } catch (Exception e) {
                return "{\"error\":\"Export failed\"}";
            }
        } else if ("csv".equalsIgnoreCase(format)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Defect Code,Title,Severity,Expected,Actual,AI Root Cause\n");
            for (WebDefectResponse d : report.defects()) {
                sb.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        d.defectCode(), d.title(), d.severity(), d.expectedResult(), d.actualResult(), d.aiRootCause()));
            }
            return sb.toString();
        } else {
            // Standalone HTML report
            return generateHtmlReport(report);
        }
    }

    private String generateHtmlReport(WebTestReportResponse report) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Test Report - ")
                .append(report.websiteName()).append("</title>")
                .append("<style>")
                .append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #0f172a; color: #f8fafc; padding: 40px; }")
                .append(".container { max-width: 900px; margin: 0 auto; background: #1e293b; border-radius: 12px; padding: 32px; border: 1px solid #334155; }")
                .append("h1 { color: #38bdf8; margin-top: 0; }")
                .append(".stats { display: flex; gap: 20px; margin: 24px 0; }")
                .append(".stat-card { background: #0f172a; padding: 16px; border-radius: 8px; border: 1px solid #334155; flex: 1; }")
                .append(".stat-val { font-size: 24px; font-weight: bold; color: #38bdf8; }")
                .append(".defect { background: #0f172a; border-left: 4px solid #ef4444; padding: 16px; margin: 16px 0; border-radius: 4px; }")
                .append(".badge { display: inline-block; padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; }")
                .append(".badge-high { background: #7f1d1d; color: #fca5a5; }")
                .append(".badge-med { background: #78350f; color: #fcd34d; }")
                .append("</style></head><body><div class='container'>")
                .append("<h1>Enterprise Test Report</h1>")
                .append("<p><strong>Website:</strong> ").append(report.websiteName()).append(" (").append(report.targetUrl()).append(")</p>")
                .append("<p><strong>Date:</strong> ").append(report.createdAt()).append("</p>")
                .append("<div class='stats'>")
                .append("<div class='stat-card'><div>Pass Rate</div><div class='stat-val'>").append(report.passPercentage()).append("%</div></div>")
                .append("<div class='stat-card'><div>Duration</div><div class='stat-val'>").append(report.executionDurationMs()).append(" ms</div></div>")
                .append("<div class='stat-card'><div>Defects</div><div class='stat-val'>").append(report.defects().size()).append("</div></div>")
                .append("</div>")
                .append("<h3>AI Executive Summary</h3>")
                .append("<p>").append(report.aiExecutiveSummary()).append("</p>")
                .append("<h3>Discovered Defects</h3>");

        if (report.defects().isEmpty()) {
            html.append("<p style='color: #4ade80;'>No defects detected! All automated validations passed.</p>");
        } else {
            for (WebDefectResponse d : report.defects()) {
                html.append("<div class='defect'>")
                        .append("<strong>").append(d.defectCode()).append(": ").append(d.title()).append("</strong> ")
                        .append("<span class='badge badge-high'>").append(d.severity()).append("</span>")
                        .append("<p><em>Expected:</em> ").append(d.expectedResult()).append("</p>")
                        .append("<p><em>Actual:</em> ").append(d.actualResult()).append("</p>")
                        .append("<p><strong>AI Root Cause & Fix:</strong> ").append(d.aiRootCause()).append("</p>")
                        .append("</div>");
            }
        }

        html.append("</div></body></html>");
        return html.toString();
    }
}
