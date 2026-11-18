package com.amalitech.newsletter.tests;

import com.amalitech.newsletter.pages.NewsletterSignupPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.amalitech.newsletter.testdata.ExpectedText.BENEFIT_COUNT;
import static com.amalitech.newsletter.testdata.ExpectedText.EMAIL_PLACEHOLDER;
import static com.amalitech.newsletter.testdata.ExpectedText.PAGE_TITLE;
import static com.amalitech.newsletter.testdata.ExpectedText.SIGNUP_HEADING;
import static com.amalitech.newsletter.testdata.ExpectedText.SUBSCRIBE_BUTTON;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Feature F1 – the sign-up form is displayed correctly. See docs/TEST_PLAN.md. */
@DisplayName("F1 · Sign-up page load")
class SignupPageLoadTest extends BaseTest {

    private NewsletterSignupPage signupPage;

    @BeforeEach
    void openSignupPage() {
        signupPage = new NewsletterSignupPage(driver).open();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-01 · form shows title, heading, 3 benefits, email field and button with no error")
    void signupFormIsDisplayedCorrectly() {
        assertAll("sign-up form content",
                () -> assertEquals(PAGE_TITLE, signupPage.getTitle()),
                () -> assertEquals(SIGNUP_HEADING, signupPage.getHeading()),
                () -> assertEquals(BENEFIT_COUNT, signupPage.getBenefitCount()),
                () -> assertTrue(signupPage.isEmailInputDisplayed(), "email input should be visible"),
                () -> assertEquals(EMAIL_PLACEHOLDER, signupPage.getEmailPlaceholder()),
                () -> assertEquals(SUBSCRIBE_BUTTON, signupPage.getSubscribeButtonText()),
                () -> assertFalse(signupPage.isErrorShown(), "no error should be shown on load"),
                () -> assertFalse(signupPage.isEmailMarkedInvalid(), "field should start as valid")
        );
    }
}
