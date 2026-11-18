package com.amalitech.newsletter.pages;

import com.amalitech.newsletter.config.Config;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Page Object for the newsletter sign-up form (QAM-lab1).
 * The only place that knows this page's locators. Different locator strategies are used
 * where each fits best: id (most stable), CSS (structure), XPath (attribute match).
 */
public class NewsletterSignupPage extends BasePage {

    private static final String ARIA_INVALID = "aria-invalid";
    private static final String ERROR_CSS_CLASS = "is-invalid";

    @FindBy(id = "signup")
    private WebElement signupCard;

    @FindBy(xpath = "//h1[@id='signup-title']")
    private WebElement heading;

    @FindBy(css = "ul.benefits li")
    private List<WebElement> benefits;

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "subscribe-btn")
    private WebElement subscribeButton;

    @FindBy(id = "email-error")
    private WebElement emailError;

    public NewsletterSignupPage(WebDriver driver) {
        super(driver);
    }

    // ---------------------------------------------------------------- navigation

    public NewsletterSignupPage open() {
        log.info("Opening {}", Config.baseUrl());
        driver.get(Config.baseUrl());
        return waitUntilLoaded();
    }

    public NewsletterSignupPage waitUntilLoaded() {
        waitUntilVisible(signupCard);
        return this;
    }

    // ---------------------------------------------------------------- user actions

    public NewsletterSignupPage enterEmail(String email) {
        log.info("Typing email '{}'", email);
        type(emailInput, email);
        return this;
    }

    /** Presses TAB to move focus out of the field; the app validates on blur. */
    public NewsletterSignupPage leaveEmailField() {
        log.info("Pressing TAB to leave the email field");
        emailInput.sendKeys(Keys.TAB);
        return this;
    }

    public void clickSubscribe() {
        log.info("Clicking '{}'", subscribeButton.getText());
        click(subscribeButton);
    }

    public void pressEnterInEmailField() {
        log.info("Pressing ENTER in the email field");
        emailInput.sendKeys(Keys.ENTER);
    }

    /** Happy path: valid e-mail + click Subscribe, then wait for the success message. */
    public SuccessPage subscribeWith(String email) {
        enterEmail(email);
        clickSubscribe();
        return new SuccessPage(driver).waitUntilLoaded();
    }

    /** Keyboard-only path: valid e-mail + ENTER, then wait for the success message. */
    public SuccessPage subscribeWithEnterKey(String email) {
        enterEmail(email);
        pressEnterInEmailField();
        return new SuccessPage(driver).waitUntilLoaded();
    }

    /** Negative path: submit, then wait until the field is flagged invalid. */
    public NewsletterSignupPage submitInvalidEmail(String email) {
        enterEmail(email);
        clickSubscribe();
        return waitForErrorState();
    }

    // ---------------------------------------------------------------- explicit waits for state changes

    public NewsletterSignupPage waitForErrorState() {
        waitForAttribute(emailInput, ARIA_INVALID, "true");
        return this;
    }

    public NewsletterSignupPage waitForErrorCleared() {
        waitForAttribute(emailInput, ARIA_INVALID, "false");
        return this;
    }

    // ---------------------------------------------------------------- page state (read by assertions)

    public boolean isDisplayed() {
        return isVisible(signupCard);
    }

    public String getHeading() {
        return visibleText(heading);
    }

    public int getBenefitCount() {
        return benefits.size();
    }

    public boolean isEmailInputDisplayed() {
        return isVisible(emailInput);
    }

    public String getEmailPlaceholder() {
        return emailInput.getDomAttribute("placeholder");
    }

    public String getEmailValue() {
        return emailInput.getDomProperty("value");
    }

    public String getSubscribeButtonText() {
        return visibleText(subscribeButton);
    }

    /** The app writes the message into an always-present span, so "shown" means "has text". */
    public String getErrorMessage() {
        return emailError.getText().trim();
    }

    public boolean isErrorShown() {
        return !getErrorMessage().isEmpty();
    }

    public boolean isEmailMarkedInvalid() {
        return "true".equals(emailInput.getDomAttribute(ARIA_INVALID));
    }

    public boolean hasErrorStyling() {
        String classes = emailInput.getDomAttribute("class");
        return classes != null && classes.contains(ERROR_CSS_CLASS);
    }

    public boolean isEmailFieldFocused() {
        return hasFocus(emailInput);
    }
}
