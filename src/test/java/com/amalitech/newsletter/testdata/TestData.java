package com.amalitech.newsletter.testdata;

/**
 * Input data used by the tests, kept separate from test logic.
 * Larger data sets (e.g. invalid e-mails) live in CSV files under src/test/resources/testdata.
 */
public final class TestData {

    public static final String VALID_EMAIL = "ash@loremcompany.com";
    public static final String VALID_EMAIL_WITH_SURROUNDING_SPACES = "   " + VALID_EMAIL + "   ";
    /** Looks like the start of an e-mail but is not valid yet (no top-level domain). */
    public static final String INCOMPLETE_EMAIL = "ash@lorem";
    /** Text that is not empty but is not an e-mail either. */
    public static final String NOT_AN_EMAIL = "ash";

    private TestData() {
        // constants only
    }
}
