package com.platform.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PlatformApplicationSmokeTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Spring ApplicationContext bootstraps cleanly with all modules and Flyway schema")
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
        assertThat(applicationContext.containsBean("authenticationService")).isTrue();
        assertThat(applicationContext.containsBean("practiceOrchestrationService")).isTrue();
        assertThat(applicationContext.containsBean("AIRoutingEngineService")).isTrue();
        assertThat(applicationContext.containsBean("websiteRegistryService")).isTrue();
    }
}
