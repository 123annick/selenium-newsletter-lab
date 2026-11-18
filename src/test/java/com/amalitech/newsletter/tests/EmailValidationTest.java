package com.amalitech.newsletter.tests;

import com.amalitech.newsletter.pages.NewsletterSignupPage;
import com.amalitech.newsletter.pages.SuccessPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.amalitech.newsletter.testdata.ExpectedText.ERROR_EMAIL_REQUIRED;
import static com.amalitech.newsletter.testdata.ExpectedText.ERROR_INVALID_EMAIL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Feature F3 – submitting an empty or invalid e-mail is blocked with the right message. See docs/TEST_PLAN.md. */
@Tag("validation")
@DisplayName("F3 · Email validation on submit")
class EmailValidationTest extends BaseTest {

    private NewsletterSignupPage signupPage;

    @BeforeEach
    void openSignupPage() {
        signupPage = new NewsletterSignupPage(driver).open();
    }

    /** Data-driven: each row of invalid-emails.csv is one equivalence class of invalid input. */
    @ParameterizedTest(name = "TC-06 · rejects \"{0}\" – {1}")
    @CsvFileSource(resources = "/testdata/invalid-emails.csv", numLinesToSkip = 1)
    @DisplayName("TC-06 · invalid email format shows 'Valid email required' and blocks submission")
    void invalidEmailIsRejected(String invalidEmail, String reason) {
        log.info("Checking invalid email '{}' ({})", invalidEmail, reason);

        signupPage.submitInvalidEmail(invalidEmail);

        assertAll("invalid email: " + reason,
                () -> assertEquals(ERROR_INVALID_EMAIL, signupPage.getErrorMessage()),
                () -> assertTrue(signupPage.hasErrorStyling(), "input should get the red 'is-invalid' style"),
                () -> assertTrue(signupPage.isEmailMarkedInvalid(), "input should have aria-invalid=true"),
                () -> assertTrue(signupPage.isEmailFieldFocused(), "focus should return to the email field"),
                () -> assertFalse(new SuccessPage(driver).isDisplayed(), "success message must not appear")
        );
    }

    @ParameterizedTest(name = "TC-07 · empty input \"{0}\"")
    @ValueSource(strings = {"", "   "})
    @DisplayName("TC-07 · empty or blank email shows 'Email address is required'")
    void emptyEmailIsRejected(String blankEmail) {
        signupPage.submitInvalidEmail(blankEmail);

        assertAll("required error",
                () -> assertEquals(ERROR_EMAIL_REQUIRED, signupPage.getErrorMessage()),
                () -> assertTrue(signupPage.isEmailMarkedInvalid(), "input should have aria-invalid=true"),
                () -> assertFalse(new SuccessPage(driver).isDisplayed(), "success message must not appear")
        );
    }
}
