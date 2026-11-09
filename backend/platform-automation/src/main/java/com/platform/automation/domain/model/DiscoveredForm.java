package com.platform.automation.domain.model;

import java.util.List;

public record DiscoveredForm(
        String formId,
        String action,
        String method,
        String formType, // LOGIN, REGISTRATION, SEARCH, CONTACT, GENERIC
        List<DiscoveredField> fields,
        String submitButtonText
) {}
