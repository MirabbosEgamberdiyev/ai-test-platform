package com.platform.automation.domain.model;

public record DiscoveredRoute(
        String url,
        String path,
        String linkText,
        boolean isInternal
) {}
