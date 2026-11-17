package com.amalitech.newsletter.config;

import java.nio.file.Path;
import java.time.Duration;

/**
 * Central place for run-time settings.
 * Values come from JVM system properties (set by Maven / the CI pipeline) with sensible defaults,
 * so the same tests run locally (visible browser) and in GitHub Actions (headless).
 */
public final class Config {

    private Config() {
    }

    /** URL of the page under test. Defaults to the local copy of the app shipped in this repo. */
    public static String baseUrl() {
        String url = System.getProperty("baseUrl", "").trim();
        if (url.isEmpty()) {
            return Path.of("app", "index.html").toAbsolutePath().toUri().toString();
        }
        return url;
    }

    /** Run Chrome without a visible window. Always true on CI (GitHub sets CI=true). */
    public static boolean headless() {
        if ("true".equalsIgnoreCase(System.getenv("CI"))) {
            return true;
        }
        return Boolean.parseBoolean(System.getProperty("headless", "true"));
    }

    public static String browser() {
        return System.getProperty("browser", "chrome").trim().toLowerCase();
    }

    /** Maximum time an explicit wait polls for a condition before failing. */
    public static Duration explicitWait() {
        return Duration.ofSeconds(Long.getLong("explicitWaitSeconds", 10));
    }
}
