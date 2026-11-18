package com.amalitech.newsletter.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

/**
 * Single source of run-time settings.
 * <p>
 * Defaults live in {@code src/test/resources/config.properties} (configuration is kept out of code);
 * a JVM system property with the same key overrides the file, e.g. {@code mvn test -Dheadless=false}.
 */
public final class Config {

    private static final String CONFIG_FILE = "config.properties";
    private static final Path LOCAL_APP = Path.of("app", "index.html");
    private static final Properties FILE_PROPERTIES = loadFile();

    private Config() {
        // utility class – no instances
    }

    /** URL of the page under test. Empty value = the local copy of the app in this repository. */
    public static String baseUrl() {
        String url = get("baseUrl");
        return url.isEmpty() ? LOCAL_APP.toAbsolutePath().toUri().toString() : url;
    }

    public static String browser() {
        return get("browser").toLowerCase();
    }

    /** Headless is always on in CI (GitHub Actions sets CI=true) because runners have no screen. */
    public static boolean headless() {
        return isRunningOnCi() || Boolean.parseBoolean(get("headless"));
    }

    public static Duration explicitWait() {
        return Duration.ofSeconds(Long.parseLong(get("explicitWaitSeconds")));
    }

    private static boolean isRunningOnCi() {
        return "true".equalsIgnoreCase(System.getenv("CI"));
    }

    /** System property wins over the file; blank values fall back to the file. */
    private static String get(String key) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override.trim();
        }
        return FILE_PROPERTIES.getProperty(key, "").trim();
    }

    private static Properties loadFile() {
        Properties properties = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IllegalStateException(CONFIG_FILE + " not found on the test classpath");
            }
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + CONFIG_FILE, e);
        }
    }
}
