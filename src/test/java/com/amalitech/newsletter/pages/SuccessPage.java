package com.amalitech.newsletter.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object for the "Thanks for subscribing!" success message.
 */
public class SuccessPage extends BasePage {

    @FindBy(id = "success")
    private WebElement successCard;

    @FindBy(id = "success-title")
    private WebElement heading;

    @FindBy(css = "#success .success__body p")
    private WebElement message;

    @FindBy(id = "success-email")
    private WebElement confirmedEmail;

    @FindBy(xpath = "//button[normalize-space()='Dismiss message']")
    private WebElement dismissButton;

    public SuccessPage(WebDriver driver) {
        super(driver);
    }

    /** Explicitly wait for the success card to become visible before using it. */
    public SuccessPage waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOf(successCard));
        log.info("Success message displayed");
        return this;
    }

    public boolean isDisplayed() {
        return isVisible(successCard);
    }

    public String getHeading() {
        return textOf(heading);
    }

    public String getMessage() {
        return textOf(message);
    }

    public String getConfirmedEmail() {
        return textOf(confirmedEmail);
    }

    /** The app moves focus to the heading so screen readers announce it. */
    public boolean isHeadingFocused() {
        return heading.equals(driver.switchTo().activeElement());
    }

    /** Clicking "Dismiss message" takes the user back to the sign-up form. */
    public NewsletterSignupPage dismiss() {
        log.info("Clicking 'Dismiss message'");
        click(dismissButton);
        return new NewsletterSignupPage(driver).waitUntilLoaded();
    }
}
