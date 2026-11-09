package com.platform.testops.application.service;

import com.platform.common.domain.model.TenantId;
import com.platform.registry.application.port.SsrfGuardPort;
import com.platform.testops.application.dto.CreateWebProjectCommand;
import com.platform.testops.application.dto.UpdateWebProjectCommand;
import com.platform.testops.application.dto.WebProjectResponse;
import com.platform.testops.application.port.WebTestProjectRepositoryPort;
import com.platform.testops.domain.model.WebTestProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebTestProjectServiceTest {

    @Mock
    private WebTestProjectRepositoryPort projectRepository;
    @Mock
    private SsrfGuardPort ssrfGuard;

    private WebTestProjectService service;

    private final TenantId tenantId = new TenantId(UUID.randomUUID());

    @BeforeEach
    void setUp() {
        service = new WebTestProjectService(projectRepository, ssrfGuard);
    }

    @Test
    @DisplayName("Should create new web testing project successfully")
    void shouldCreateWebProject() {
        CreateWebProjectCommand cmd = new CreateWebProjectCommand(
                "My Shop",
                "https://myshop.example.com",
                "PRODUCTION",
                "E-Commerce website",
                "E-Commerce (Next.js/React)",
                true
        );

        when(projectRepository.save(any(WebTestProject.class))).thenAnswer(i -> i.getArgument(0));

        WebProjectResponse response = service.createProject(tenantId, cmd);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("My Shop");
        assertThat(response.targetUrl()).isEqualTo("https://myshop.example.com");
        assertThat(response.environment()).isEqualTo("PRODUCTION");
        assertThat(response.isAuthorized()).isTrue();
        verify(ssrfGuard).validateUrl("https://myshop.example.com");
        verify(projectRepository).save(any(WebTestProject.class));
    }

    @Test
    @DisplayName("Should update existing web project")
    void shouldUpdateWebProject() {
        UUID projectId = UUID.randomUUID();
        WebTestProject existing = WebTestProject.create(
                tenantId, null, "Old Name", "https://old.example.com",
                "STAGING", "Old Desc", "React", true
        );

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existing));
        when(projectRepository.save(any(WebTestProject.class))).thenAnswer(i -> i.getArgument(0));

        UpdateWebProjectCommand cmd = new UpdateWebProjectCommand(
                "New Name", "https://new.example.com", "PRODUCTION", "Updated", "Vue", true
        );

        WebProjectResponse updated = service.updateProject(tenantId, projectId, cmd);

        assertThat(updated.name()).isEqualTo("New Name");
        assertThat(updated.targetUrl()).isEqualTo("https://new.example.com");
        verify(ssrfGuard).validateUrl("https://new.example.com");
    }

    @Test
    @DisplayName("Should list web projects for tenant")
    void shouldListWebProjects() {
        WebTestProject p1 = WebTestProject.create(tenantId, null, "P1", "https://p1.example.com", "PROD", "d1", "i1", true);
        WebTestProject p2 = WebTestProject.create(tenantId, null, "P2", "https://p2.example.com", "DEV", "d2", "i2", true);

        when(projectRepository.findByTenantId(tenantId)).thenReturn(List.of(p1, p2));

        List<WebProjectResponse> projects = service.listProjects(tenantId);

        assertThat(projects).hasSize(2);
        assertThat(projects.get(0).name()).isEqualTo("P1");
        assertThat(projects.get(1).name()).isEqualTo("P2");
    }
}
