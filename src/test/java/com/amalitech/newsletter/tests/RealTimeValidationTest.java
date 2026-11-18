package com.amalitech.newsletter.tests;

import com.amalitech.newsletter.pages.NewsletterSignupPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.amalitech.newsletter.testdata.ExpectedText.ERROR_EMAIL_REQUIRED;
import static com.amalitech.newsletter.testdata.ExpectedText.ERROR_INVALID_EMAIL;
import static com.amalitech.newsletter.testdata.TestData.INCOMPLETE_EMAIL;
import static com.amalitech.newsletter.testdata.TestData.NOT_AN_EMAIL;
import static com.amalitech.newsletter.testdata.TestData.VALID_EMAIL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Feature F4 – real-time validation (state transitions of the email field). See docs/TEST_PLAN.md.
 * States: untouched -> (blur/submit) -> validated -> live feedback on every keystroke.
 */
@Tag("validation")
@DisplayName("F4 · Real-time validation")
class RealTimeValidationTest extends BaseTest {

    private NewsletterSignupPage signupPage;

    @BeforeEach
    void openSignupPage() {
        signupPage = new NewsletterSignupPage(driver).open();
    }

    @Test
    @DisplayName("TC-08 · no error is shown while typing the first attempt")
    void noErrorWhileTypingFirstAttempt() {
        signupPage.enterEmail(INCOMPLETE_EMAIL);

        assertFalse(signupPage.isErrorShown(), "error must not appear before the user leaves the field");
    }

    @Test
    @DisplayName("TC-09 · leaving the field with an invalid email shows the error without submitting")
    void leavingFieldShowsError() {
        signupPage.enterEmail(INCOMPLETE_EMAIL)
                .leaveEmailField()
                .waitForErrorState();

        assertEquals(ERROR_INVALID_EMAIL, signupPage.getErrorMessage());
    }

    @Test
    @DisplayName("TC-10 · after an error, the message clears as soon as the email becomes valid")
    void errorClearsWhenEmailCorrected() {
        signupPage.submitInvalidEmail(INCOMPLETE_EMAIL)
                .enterEmail(VALID_EMAIL)
                .waitForErrorCleared();

        assertAll("error cleared",
                () -> assertFalse(signupPage.isErrorShown(), "error text should be gone"),
                () -> assertFalse(signupPage.hasErrorStyling(), "red style should be gone")
        );
    }

    @Test
    @DisplayName("TC-11 · the message switches from 'required' to 'invalid' while typing")
    void messageUpdatesWhileTyping() {
        signupPage.submitInvalidEmail("");
        assertEquals(ERROR_EMAIL_REQUIRED, signupPage.getErrorMessage(), "after empty submit");

        signupPage.enterEmail(NOT_AN_EMAIL);
        assertEquals(ERROR_INVALID_EMAIL, signupPage.getErrorMessage(), "after typing non-email text");
    }
}
