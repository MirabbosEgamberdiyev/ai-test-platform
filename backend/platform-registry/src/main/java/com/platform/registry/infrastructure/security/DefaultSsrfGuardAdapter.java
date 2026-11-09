package com.platform.registry.infrastructure.security;

import com.platform.common.domain.exception.SsrfSecurityException;
import com.platform.registry.application.port.SsrfGuardPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Set;

@Component
public class DefaultSsrfGuardAdapter implements SsrfGuardPort {

    private static final Logger log = LoggerFactory.getLogger(DefaultSsrfGuardAdapter.class);

    // Banned high-stakes proctoring domains (Ethical Non-Circumvention Policy)
    private static final Set<String> PROCTORED_DOMAINS = Set.of(
            "proctorio.com",
            "respondus.com",
            "honorlock.com",
            "examity.com",
            "proctoru.com",
            "pearsonvue.com",
            "ets.org"
    );

    @Override
    public void validateUrl(String urlString) {
        if (urlString == null || urlString.isBlank()) {
            throw new SsrfSecurityException("Target URL cannot be empty");
        }

        try {
            URI uri = URI.create(urlString.trim());
            validateUri(uri);
        } catch (IllegalArgumentException e) {
            throw new SsrfSecurityException("Malformed URI: " + e.getMessage());
        }
    }

    @Override
    public void validateUri(URI uri) {
        String scheme = uri.getScheme();
        if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
            throw new SsrfSecurityException("Forbidden URI scheme: '" + scheme + "'. Only HTTP and HTTPS are permitted.");
        }

        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new SsrfSecurityException("URI host is missing or invalid");
        }

        host = host.toLowerCase().trim();

        // Check proctored / high-stakes blacklist
        for (String banned : PROCTORED_DOMAINS) {
            if (host.equals(banned) || host.endsWith("." + banned)) {
                log.error("SECURITY ALERT: User attempted to connect proctored examination domain: {}", host);
                throw new SsrfSecurityException("Access to high-stakes proctored platform '" + host + "' is strictly forbidden by policy");
            }
        }

        // Resolve DNS and check all resolved IP addresses
        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress address : addresses) {
                checkIpSafety(address);
            }
        } catch (UnknownHostException e) {
            throw new SsrfSecurityException("Could not resolve host: " + host);
        }
    }

    @Override
    public boolean isAllowedDomain(String host) {
        try {
            validateUri(URI.create("https://" + host));
            return true;
        } catch (SsrfSecurityException e) {
            return false;
        }
    }

    private void checkIpSafety(InetAddress address) {
        String ipStr = address.getHostAddress();
        // Cloud metadata protection (AWS / GCP / Azure 169.254.169.254)
        if (ipStr.equals("169.254.169.254") || ipStr.startsWith("169.254.")) {
            throw new SsrfSecurityException("SSRF Blocked: Cloud metadata service access prohibited: " + ipStr);
        }
        if (address.isLoopbackAddress()) {
            throw new SsrfSecurityException("SSRF Blocked: Target resolves to loopback address " + ipStr);
        }
        if (address.isSiteLocalAddress()) {
            throw new SsrfSecurityException("SSRF Blocked: Target resolves to private RFC 1918 subnet " + ipStr);
        }
        if (address.isLinkLocalAddress()) {
            throw new SsrfSecurityException("SSRF Blocked: Target resolves to link-local address " + ipStr);
        }
        if (address.isAnyLocalAddress()) {
            throw new SsrfSecurityException("SSRF Blocked: Target resolves to wildcard/any local address " + ipStr);
        }
    }
}
