package com.amalitech.newsletter.pages;

import com.amalitech.newsletter.config.Config;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object for the sign-up form of the QAM-lab1 newsletter app.
 * All locators for this page live here – tests never call driver.findElement() directly.
 * Several locator strategies are used on purpose: id, CSS, XPath.
 */
public class NewsletterSignupPage extends BasePage {

    @FindBy(id = "signup")
    private WebElement signupCard;

    @FindBy(xpath = "//h1[@id='signup-title']")
    private WebElement heading;

    @FindBy(css = "ul.benefits li")
    private List<WebElement> benefits;

    @FindBy(id = "email")
    private WebElement emailInput;

    // The button has no id, so we locate it by its position inside the form
    @FindBy(id = "subscribe-btn")
    private WebElement subscribeButton;

    @FindBy(id = "email-error")
    private WebElement emailError;

    public NewsletterSignupPage(WebDriver driver) {
        super(driver);
    }

    /** Navigates to the app and waits until the form is visible. */
    public NewsletterSignupPage open() {
        log.info("Opening {}", Config.baseUrl());
        driver.get(Config.baseUrl());
        return waitUntilLoaded();
    }

    public NewsletterSignupPage waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOf(signupCard));
        return this;
    }

    // ---------- actions ----------

    public NewsletterSignupPage enterEmail(String email) {
        log.info("Typing email: '{}'", email);
        type(emailInput, email);
        return this;
    }

    /** Presses TAB to leave the field – the app validates on "blur". */
    public NewsletterSignupPage leaveEmailField() {
        log.info("Pressing TAB to leave the email field");
        emailInput.sendKeys(Keys.TAB);
        return this;
    }

    public void clickSubscribe() {
        log.info("Clicking '{}'", subscribeButton.getText());
        click(subscribeButton);
    }

    /** Happy path: valid email -> success message. */
    public SuccessPage subscribeWith(String email) {
        enterEmail(email);
        clickSubscribe();
        return new SuccessPage(driver).waitUntilLoaded();
    }

    /** Negative path: invalid email -> stay on the form; wait until the field is flagged invalid. */
    public NewsletterSignupPage submitInvalidEmail(String email) {
        enterEmail(email);
        clickSubscribe();
        return waitForErrorState();
    }

    public NewsletterSignupPage waitForErrorState() {
        wait.until(ExpectedConditions.attributeToBe(emailInput, "aria-invalid", "true"));
        return this;
    }

    public NewsletterSignupPage waitForErrorCleared() {
        wait.until(ExpectedConditions.attributeToBe(emailInput, "aria-invalid", "false"));
        return this;
    }

    // ---------- state readers used by the assertions ----------

    public boolean isDisplayed() {
        return isVisible(signupCard);
    }

    public String getHeading() {
        return textOf(heading);
    }

    public int getBenefitCount() {
        return benefits.size();
    }

    public String getSubscribeButtonText() {
        return textOf(subscribeButton);
    }

    public boolean isEmailInputDisplayed() {
        return isVisible(emailInput);
    }

    public String getEmailValue() {
        return emailInput.getDomProperty("value");
    }

    public String getEmailPlaceholder() {
        return emailInput.getDomAttribute("placeholder");
    }

    /** The app injects the error text into an always-present span, so "shown" = "has text". */
    public String getErrorMessage() {
        return emailError.getText().trim();
    }

    public boolean isErrorShown() {
        return !getErrorMessage().isEmpty();
    }

    public boolean isEmailMarkedInvalid() {
        return "true".equals(emailInput.getDomAttribute("aria-invalid"));
    }

    /** The red error styling is applied with the CSS class "is-invalid". */
    public boolean hasErrorStyling() {
        String classes = emailInput.getDomAttribute("class");
        return classes != null && classes.contains("is-invalid");
    }

    /** True if keyboard focus is on the email field (the app refocuses it after a failed submit). */
    public boolean isEmailFieldFocused() {
        return emailInput.equals(driver.switchTo().activeElement());
    }
}
