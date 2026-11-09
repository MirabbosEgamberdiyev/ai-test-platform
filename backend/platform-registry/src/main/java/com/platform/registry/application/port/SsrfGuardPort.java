package com.platform.registry.application.port;

import java.net.URI;

public interface SsrfGuardPort {
    void validateUrl(String url);
    void validateUri(URI uri);
    boolean isAllowedDomain(String host);
}
