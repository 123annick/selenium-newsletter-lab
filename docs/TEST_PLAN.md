# Test Plan – Newsletter Sign-up Form (Selenium WebDriver)

| | |
|---|---|
| **Project** | Test Automation with Selenium WebDriver |
| **Application under test** | Newsletter sign-up form with success message – [QAM-lab1](https://github.com/123annick/QAM-lab1) ([live](https://123annick.github.io/QAM-lab1/)) |
| **Author** | Annick Ujeneza |
| **Version** | 1.0 |
| **Test repository** | `selenium-newsletter-lab` |

---

## 1. Purpose

This plan describes **what** is tested, **how**, **with what**, and **when testing is considered done** for the newsletter sign-up form. Every automated test in `src/test/java` carries the ID of a test case below (e.g. `TC-06`) so each test can be traced back to a requirement and justified.

## 2. Scope

### In scope
| ID | Feature (from the QAM-lab1 README) |
|---|---|
| F1 | The sign-up form displays correctly (content and initial state) |
| F2 | A valid email is accepted, the success message shows that email, and *Dismiss* returns to the form |
| F3 | An empty or badly formatted email is rejected with the correct message |
| F4 | Real-time validation: no error while typing the first attempt, check on leaving the field, live feedback afterwards |

Accessibility behaviour that is observable through the UI (focus movement, `aria-invalid`) is checked inside these features.

### Out of scope (with reason)
| Item | Reason |
|---|---|
| Sending a real confirmation email | The app has no backend; the success message is client-side only |
| Pixel-perfect visual design | Covered by the visual comparison done in QAM-lab1; Selenium functional tests are not the right tool |
| Mobile layout (< 600 px) | Lab scope is desktop Chrome; the window size is fixed at 1366×900 for stable results |
| Browsers other than Chrome | The brief requires ChromeDriver; the framework is ready to add more (`DriverFactory`) |
| Performance / load, security testing | Static page with no server – not applicable |

## 3. Test approach

| Aspect | Decision | Justification |
|---|---|---|
| Level | System / end-to-end UI tests | The brief asks for UI automation of the user flows |
| Type | Functional (positive + negative), plus UI-observable accessibility checks | Covers every feature in the README |
| Tooling | Java 21+, Selenium WebDriver 4, JUnit 5, Maven, GitHub Actions | Required by the brief; Maven gives reproducible dependencies |
| Design pattern | Page Object Model + Page Factory | Locators and page actions in one place; tests read like user stories |
| Synchronisation | Explicit waits only (`WebDriverWait`), no `Thread.sleep`, no implicit wait | Recommended by the Selenium docs; avoids flaky and slow tests |
| Isolation | New browser per test (`@BeforeEach` / `@AfterEach`) | No test depends on another; any order works |
| Test data | Kept outside the tests: `TestData`, `ExpectedText`, `invalid-emails.csv` | Change data without touching test logic |

### Test design techniques
| Technique | Where it is applied |
|---|---|
| **Equivalence partitioning** | Email input split into classes: valid · empty · whitespace-only · invalid format. One representative per invalid sub-class in `invalid-emails.csv` (missing `@`, missing domain, missing TLD, missing local part, space, wrong symbol) |
| **Boundary values** | Empty string `""` vs. whitespace-only `"   "` vs. valid email surrounded by spaces (trimming) |
| **State transition** | Email field states *untouched → validated → live feedback*, and screens *form → success → form* (TC-05, TC-08 – TC-11) |
| **Use-case / scenario testing** | Mouse user (click Subscribe) and keyboard-only user (ENTER) – TC-02, TC-04 |
| **Error guessing** | Leading/trailing spaces, `#` instead of `@` (a common typo) |

## 4. Test environment

| Item | Local | CI (GitHub Actions) |
|---|---|---|
| OS | Windows 11 | `ubuntu-latest` |
| Java | JDK 27 (compiles for Java 21) | Temurin JDK 21 |
| Build | Maven 3.10 | Maven (pre-installed, cached `~/.m2`) |
| Browser | Google Chrome (latest) | Chrome stable via `browser-actions/setup-chrome` |
| Driver | ChromeDriver resolved by Selenium Manager | same |
| Mode | headless by default, `-Dheadless=false` to watch | always headless |
| App URL | local `app/index.html` (or `-DbaseUrl=…`) | local `app/index.html`, or the live URL via *Run workflow* input |

## 5. Entry and exit criteria

**Entry** – the app loads in Chrome; JDK, Maven and Chrome are installed; the project compiles (`mvn test-compile`).

**Exit** – all test cases below are executed; **100 % pass** on the `main` branch in CI; any failure has a screenshot and Surefire report attached to the run; no open *High* severity defect.

## 6. Test cases

Priority: **H** = core business flow / smoke · **M** = important validation · **L** = usability detail.

| ID | Feature | Title | Pri | Test data | Steps | Expected result | Automated in |
|---|---|---|---|---|---|---|---|
| TC-01 | F1 | Form is displayed correctly | H | – | Open the page | Title "Frontend Mentor \| Newsletter sign-up form…", heading "Stay updated!", 3 benefits, email field with placeholder `email@company.com`, button "Subscribe to monthly newsletter", no error, `aria-invalid=false` | `SignupPageLoadTest` |
| TC-02 | F2 | Valid email shows success message | H | `ash@loremcompany.com` | Type email → click Subscribe | Form hidden; "Thanks for subscribing!" shown with the same email; message starts "A confirmation email has been sent to"; focus on success heading | `SubscriptionTest` |
| TC-03 | F2 | Email is trimmed | M | `"   ash@loremcompany.com   "` | Type email → click Subscribe | Success message shows `ash@loremcompany.com` without spaces | `SubscriptionTest` |
| TC-04 | F2 | ENTER key submits the form | M | `ash@loremcompany.com` | Type email → press ENTER | Success message shown with the email | `SubscriptionTest` |
| TC-05 | F2 | Dismiss returns to an empty form | H | `ash@loremcompany.com` | Subscribe → click "Dismiss message" | Form visible, success hidden, email field empty, no error, focus on email field | `SubscriptionTest` |
| TC-06 | F3 | Invalid format is rejected (×6) | H | `invalid-emails.csv` | Type email → click Subscribe | "Valid email required"; red `is-invalid` style; `aria-invalid=true`; focus back on field; success **not** shown | `EmailValidationTest` |
| TC-07 | F3 | Empty / blank email is rejected (×2) | H | `""`, `"   "` | Click Subscribe | "Email address is required"; `aria-invalid=true`; success **not** shown | `EmailValidationTest` |
| TC-08 | F4 | No error while typing the first attempt | L | `ash@lorem` | Type without leaving the field | No error shown | `RealTimeValidationTest` |
| TC-09 | F4 | Leaving the field validates it | M | `ash@lorem` | Type → press TAB | "Valid email required" without submitting | `RealTimeValidationTest` |
| TC-10 | F4 | Error clears when corrected | M | `ash@lorem` → `ash@loremcompany.com` | Submit invalid → type valid | Error text and red style removed | `RealTimeValidationTest` |
| TC-11 | F4 | Message updates live | L | `""` → `ash` | Submit empty → type text | "Email address is required" then "Valid email required" | `RealTimeValidationTest` |

**Total:** 11 test cases → **17 automated executions** (TC-06 runs 6×, TC-07 runs 2×). Smoke suite (`@Tag("smoke")`): TC-01, TC-02.

## 7. Requirements traceability

| Requirement (QAM-lab1 README) | Test cases |
|---|---|
| User can add their email and submit the form | TC-02, TC-04 |
| Success message shows the submitted email | TC-02, TC-03 |
| "Email address is required" when the field is empty | TC-07, TC-11 |
| "Valid email required" when the format is wrong | TC-06, TC-09, TC-11 |
| Real-time validation | TC-08, TC-09, TC-10, TC-11 |
| Dismiss returns to the form | TC-05 |
| Accessible states (focus, `aria-invalid`) | TC-01, TC-02, TC-05, TC-06, TC-07 |

## 8. Execution

| When | How | Suite |
|---|---|---|
| While developing | `mvn test` (or `-Dheadless=false` to watch) | full |
| Quick check | `mvn test -Dgroups=smoke` | smoke |
| Every push / pull request | GitHub Actions `ci.yml` | full |
| Against the deployed site | `mvn test -DbaseUrl=https://123annick.github.io/QAM-lab1/` or *Run workflow* with `base_url` | full |

## 9. Reporting and defect management

- **Console / CI log:** each test is printed as a tree with its `TC-xx` name and duration; each step is logged (`Typing email…`, `Clicking…`).
- **CI job summary:** per-test pass/fail table (`action-junit-report`).
- **Artifacts:** Surefire XML/TXT reports (always), screenshots of failed tests (`target/screenshots`).
- **Notification:** Slack message with PASSED/FAILED, branch, commit and link to the run.
- **Defects:** logged as GitHub Issues with: TC-ID, steps, expected vs actual, screenshot from the artifact, severity (High / Medium / Low).

## 10. Risks and mitigation

| Risk | Impact | Mitigation |
|---|---|---|
| Flaky tests from timing | False failures | Explicit waits on concrete conditions; no sleeps; fresh browser per test |
| Chrome / ChromeDriver version mismatch | Tests cannot start | Selenium Manager resolves the matching driver automatically |
| UI changes break locators | Many failing tests | Locators only in Page Objects; stable `id`s (e.g. `subscribe-btn` added for testability) |
| Live site unavailable | CI failures unrelated to code | CI tests the local copy in `app/` by default |
| Secrets exposed | Unauthorised Slack posts | Webhook stored only as a GitHub secret, never in code |
| Wrong expected text | Tests pass/fail for the wrong reason | Expected text taken from the design/README and kept in `ExpectedText` |

## 11. Deliverables

Test plan (this document) · automated test suite · `ci.yml` pipeline · CI run history with reports and Slack notifications · README with setup and design justification.
