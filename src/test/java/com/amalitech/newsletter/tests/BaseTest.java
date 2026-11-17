package com.amalitech.newsletter.tests;

import com.amalitech.newsletter.config.DriverFactory;
import com.amalitech.newsletter.utils.ScreenshotOnFailureExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common setup/teardown for every UI test.
 * Each test gets a fresh browser (@BeforeEach) that is always closed afterwards (@AfterEach),
 * so tests are independent and can run in any order.
 */
@ExtendWith(ScreenshotOnFailureExtension.class)
public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected WebDriver driver;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        log.info("========== START: {} ==========", testInfo.getDisplayName());
        driver = DriverFactory.createDriver();
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        if (driver != null) {
            driver.quit();
        }
        log.info("========== END:   {} ==========", testInfo.getDisplayName());
    }

    public WebDriver getDriver() {
        return driver;
    }
}
