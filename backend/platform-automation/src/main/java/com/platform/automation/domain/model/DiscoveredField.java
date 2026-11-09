package com.platform.automation.domain.model;

public record DiscoveredField(
        String name,
        String type, // text, password, email, checkbox, etc.
        String placeholder,
        boolean required,
        String label
) {}
