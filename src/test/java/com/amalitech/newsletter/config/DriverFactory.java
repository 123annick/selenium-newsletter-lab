package com.amalitech.newsletter.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates configured WebDriver instances – the only class that knows how a browser is started.
 * <p>
 * Selenium Manager (bundled with selenium-java since 4.6) downloads the ChromeDriver that matches
 * the installed Chrome, so no driver path is hard-coded.
 */
public final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);

    private DriverFactory() {
        // utility class – no instances
    }

    public static WebDriver createDriver() {
        String browser = Config.browser();
        if (!"chrome".equals(browser)) {
            throw new IllegalArgumentException("Unsupported browser '" + browser + "'. This project supports: chrome");
        }
        LOG.info("Starting Chrome (headless={})", Config.headless());
        return new ChromeDriver(chromeOptions());
    }

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (Config.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
                "--window-size=1366,900",   // fixed size -> the same (desktop) layout on every machine
                "--no-sandbox",             // required inside CI containers
                "--disable-dev-shm-usage",  // avoids crashes when /dev/shm is small on CI runners
                "--disable-gpu"
        );
        // No implicit wait is set on purpose: the framework uses explicit waits only
        // (the Selenium docs warn that mixing both causes unpredictable wait times).
        return options;
    }
}
