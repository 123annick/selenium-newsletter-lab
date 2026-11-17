/* ==========================================================================
   Newsletter sign-up: real-time validation and success message
   ========================================================================== */

// ---------- 1. Elements ----------
const signupCard = document.getElementById("signup");
const successCard = document.getElementById("success");
const form = document.getElementById("signup-form");
const emailInput = document.getElementById("email");
const errorMessage = document.getElementById("email-error");
const successEmail = document.getElementById("success-email");
const successTitle = document.getElementById("success-title");
const dismissButton = document.getElementById("dismiss-btn");

// Practical email pattern: text, one @, a domain, a dot, and at least 2 letters (e.g. name@site.com)
const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;

// Error messages shown to the user
const MESSAGES = {
  empty: "Email address is required",
  invalid: "Valid email required",
};

// Becomes true after the first blur or submit. From then on the field is checked
// on every keystroke, so errors never appear while someone is still typing their first attempt.
let hasInteracted = false;

// ---------- 2. Validation helpers ----------

/** Returns an error message for the value, or an empty string if it is valid. */
function getEmailError(value) {
  const email = value.trim();
  if (email === "") return MESSAGES.empty;
  if (!EMAIL_PATTERN.test(email)) return MESSAGES.invalid;
  return "";
}

/** Shows or clears the error message and the red input styling. */
function setErrorState(message) {
  const hasError = message !== "";
  errorMessage.textContent = message;
  emailInput.classList.toggle("is-invalid", hasError);
  emailInput.setAttribute("aria-invalid", String(hasError));
}

/** Validates the field, updates the screen, and returns true if the email is valid. */
function validateEmail() {
  const error = getEmailError(emailInput.value);
  setErrorState(error);
  return error === "";
}

// ---------- 3. Real-time validation ----------

// Leaving the field: check it, as long as the user has typed something (or tried before)
emailInput.addEventListener("blur", () => {
  if (emailInput.value.trim() !== "" || hasInteracted) {
    hasInteracted = true;
    validateEmail();
  }
});

// Typing: after the first check, update the feedback live
emailInput.addEventListener("input", () => {
  if (hasInteracted) validateEmail();
});

// ---------- 4. Submitting the form ----------
form.addEventListener("submit", (event) => {
  event.preventDefault(); // stop the page from reloading
  hasInteracted = true;

  if (!validateEmail()) {
    emailInput.focus(); // take the user straight back to the field to fix it
    return;
  }

  showSuccess(emailInput.value.trim());
});

// ---------- 5. Switching between the form and the success message ----------

/** Hides the form and shows the success message with the user's email. */
function showSuccess(email) {
  successEmail.textContent = email; // textContent shows it as plain text, so no code can be injected
  signupCard.hidden = true;
  successCard.hidden = false;
  successTitle.focus(); // screen readers announce the new message
}

/** Returns to an empty form, ready for another sign-up. */
function dismissSuccess() {
  form.reset();
  setErrorState("");
  hasInteracted = false;
  successCard.hidden = true;
  signupCard.hidden = false;
  emailInput.focus();
}

dismissButton.addEventListener("click", dismissSuccess);
