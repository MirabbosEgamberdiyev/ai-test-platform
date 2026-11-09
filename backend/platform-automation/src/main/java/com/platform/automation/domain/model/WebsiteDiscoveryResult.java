package com.platform.automation.domain.model;

import java.time.Instant;
import java.util.List;

public record WebsiteDiscoveryResult(
        String targetUrl,
        String pageTitle,
        String metaDescription,
        String detectedFramework, // e.g. "React / Next.js", "WordPress / PHP", "Django", "Generic Web"
        boolean hasLoginForm,
        boolean hasRegistrationForm,
        List<DiscoveredRoute> routes,
        List<DiscoveredForm> forms,
        List<DiscoveredElement> interactiveElements,
        String screenshotPath,
        String accessibilitySummary,
        Instant discoveredAt
) {}
