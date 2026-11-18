package com.amalitech.newsletter.testdata;

/**
 * Text the application is expected to display (taken from the QAM-lab1 design and README).
 * Keeping it in one place means a copy change in the app is fixed here once, not in every test.
 */
public final class ExpectedText {

    public static final String PAGE_TITLE = "Frontend Mentor | Newsletter sign-up form with success message";
    public static final String SIGNUP_HEADING = "Stay updated!";
    public static final int BENEFIT_COUNT = 3;
    public static final String EMAIL_PLACEHOLDER = "email@company.com";
    public static final String SUBSCRIBE_BUTTON = "Subscribe to monthly newsletter";

    public static final String ERROR_INVALID_EMAIL = "Valid email required";
    public static final String ERROR_EMAIL_REQUIRED = "Email address is required";

    public static final String SUCCESS_HEADING = "Thanks for subscribing!";
    public static final String SUCCESS_MESSAGE_START = "A confirmation email has been sent to";

    private ExpectedText() {
        // constants only
    }
}
