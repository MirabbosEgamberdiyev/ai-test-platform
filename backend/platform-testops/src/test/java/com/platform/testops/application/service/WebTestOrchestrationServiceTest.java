package com.platform.testops.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.ai.application.service.UniversalTestPlanGeneratorService;
import com.platform.ai.domain.model.DefectAnalysisResult;
import com.platform.ai.domain.model.GeneratedTestCase;
import com.platform.ai.domain.model.GeneratedTestPlan;
import com.platform.automation.application.service.UniversalAutomationService;
import com.platform.automation.domain.model.ExecutionDefectData;
import com.platform.automation.domain.model.TestRunExecutionSummary;
import com.platform.automation.domain.model.WebsiteDiscoveryResult;
import com.platform.common.domain.model.TenantId;
import com.platform.testops.application.dto.CreateWebTestPlanCommand;
import com.platform.testops.application.dto.StartWebTestRunCommand;
import com.platform.testops.application.dto.WebDashboardStatsResponse;
import com.platform.testops.application.dto.WebTestPlanResponse;
import com.platform.testops.application.dto.WebTestRunResponse;
import com.platform.testops.application.port.WebDefectRepositoryPort;
import com.platform.testops.application.port.WebTestCaseRepositoryPort;
import com.platform.testops.application.port.WebTestPlanRepositoryPort;
import com.platform.testops.application.port.WebTestProjectRepositoryPort;
import com.platform.testops.application.port.WebTestReportRepositoryPort;
import com.platform.testops.application.port.WebTestRunRepositoryPort;
import com.platform.testops.domain.model.WebDefect;
import com.platform.testops.domain.model.WebTestCase;
import com.platform.testops.domain.model.WebTestCaseCategory;
import com.platform.testops.domain.model.WebTestPlan;
import com.platform.testops.domain.model.WebTestProject;
import com.platform.testops.domain.model.WebTestReport;
import com.platform.testops.domain.model.WebTestRun;
import com.platform.testops.domain.model.WebTestRunStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebTestOrchestrationServiceTest {

    @Mock
    private WebTestProjectRepositoryPort projectRepository;
    @Mock
    private WebTestPlanRepositoryPort planRepository;
    @Mock
    private WebTestCaseRepositoryPort testCaseRepository;
    @Mock
    private WebTestRunRepositoryPort testRunRepository;
    @Mock
    private WebDefectRepositoryPort defectRepository;
    @Mock
    private WebTestReportRepositoryPort reportRepository;
    @Mock
    private UniversalAutomationService automationService;
    @Mock
    private UniversalTestPlanGeneratorService aiPlanGenerator;

    private ObjectMapper objectMapper;
    private WebTestOrchestrationService service;

    private final TenantId tenantId = new TenantId(UUID.randomUUID());
    private final UUID projectId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new WebTestOrchestrationService(
                projectRepository,
                planRepository,
                testCaseRepository,
                testRunRepository,
                defectRepository,
                reportRepository,
                automationService,
                aiPlanGenerator,
                objectMapper
        );
    }

    @Test
    @DisplayName("Should generate AI web test plan using natural language prompt")
    void shouldGenerateTestPlan() {
        WebTestProject project = WebTestProject.create(
                tenantId, null, "Store", "https://store.example.com",
                "PRODUCTION", "Store", "Vue", true
        );

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        WebsiteDiscoveryResult discovery = new WebsiteDiscoveryResult(
                "https://store.example.com", "Store Home", "Online Store", "Vue",
                true, false, List.of(), List.of(), List.of(), "snap.png", "A11y pass", Instant.now()
        );
        when(automationService.discoverWebsite("https://store.example.com")).thenReturn(discovery);

        GeneratedTestPlan generated = new GeneratedTestPlan(
                "Test checkout",
                "FUNCTIONAL, RESPONSIVE",
                "uz-Latn",
                "Discovered 0 routes",
                List.of(
                        new GeneratedTestCase("Check Title", "FUNCTIONAL", "HIGH", "[]", "Title visible", 1),
                        new GeneratedTestCase("Check Mobile", "RESPONSIVE", "MEDIUM", "[]", "No overflow", 2)
                )
        );
        when(aiPlanGenerator.generatePlan(eq("https://store.example.com"), eq("Test checkout"), eq("uz-Latn"), any(), any()))
                .thenReturn(generated);

        when(planRepository.save(any(WebTestPlan.class))).thenAnswer(i -> i.getArgument(0));

        CreateWebTestPlanCommand cmd = new CreateWebTestPlanCommand(
                projectId,
                "Test checkout",
                "uz-Latn",
                List.of("FUNCTIONAL", "RESPONSIVE")
        );

        WebTestPlanResponse response = service.generateTestPlan(tenantId, cmd);

        assertThat(response).isNotNull();
        assertThat(response.projectId()).isEqualTo(project.getId());
        assertThat(response.language()).isEqualTo("uz-Latn");
        assertThat(response.status()).isEqualTo("DRAFT");
        verify(planRepository).save(any(WebTestPlan.class));
    }

    @Test
    @DisplayName("Should approve test plan")
    void shouldApprovePlan() {
        UUID planId = UUID.randomUUID();
        WebTestPlan plan = new WebTestPlan(
                planId, projectId, tenantId, "prompt", "FUNCTIONAL", "uz-Latn",
                "DRAFT", "summary", 0L, Instant.now(), Instant.now()
        );

        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(planRepository.save(any(WebTestPlan.class))).thenAnswer(i -> i.getArgument(0));

        WebTestPlanResponse approved = service.approveTestPlan(tenantId, planId);

        assertThat(approved.status()).isEqualTo("APPROVED");
        verify(planRepository).save(any(WebTestPlan.class));
    }

    @Test
    @DisplayName("Should execute browser test run and persist defect report")
    void shouldExecuteTestRun() {
        UUID planId = UUID.randomUUID();
        WebTestProject project = WebTestProject.create(
                tenantId, null, "Store", "https://store.example.com",
                "PRODUCTION", "Store", "React", true
        );
        WebTestPlan plan = new WebTestPlan(
                planId, project.getId(), tenantId, "prompt", "FUNCTIONAL", "uz-Latn",
                "APPROVED", "summary", 0L, Instant.now(), Instant.now()
        );
        WebTestCase tc1 = WebTestCase.create(
                planId, tenantId, "Load Page", WebTestCaseCategory.FUNCTIONAL,
                "HIGH", "[]", "Title is present", 1
        );

        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(testCaseRepository.findByPlanId(planId)).thenReturn(List.of(tc1));
        when(testRunRepository.save(any(WebTestRun.class))).thenAnswer(i -> i.getArgument(0));

        TestRunExecutionSummary summary = new TestRunExecutionSummary(
                1,
                0,
                1,
                0,
                0,
                500L,
                "Execution finished",
                "trace logs",
                List.of(new ExecutionDefectData(
                        tc1.getId(),
                        "DEF-001",
                        "Button not clickable",
                        "HIGH",
                        "Click buy button",
                        "Modal opens",
                        "Element intercepted",
                        "defect.png",
                        "trace logs",
                        "[]",
                        "wait issue",
                        0.95
                ))
        );
        when(automationService.executeTests(any(UUID.class), eq("https://store.example.com"), eq("CHROMIUM"), eq(1280), eq(800), any()))
                .thenReturn(summary);

        DefectAnalysisResult analysis = new DefectAnalysisResult("Element overlap", "Add z-index or wait", "CONFIRMED_DEFECT", 0.95);
        when(aiPlanGenerator.analyzeDefect(any(), any(), any(), any())).thenReturn(analysis);
        when(defectRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));
        when(reportRepository.save(any(WebTestReport.class))).thenAnswer(i -> i.getArgument(0));

        StartWebTestRunCommand cmd = new StartWebTestRunCommand(
                planId, "CHROMIUM", 1280, 800
        );

        WebTestRunResponse runResponse = service.startTestRun(tenantId, cmd);

        assertThat(runResponse).isNotNull();
        assertThat(runResponse.status()).isEqualTo(WebTestRunStatus.FAILED);
        assertThat(runResponse.totalCases()).isEqualTo(1);
        assertThat(runResponse.passedCases()).isEqualTo(0);
        assertThat(runResponse.failedCases()).isEqualTo(1);
        verify(defectRepository).saveAll(any());
        verify(reportRepository).save(any(WebTestReport.class));
    }

    @Test
    @DisplayName("Should aggregate dashboard metrics correctly")
    void shouldAggregateDashboardStats() {
        WebTestProject p1 = WebTestProject.create(tenantId, null, "P1", "https://p1.com", "PROD", "d1", "i1", true);
        when(projectRepository.findByTenantId(tenantId)).thenReturn(List.of(p1));
        when(testRunRepository.countByTenantId(tenantId)).thenReturn(1L);
        when(defectRepository.countByTenantId(tenantId)).thenReturn(2L);

        WebTestRun run1 = WebTestRun.create(projectId, UUID.randomUUID(), tenantId, "CHROMIUM", 1280, 800);
        run1.complete(10, 8, 2, 0, 0, "Execution finished", "logs");
        when(testRunRepository.findByTenantId(tenantId)).thenReturn(List.of(run1));

        WebDashboardStatsResponse stats = service.getDashboardStats(tenantId);

        assertThat(stats.totalProjects()).isEqualTo(1L);
        assertThat(stats.totalTestRuns()).isEqualTo(1L);
        assertThat(stats.criticalDefects()).isEqualTo(2L);
        assertThat(stats.passedTests()).isEqualTo(8);
        assertThat(stats.failedTests()).isEqualTo(2);
        assertThat(stats.successRatePercentage()).isEqualTo(80.0);
    }
}
