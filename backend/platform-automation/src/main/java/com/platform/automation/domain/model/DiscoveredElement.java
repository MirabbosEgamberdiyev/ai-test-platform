package com.platform.automation.domain.model;

public record DiscoveredElement(
        String tagName,
        String text,
        String selector,
        String role,
        String ariaLabel,
        String type
) {}
