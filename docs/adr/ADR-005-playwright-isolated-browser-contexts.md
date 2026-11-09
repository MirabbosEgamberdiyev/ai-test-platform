# ADR-005: Playwright Java with Ephemeral Isolated Browser Contexts

## Status
Accepted

## Context
Browser automation requires rendering complex JavaScript single-page applications, extracting interactive DOM nodes, capturing screenshots of graphical questions, and interacting with test controls. Selenium is slow and resource-heavy; raw Puppeteer lacks type-safe Java bindings. Cross-session contamination or cookie leaks between tenants would be a catastrophic security breach.

## Decision
We select **Playwright Java** with strict isolation:
1. Playwright utilizes a single browser process instance per worker but spawns a brand new, ephemeral `BrowserContext` for every single practice session.
2. An isolated `BrowserContext` operates like an incognito browser window: separate cache, separate cookies, separate local storage, and independent network routing.
3. Upon practice completion, timeout, or failure, the `BrowserContext` is deterministically closed and destroyed, releasing all memory and cached artifacts.
4. Egress network filters prevent connections to internal private IP ranges (SSRF defense).

## Consequences
- **Positive**: Blazing fast context creation (< 50ms) compared to spinning up separate browser processes; complete tenant credential and cookie isolation; native auto-waiting for dynamic DOM elements.
- **Negative**: High RAM usage under heavy load (~80-150MB per concurrent active context); managed by capping max concurrent sessions and scaling worker pods horizontally.
