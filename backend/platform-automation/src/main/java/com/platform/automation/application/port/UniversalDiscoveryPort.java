package com.platform.automation.application.port;

import com.platform.automation.domain.model.WebsiteDiscoveryResult;

public interface UniversalDiscoveryPort {
    WebsiteDiscoveryResult discoverWebsite(String targetUrl);
}
