package com.amalitech.newsletter.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates and configures WebDriver instances.
 *
 * Since Selenium 4.6, Selenium Manager (bundled with selenium-java) automatically finds/downloads
 * the ChromeDriver that matches the installed Chrome, so no manual driver path is required.
 * If you still want to use a specific driver binary, pass -Dwebdriver.chrome.driver=/path/to/chromedriver.
 */
public final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        String browser = Config.browser();
        if (!"chrome".equals(browser)) {
            throw new IllegalArgumentException("Unsupported browser: " + browser + " (this lab uses Chrome)");
        }

        ChromeOptions options = new ChromeOptions();
        if (Config.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
                "--window-size=1366,900",
                "--no-sandbox",             // needed inside CI containers
                "--disable-dev-shm-usage",  // avoids crashes when /dev/shm is small (CI runners)
                "--disable-gpu",
                "--remote-allow-origins=*"
        );

        LOG.info("Starting Chrome (headless={})", Config.headless());
        WebDriver driver = new ChromeDriver(options);
        // We rely ONLY on explicit waits (WebDriverWait) – the Selenium docs warn against mixing implicit + explicit waits.
        return driver;
    }
}
