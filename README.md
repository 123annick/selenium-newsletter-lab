# Test Automation with Selenium WebDriver – Newsletter Sign-up

![CI](https://github.com/123annick/selenium-newsletter-lab/actions/workflows/ci.yml/badge.svg)

Automated UI tests for my **Newsletter sign-up form with success message** from [QAM-lab1](https://github.com/123annick/QAM-lab1) ([live site](https://123annick.github.io/QAM-lab1/)), built with **Java + Selenium WebDriver 4 + JUnit 5 + Maven**, structured with the **Page Object Model / Page Factory**, and run on every push by **GitHub Actions** with **Slack** notifications.

📋 **Test plan:** [`docs/TEST_PLAN.md`](docs/TEST_PLAN.md) – scope, approach, 11 test cases, traceability, risks.

| Grading metric | Where it is covered |
|---|---|
| Setup & configuration (15) | [Step 1](#step-1--setup-and-configuration) – `pom.xml`, `config.properties`, `Config`, `DriverFactory` |
| Basic automated test with JUnit 5 (35) | [Step 3](#step-3--tests-with-junit-5) – 4 test classes, 11 test cases, 17 executions |
| Page Object Model (20) | [Step 4](#step-4--page-object-model--page-factory) – `pages/` package |
| GitHub Actions (30) | [Step 5](#step-5--ci-pipeline-github-actions) – `.github/workflows/ci.yml` |

---

## Project structure – one responsibility per package

```
selenium-newsletter-lab/
├── app/                              Application under test (copied from QAM-lab1)
├── docs/TEST_PLAN.md                 Test plan
├── pom.xml                           Dependencies and build/test plugins
├── .github/workflows/ci.yml          CI pipeline + Slack notification
└── src/test/
    ├── java/com/amalitech/newsletter/
    │   ├── config/                   HOW to run   – settings and browser creation
    │   │   ├── Config.java
    │   │   └── DriverFactory.java
    │   ├── pages/                    WHERE things are – locators + user actions (no assertions)
    │   │   ├── BasePage.java
    │   │   ├── NewsletterSignupPage.java
    │   │   └── SuccessPage.java
    │   ├── testdata/                 WITH WHAT    – inputs and expected texts
    │   │   ├── TestData.java
    │   │   └── ExpectedText.java
    │   ├── tests/                    WHAT is checked – one class per feature (assertions only here)
    │   │   ├── BaseTest.java              browser lifecycle
    │   │   ├── SignupPageLoadTest.java    F1 · TC-01
    │   │   ├── SubscriptionTest.java      F2 · TC-02 – TC-05
    │   │   ├── EmailValidationTest.java   F3 · TC-06 – TC-07
    │   │   └── RealTimeValidationTest.java F4 · TC-08 – TC-11
    │   └── utils/
    │       └── ScreenshotOnFailureExtension.java  evidence when a test fails
    └── resources/
        ├── config.properties         default settings
        ├── testdata/invalid-emails.csv  data-driven invalid inputs
        ├── simplelogger.properties   log format
        └── junit-platform.properties JUnit settings
```

---

## Step 1 – Setup and configuration

**Prerequisites:** JDK 21 or newer, Maven 3.9+, Google Chrome, Git. ChromeDriver is **not** installed manually – *Selenium Manager* (inside `selenium-java` since 4.6) downloads the driver that matches the installed Chrome.

**`pom.xml`**

| Entry | Why |
|---|---|
| `selenium-java` | WebDriver API, ChromeDriver, Page Factory, waits, Selenium Manager |
| `junit-bom` + `junit-jupiter` + `junit-jupiter-params` | JUnit 5 annotations, assertions, parameterized tests – the BOM keeps all JUnit modules on one version |
| `slf4j-simple` | Step-by-step logs in the console and CI |
| `maven-compiler-plugin` (`release 21`, `-Xlint:all`) | Same bytecode level locally and on CI; compiler warnings visible |
| `maven-surefire-plugin` + JUnit 5 tree reporter | Runs the tests; prints each test name as a ✔/✘ tree; full stack traces |
| `<scope>test</scope>` | Libraries are only needed for tests |

**Configuration is kept out of code** – `src/test/resources/config.properties`:

```properties
baseUrl=                 # empty = local app/index.html
browser=chrome
headless=true
explicitWaitSeconds=10
```

Any value can be overridden on the command line (`mvn test -Dheadless=false`). `Config` reads the file, applies overrides, and forces headless on CI. `DriverFactory` is the only class that knows how Chrome is started.

---

## Step 2 – Application under test

The QAM-lab1 app is copied into `app/` so every pipeline run tests **the exact code in the repository**, independent of the live site. Behaviour checked (from its README): content of the form, success message with the email, two error messages ("Email address is required" / "Valid email required"), real-time validation, focus handling, Dismiss.

For testability I added `id="subscribe-btn"` to the Subscribe button – the only element without an id. The Page Object switched from a structural CSS locator to `By.id` with **one line changed** and no test touched.

| Form | Error state | Success state |
|---|---|---|
| ![form](docs/form-state.png) | ![error](docs/error-state.png) | ![success](docs/success-state.png) |

---

## Step 3 – Tests with JUnit 5

Four test classes, one per feature from the test plan. Every test name starts with its test-case ID, so a red test in CI points straight to the row in [`TEST_PLAN.md`](docs/TEST_PLAN.md#6-test-cases).

| Class | Test cases |
|---|---|
| `SignupPageLoadTest` | TC-01 form displayed correctly `@Tag("smoke")` |
| `SubscriptionTest` | TC-02 success message `@Tag("smoke")` · TC-03 email trimmed · TC-04 ENTER submits · TC-05 Dismiss |
| `EmailValidationTest` | TC-06 6 invalid formats from CSV · TC-07 empty / blank |
| `RealTimeValidationTest` | TC-08 no early error · TC-09 error on blur · TC-10 error clears · TC-11 message switches |

**JUnit 5 features used**

| Feature | Purpose |
|---|---|
| `@Test`, `@DisplayName` | test + readable name with TC-ID in reports and logs |
| `@BeforeEach` / `@AfterEach` | fresh browser per test, always closed |
| `@ParameterizedTest` + `@CsvFileSource` | data-driven: each CSV row (email + reason) is one invalid equivalence class |
| `@ParameterizedTest` + `@ValueSource` | boundary inputs `""` and `"   "` |
| `@Tag("smoke")`, `@Tag("validation")` | run subsets: `mvn test -Dgroups=smoke` |
| `@ExtendWith` | screenshot-on-failure extension |
| `assertAll` | all checks of one scenario reported together |

**Test structure – Arrange / Act / Assert:** the page is opened in `@BeforeEach` (arrange), the test performs one user action through the Page Object (act), then asserts on page state (assert). Assertions live only in test classes.

---

## Step 4 – Page Object Model + Page Factory

```
BasePage   PageFactory.initElements + click/type/visibleText/waitFor… helpers (explicit waits)
 ├── NewsletterSignupPage   open, enterEmail, leaveEmailField, clickSubscribe, subscribeWith → SuccessPage,
 │                          subscribeWithEnterKey → SuccessPage, submitInvalidEmail, getErrorMessage, …
 └── SuccessPage            waitUntilLoaded, getHeading, getConfirmedEmail, isHeadingFocused, dismiss → NewsletterSignupPage
```

- **Page Factory:** `@FindBy` fields become lazy proxies created by `PageFactory.initElements`, so elements are looked up when used.
- **Locator strategy:** `id` first (fastest, most stable), CSS for structure (`ul.benefits li`), XPath for attribute/text match (`//button[normalize-space()='Dismiss message']`).
- **Fluent navigation:** actions that change screen return the next page object: `signupPage.subscribeWith(email).dismiss()`.
- **No assertions in pages** – pages report state (`isErrorShown()`, `getConfirmedEmail()`); tests decide pass/fail.

---

## Step 5 – CI pipeline (GitHub Actions)

`.github/workflows/ci.yml`

| Requirement | Implementation |
|---|---|
| Trigger on push / PR | `on: push`, `pull_request`, plus `workflow_dispatch` with optional `base_url` input |
| Install dependencies | `setup-java` (JDK 21, Maven cache) → `setup-chrome` → `mvn test-compile` |
| Run tests | `mvn -B -ntp -e test -Dheadless=true` |
| Detailed logs | tool versions; ✔/✘ tree with TC-IDs and durations; step logs; full stack traces; per-test table in the job summary |
| Artifacts | Surefire reports (always); failure screenshots (on failure) |
| Notifications | `notify` job (`needs: test`, `if: always()`) posts ✅ PASSED / ❌ FAILED to Slack with repo, branch, commit, author and run link |

Slack setup: create a Slack app with an *Incoming Webhook*, then store its URL as the repository secret `SLACK_WEBHOOK_URL` (Settings → Secrets and variables → Actions). Without the secret the job only logs a notice.

---

## Design decisions and justification

| Decision | Why | Alternative rejected |
|---|---|---|
| **Separation of concerns** – config / pages / test data / tests / utils packages | Each class has one reason to change: a new locator → page; new copy → `ExpectedText`; new input → CSV; new setting → `config.properties` | Everything in test classes – hard to read and maintain |
| **Page Object Model + Page Factory** | Locators in one place; tests describe behaviour, not HTML. Proven when the button got an id: 1 line changed, 0 tests changed | Raw `driver.findElement` in tests |
| **Explicit waits only** | Wait for the exact condition (visible, attribute value); Selenium docs advise not mixing implicit and explicit waits | `Thread.sleep` – slow and flaky; implicit wait – unpredictable when mixed |
| **New browser per test** | Independent tests, no shared state, any order, easy to debug | One browser for all tests – faster but failures cascade |
| **Test data outside test logic** (`TestData`, `ExpectedText`, CSV) | Change data without touching code; CSV lets non-developers add cases | Hard-coded strings in each test |
| **One test class per feature + TC-IDs** | Mirrors the test plan; failures are traceable to requirements | One big test class |
| **`assertAll`** | Reports every broken check of a scenario in one run | Stop at the first failed assertion |
| **No assertions in Page Objects** | Pages stay reusable for positive and negative tests | Pages that assert – cannot reuse for negative paths |
| **Testing the local copy of the app in CI** | Reproducible; tests the code being pushed | Always testing the live site – depends on deployment/network |
| **Headless on CI, configurable locally** | CI runners have no screen; locally you can watch | Hard-coded mode |
| **Screenshot + reports as artifacts** | Evidence for every failure | Only a red ✘ in the log |
| **Webhook as GitHub secret** | Never committed to code | URL in `ci.yml` – anyone could post to the channel |
| **Selenium Manager** | Driver always matches Chrome; no binaries in the repo | Manual ChromeDriver download – breaks on Chrome updates |

**Coding conventions applied:** descriptive names (`invalidEmailIsRejected`, `waitForErrorCleared`), small single-purpose methods, constants instead of magic strings, `final` utility classes with private constructors, Javadoc on every class, no duplicated waiting logic (`BasePage` helpers), no unused code, consistent Arrange / Act / Assert.

---

## Run it

```bash
mvn test                         # all 17 executions, headless
mvn test -Dheadless=false        # watch Chrome
mvn test -Dgroups=smoke          # TC-01, TC-02 only
mvn test -Dtest=EmailValidationTest
mvn test -DbaseUrl=https://123annick.github.io/QAM-lab1/   # test the live site
```

Reports: `target/surefire-reports/` · Failure screenshots: `target/screenshots/`

Expected output (abridged):
```
[INFO] F1 · Sign-up page load
[INFO] └─ ✔ TC-01 · form shows title, heading, 3 benefits, email field and button with no error
[INFO] F3 · Email validation on submit
[INFO] ├─ TC-06 · invalid email format shows 'Valid email required' and blocks submission
[INFO] │  ├─ ✔ TC-06 · rejects "ash#loremcompany.com" – # used instead of @
[INFO] │  └─ ...
[INFO] Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## Resources used

- Selenium docs: [WebDriver](https://www.selenium.dev/documentation/webdriver/), [Browser](https://www.selenium.dev/documentation/webdriver/browser/), [Elements](https://www.selenium.dev/documentation/webdriver/elements/), [Locators](https://www.selenium.dev/documentation/webdriver/elements/locators/), [Waits](https://www.selenium.dev/documentation/webdriver/waits/)
- Test Automation University: [Selenium WebDriver with Java](https://testautomationu.applitools.com/selenium-webdriver-tutorial-java/), [Setting a Foundation for Successful Test Automation](https://testautomationu.applitools.com/setting-a-foundation-for-successful-test-automation/), [GitHub Actions for Testing](https://testautomationu.applitools.com/github-actions-for-testing/)
- Application under test: [QAM-lab1](https://github.com/123annick/QAM-lab1)

**Author:** Annick Ujeneza
