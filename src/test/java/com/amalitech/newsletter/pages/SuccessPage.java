package com.amalitech.newsletter.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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

    public SuccessPage waitUntilLoaded() {
        waitUntilVisible(successCard);
        log.info("Success message displayed");
        return this;
    }

    // ---------------------------------------------------------------- user actions

    /** "Dismiss message" returns the user to the sign-up form. */
    public NewsletterSignupPage dismiss() {
        log.info("Clicking 'Dismiss message'");
        click(dismissButton);
        return new NewsletterSignupPage(driver).waitUntilLoaded();
    }

    // ---------------------------------------------------------------- page state

    public boolean isDisplayed() {
        return isVisible(successCard);
    }

    public String getHeading() {
        return visibleText(heading);
    }

    public String getMessage() {
        return visibleText(message);
    }

    public String getConfirmedEmail() {
        return visibleText(confirmedEmail);
    }

    public boolean isHeadingFocused() {
        return hasFocus(heading);
    }
}
