package com.platform.registry.infrastructure.security;

import com.platform.common.domain.exception.SsrfSecurityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SsrfGuardTest {

    private DefaultSsrfGuardAdapter ssrfGuard;

    @BeforeEach
    void setUp() {
        ssrfGuard = new DefaultSsrfGuardAdapter();
    }

    @Test
    @DisplayName("Blocks loopback address (localhost)")
    void blocksLoopback() {
        assertThatThrownBy(() -> ssrfGuard.validateUrl("http://localhost:8080/test"))
                .isInstanceOf(SsrfSecurityException.class)
                .hasMessageContaining("loopback");

        assertThatThrownBy(() -> ssrfGuard.validateUrl("http://127.0.0.1:8080/test"))
                .isInstanceOf(SsrfSecurityException.class)
                .hasMessageContaining("loopback");
    }

    @Test
    @DisplayName("Blocks cloud metadata service (169.254.169.254)")
    void blocksCloudMetadata() {
        assertThatThrownBy(() -> ssrfGuard.validateUrl("http://169.254.169.254/latest/meta-data/"))
                .isInstanceOf(SsrfSecurityException.class)
                .hasMessageContaining("Cloud metadata");
    }

    @Test
    @DisplayName("Blocks banned high-stakes proctoring platforms (Non-circumvention rule)")
    void blocksProctoredDomains() {
        assertThatThrownBy(() -> ssrfGuard.validateUrl("https://app.proctorio.com/exam"))
                .isInstanceOf(SsrfSecurityException.class)
                .hasMessageContaining("proctored platform");

        assertThatThrownBy(() -> ssrfGuard.validateUrl("https://respondus.com/lockdown"))
                .isInstanceOf(SsrfSecurityException.class)
                .hasMessageContaining("proctored platform");
    }

    @Test
    @DisplayName("Blocks non-HTTP/HTTPS protocols like file:// or gopher://")
    void blocksInvalidProtocols() {
        assertThatThrownBy(() -> ssrfGuard.validateUrl("file:///etc/passwd"))
                .isInstanceOf(SsrfSecurityException.class)
                .hasMessageContaining("Forbidden URI scheme");

        assertThatThrownBy(() -> ssrfGuard.validateUrl("gopher://127.0.0.1/"))
                .isInstanceOf(SsrfSecurityException.class)
                .hasMessageContaining("Forbidden URI scheme");
    }

    @Test
    @DisplayName("Allows valid public website domain")
    void allowsPublicDomain() {
        // google.com or cloudflare.com resolves to public IPs
        assertThat(ssrfGuard.isAllowedDomain("cloudflare.com")).isTrue();
    }
}
