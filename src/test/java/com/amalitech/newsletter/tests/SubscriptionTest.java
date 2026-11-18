package com.amalitech.newsletter.tests;

import com.amalitech.newsletter.pages.NewsletterSignupPage;
import com.amalitech.newsletter.pages.SuccessPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.amalitech.newsletter.testdata.ExpectedText.SUCCESS_HEADING;
import static com.amalitech.newsletter.testdata.ExpectedText.SUCCESS_MESSAGE_START;
import static com.amalitech.newsletter.testdata.TestData.VALID_EMAIL;
import static com.amalitech.newsletter.testdata.TestData.VALID_EMAIL_WITH_SURROUNDING_SPACES;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Feature F2 – subscribing with a valid e-mail and dismissing the success message. See docs/TEST_PLAN.md. */
@DisplayName("F2 · Successful subscription")
class SubscriptionTest extends BaseTest {

    private NewsletterSignupPage signupPage;

    @BeforeEach
    void openSignupPage() {
        signupPage = new NewsletterSignupPage(driver).open();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-02 · valid email shows the success message with that email")
    void validEmailShowsSuccessMessage() {
        SuccessPage successPage = signupPage.subscribeWith(VALID_EMAIL);

        assertAll("success message",
                () -> assertTrue(successPage.isDisplayed(), "success message should be visible"),
                () -> assertFalse(signupPage.isDisplayed(), "sign-up form should be hidden"),
                () -> assertEquals(SUCCESS_HEADING, successPage.getHeading()),
                () -> assertEquals(VALID_EMAIL, successPage.getConfirmedEmail()),
                () -> assertTrue(successPage.getMessage().startsWith(SUCCESS_MESSAGE_START)),
                () -> assertTrue(successPage.isHeadingFocused(), "focus should move to the success heading")
        );
    }

    @Test
    @DisplayName("TC-03 · spaces around the email are trimmed in the success message")
    void emailIsTrimmed() {
        SuccessPage successPage = signupPage.subscribeWith(VALID_EMAIL_WITH_SURROUNDING_SPACES);

        assertEquals(VALID_EMAIL, successPage.getConfirmedEmail());
    }

    @Test
    @DisplayName("TC-04 · pressing ENTER submits the form (keyboard-only user)")
    void enterKeySubmitsForm() {
        SuccessPage successPage = signupPage.subscribeWithEnterKey(VALID_EMAIL);

        assertEquals(VALID_EMAIL, successPage.getConfirmedEmail());
    }

    @Test
    @DisplayName("TC-05 · 'Dismiss message' returns to an empty form with focus on the email field")
    void dismissReturnsToEmptyForm() {
        SuccessPage successPage = signupPage.subscribeWith(VALID_EMAIL);
        NewsletterSignupPage formAgain = successPage.dismiss();

        assertAll("form after dismiss",
                () -> assertTrue(formAgain.isDisplayed(), "sign-up form should be visible again"),
                () -> assertFalse(successPage.isDisplayed(), "success message should be hidden"),
                () -> assertEquals("", formAgain.getEmailValue(), "email field should be cleared"),
                () -> assertFalse(formAgain.isErrorShown(), "no error should be shown"),
                () -> assertTrue(formAgain.isEmailFieldFocused(), "focus should return to the email field")
        );
    }
}
