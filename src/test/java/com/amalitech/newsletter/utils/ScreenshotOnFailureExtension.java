package com.amalitech.newsletter.utils;

import com.amalitech.newsletter.tests.BaseTest;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * JUnit 5 extension: when a test fails, save a PNG screenshot to target/screenshots.
 * AfterTestExecutionCallback runs BEFORE @AfterEach, so the browser is still open.
 * The CI pipeline uploads this folder as an artifact for debugging.
 */
public class ScreenshotOnFailureExtension implements AfterTestExecutionCallback {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenshotOnFailureExtension.class);
    private static final Path DIR = Path.of("target", "screenshots");

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isEmpty()) {
            return;
        }
        // For @Nested classes the innermost instance is not a BaseTest, so search all enclosing instances
        WebDriver driver = context.getRequiredTestInstances().getAllInstances().stream()
                .filter(BaseTest.class::isInstance)
                .map(i -> ((BaseTest) i).getDriver())
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElse(null);
        if (driver == null) {
            return;
        }
        String name = (context.getRequiredTestClass().getSimpleName() + "_" + context.getDisplayName())
                .replaceAll("[^a-zA-Z0-9-_]", "_");
        try {
            Files.createDirectories(DIR);
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Path file = DIR.resolve(name + ".png");
            Files.write(file, png);
            LOG.error("Test FAILED – screenshot saved to {}", file.toAbsolutePath());
        } catch (IOException | RuntimeException e) {
            LOG.warn("Could not capture screenshot: {}", e.getMessage());
        }
    }
}
