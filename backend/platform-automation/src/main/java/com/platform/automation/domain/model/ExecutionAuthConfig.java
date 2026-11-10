package com.platform.automation.domain.model;

import java.io.Serializable;

/**
 * Encapsulates authentication context for isolated browser execution.
 * Supports SSO (Google, Telegram, OAuth 2.0), Session Cookies, Bearer Tokens (JWT),
 * Storage State JSON (Playwright/Puppeteer), and traditional credentials.
 */
public record ExecutionAuthConfig(
        String authType,         // NONE, GOOGLE_TELEGRAM_SSO, BEARER_TOKEN, SESSION_COOKIE, CREDENTIALS
        String username,
        String password,
        String sessionCookie,
        String bearerToken,
        String storageStateJson
) implements Serializable {

    public static ExecutionAuthConfig none() {
        return new ExecutionAuthConfig("NONE", null, null, null, null, null);
    }

    public static ExecutionAuthConfig sso(String sessionCookie, String username) {
        return new ExecutionAuthConfig("GOOGLE_TELEGRAM_SSO", username, null, sessionCookie, null, null);
    }

    public static ExecutionAuthConfig bearer(String token) {
        return new ExecutionAuthConfig("BEARER_TOKEN", null, null, null, token, null);
    }

    public static ExecutionAuthConfig credentials(String username, String password) {
        return new ExecutionAuthConfig("CREDENTIALS", username, password, null, null, null);
    }

    public boolean isAuthEnabled() {
        if (authType == null || "NONE".equalsIgnoreCase(authType)) {
            return (sessionCookie != null && !sessionCookie.isBlank())
                    || (bearerToken != null && !bearerToken.isBlank())
                    || (username != null && !username.isBlank() && password != null && !password.isBlank());
        }
        return true;
    }

    public boolean isSsoOrCookie() {
        return "GOOGLE_TELEGRAM_SSO".equalsIgnoreCase(authType)
                || "SESSION_COOKIE".equalsIgnoreCase(authType)
                || (sessionCookie != null && !sessionCookie.isBlank());
    }

    public boolean isBearer() {
        return "BEARER_TOKEN".equalsIgnoreCase(authType)
                || (bearerToken != null && !bearerToken.isBlank());
    }
}
