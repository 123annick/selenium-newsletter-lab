package com.amalitech.newsletter.pages;

import com.amalitech.newsletter.config.Config;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent of every Page Object.
 * <ul>
 *   <li>Initialises {@code @FindBy} fields with {@link PageFactory} (Page Factory pattern).</li>
 *   <li>Wraps low-level Selenium calls with explicit waits, so page classes and tests never sleep.</li>
 * </ul>
 * Page Objects expose user actions and page state; they contain NO assertions – checking is the tests' job.
 */
public abstract class BasePage {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    /**
     * PageFactory must receive "this" to fill the subclass's @FindBy fields with lazy proxies.
     * This is safe: no locator is used until a page method is called.
     */
    @SuppressWarnings("this-escape")
    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Config.explicitWait());
        PageFactory.initElements(driver, this);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    protected void click(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    /** Clears the field, then types the text (an empty string just clears it). */
    protected void type(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element)).clear();
        if (!text.isEmpty()) {
            element.sendKeys(text);
        }
    }

    protected String visibleText(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element)).getText().trim();
    }

    protected void waitUntilVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected void waitForAttribute(WebElement element, String attribute, String value) {
        wait.until(ExpectedConditions.attributeToBe(element, attribute, value));
    }

    /** Returns false instead of throwing when the element is absent or was re-rendered. */
    protected boolean isVisible(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /** True if keyboard focus is currently on the given element. */
    protected boolean hasFocus(WebElement element) {
        return element.equals(driver.switchTo().activeElement());
    }
}
