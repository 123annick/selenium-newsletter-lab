package com.amalitech.newsletter.tests;

import com.amalitech.newsletter.pages.NewsletterSignupPage;
import com.amalitech.newsletter.pages.SuccessPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UI tests for the QAM-lab1 newsletter sign-up form (https://github.com/123annick/QAM-lab1).
 * The scenarios follow the features listed in that project's README.
 */
@DisplayName("Newsletter sign-up form")
class NewsletterSignupTest extends BaseTest {

    private static final String VALID_EMAIL = "ash@loremcompany.com";
    private static final String INVALID_MSG = "Valid email required";
    private static final String REQUIRED_MSG = "Email address is required";

    private NewsletterSignupPage signupPage;

    @BeforeEach
    void openSignupPage() {
        // Runs after BaseTest.setUp(), so the driver already exists
        signupPage = new NewsletterSignupPage(driver).open();
    }

    @Nested
    @DisplayName("When the page loads")
    class PageLoad {

        @Test
        @Tag("smoke")
        @DisplayName("shows the sign-up form with heading, benefits, email field and button")
        void showsSignupForm() {
            assertAll("sign-up form content",
                    () -> assertEquals("Frontend Mentor | Newsletter sign-up form with success message", signupPage.getTitle()),
                    () -> assertEquals("Stay updated!", signupPage.getHeading()),
                    () -> assertEquals(3, signupPage.getBenefitCount()),
                    () -> assertTrue(signupPage.isEmailInputDisplayed(), "email input should be visible"),
                    () -> assertEquals("email@company.com", signupPage.getEmailPlaceholder()),
                    () -> assertEquals("Subscribe to monthly newsletter", signupPage.getSubscribeButtonText()),
                    () -> assertFalse(signupPage.isErrorShown(), "no error should be shown initially"),
                    () -> assertFalse(signupPage.isEmailMarkedInvalid(), "field should start as valid")
            );
        }
    }

    @Nested
    @DisplayName("When a valid email is submitted")
    class ValidEmail {

        @Test
        @Tag("smoke")
        @DisplayName("shows the success message containing the submitted email")
        void showsSuccessMessage() {
            SuccessPage successPage = signupPage.subscribeWith(VALID_EMAIL);

            assertAll("success message",
                    () -> assertTrue(successPage.isDisplayed(), "success card should be visible"),
                    () -> assertFalse(signupPage.isDisplayed(), "sign-up form should be hidden"),
                    () -> assertEquals("Thanks for subscribing!", successPage.getHeading()),
                    () -> assertEquals(VALID_EMAIL, successPage.getConfirmedEmail()),
                    () -> assertTrue(successPage.getMessage().startsWith("A confirmation email has been sent to")),
                    () -> assertTrue(successPage.isHeadingFocused(), "focus should move to the success heading")
            );
        }

        @Test
        @DisplayName("trims spaces around the email before showing it")
        void trimsEmail() {
            SuccessPage successPage = signupPage.subscribeWith("   " + VALID_EMAIL + "   ");

            assertEquals(VALID_EMAIL, successPage.getConfirmedEmail());
        }

        @Test
        @DisplayName("'Dismiss message' returns to an empty sign-up form")
        void dismissReturnsToForm() {
            NewsletterSignupPage formAgain = signupPage.subscribeWith(VALID_EMAIL).dismiss();

            assertAll("form after dismiss",
                    () -> assertTrue(formAgain.isDisplayed(), "sign-up form should be visible again"),
                    () -> assertFalse(new SuccessPage(driver).isDisplayed(), "success card should be hidden"),
                    () -> assertEquals("", formAgain.getEmailValue(), "email field should be cleared"),
                    () -> assertFalse(formAgain.isErrorShown(), "no error should be shown"),
                    () -> assertTrue(formAgain.isEmailFieldFocused(), "focus should return to the email field")
            );
        }
    }

    @Nested
    @DisplayName("When an invalid email is submitted")
    class InvalidEmail {

        @ParameterizedTest(name = "rejects \"{0}\"")
        @ValueSource(strings = {"ash#loremcompany.com", "plainaddress", "user@", "user@domain", "@nouser.com", "user name@mail.com"})
        @DisplayName("shows 'Valid email required' and stays on the form")
        void showsInvalidEmailError(String invalidEmail) {
            signupPage.submitInvalidEmail(invalidEmail);

            assertAll("validation error",
                    () -> assertEquals(INVALID_MSG, signupPage.getErrorMessage()),
                    () -> assertTrue(signupPage.hasErrorStyling(), "input should get the red 'is-invalid' style"),
                    () -> assertTrue(signupPage.isEmailMarkedInvalid(), "input should have aria-invalid=true"),
                    () -> assertTrue(signupPage.isEmailFieldFocused(), "focus should go back to the email field"),
                    () -> assertFalse(new SuccessPage(driver).isDisplayed(), "success message must NOT appear")
            );
        }

        @ParameterizedTest(name = "empty input \"{0}\"")
        @ValueSource(strings = {"", "   "})
        @DisplayName("an empty email shows 'Email address is required'")
        void emptyEmailShowsRequiredError(String emptyEmail) {
            signupPage.submitInvalidEmail(emptyEmail);

            assertAll("required error",
                    () -> assertEquals(REQUIRED_MSG, signupPage.getErrorMessage()),
                    () -> assertTrue(signupPage.isEmailMarkedInvalid()),
                    () -> assertFalse(new SuccessPage(driver).isDisplayed())
            );
        }
    }

    @Nested
    @DisplayName("Real-time validation")
    class RealTimeValidation {

        @Test
        @DisplayName("no error appears while typing the first attempt")
        void noErrorWhileFirstTyping() {
            signupPage.enterEmail("ash@lorem");

            assertFalse(signupPage.isErrorShown(), "error must not appear before the user leaves the field");
        }

        @Test
        @DisplayName("leaving the field with an invalid email shows the error without submitting")
        void blurShowsError() {
            signupPage.enterEmail("ash@lorem").leaveEmailField().waitForErrorState();

            assertEquals(INVALID_MSG, signupPage.getErrorMessage());
        }

        @Test
        @DisplayName("after an error, the message clears as soon as the email becomes valid")
        void errorClearsWhenCorrected() {
            signupPage.submitInvalidEmail("ash@lorem")
                    .enterEmail(VALID_EMAIL)
                    .waitForErrorCleared();

            assertAll("error cleared",
                    () -> assertFalse(signupPage.isErrorShown()),
                    () -> assertFalse(signupPage.hasErrorStyling())
            );
        }

        @Test
        @DisplayName("the message switches from 'required' to 'invalid' while typing")
        void messageUpdatesLive() {
            signupPage.submitInvalidEmail("");
            assertEquals(REQUIRED_MSG, signupPage.getErrorMessage());

            signupPage.enterEmail("ash");
            assertEquals(INVALID_MSG, signupPage.getErrorMessage());
        }
    }
}
