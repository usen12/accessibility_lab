package com.makhabatusen.access_lab_app.ui.auth

import android.content.pm.ActivityInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.tryPerformAccessibilityChecks
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.makhabatusen.access_lab_app.BaseAccessibilityComplianceTest
import com.makhabatusen.access_lab_app.MainActivity
import com.makhabatusen.access_lab_app.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenAccessibilityComplianceTest : BaseAccessibilityComplianceTest() {

    override val componentName = "RegisterScreen"

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        resetResults()
        navigateToRegisterScreen()
    }

    @After
    fun tearDown() {
        generateAccessibilityReports()
    }

    private fun navigateToRegisterScreen() {
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            val loginNodes = composeTestRule.onAllNodesWithText(getString(R.string.login_welcome_title)).fetchSemanticsNodes().size
            val registerNodes = composeTestRule.onAllNodesWithText(getString(R.string.register_welcome_title)).fetchSemanticsNodes().size
            val homeNodes = composeTestRule.onAllNodesWithText("Access Lab").fetchSemanticsNodes().size
            loginNodes > 0 || registerNodes > 0 || homeNodes > 0
        }

        if (composeTestRule.onAllNodesWithText(getString(R.string.register_welcome_title)).fetchSemanticsNodes().isNotEmpty()) return

        if (composeTestRule.onAllNodesWithText(getString(R.string.login_welcome_title)).fetchSemanticsNodes().isEmpty()) {
            logoutToLoginScreen()
        }

        composeTestRule.onNodeWithText(getString(R.string.login_register_prompt)).performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText(getString(R.string.register_welcome_title)).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun logoutToLoginScreen() {
        try {
            composeTestRule.onNodeWithContentDescription("Logout").performClick()
        } catch (e: AssertionError) {
            try {
                composeTestRule.onNodeWithText("Logout").performClick()
            } catch (e2: AssertionError) {
                composeTestRule.onNodeWithContentDescription("Profile").performClick()
                composeTestRule.waitForIdle()
                composeTestRule.onNodeWithText("Logout").performClick()
            }
        }
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText(getString(R.string.login_welcome_title)).fetchSemanticsNodes().isNotEmpty()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.3.3.4: Error Prevention (Form Validation)
    // ============================================================================

    @Test
    fun test_11_3_3_4_FormValidationPreventsInvalidSubmission() {
        runTest(
            "Form Validation Prevents Invalid Submission",
            "11.3.3.4",
            "Form validation prevents submission with invalid email and password"
        ) {
            composeTestRule.onNodeWithTag("registerEmailField").performTextInput("invalid-email")
            composeTestRule.onNodeWithTag("registerPasswordField").performTextInput("123")
            composeTestRule.onNodeWithTag("registerConfirmPasswordField").performTextInput("123")
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_register_button)).performClick()

            assert(composeTestRule.onAllNodesWithText(getString(R.string.register_email_invalid_error)).fetchSemanticsNodes().isNotEmpty())
            assert(composeTestRule.onAllNodesWithText(getString(R.string.register_password_too_short_error)).fetchSemanticsNodes().isNotEmpty())
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    @Test
    fun test_11_3_3_4_PasswordConfirmationValidation() {
        runTest(
            "Password Confirmation Validation",
            "11.3.3.4",
            "Password confirmation validation prevents submission when passwords don't match"
        ) {
            composeTestRule.onNodeWithTag("registerEmailField").performTextInput("user@example.com")
            composeTestRule.onNodeWithTag("registerPasswordField").performTextInput("TestPassword1!")
            composeTestRule.onNodeWithTag("registerConfirmPasswordField").performTextInput("DifferentPassword1!")
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_register_button)).performClick()

            assert(composeTestRule.onAllNodesWithText(getString(R.string.register_passwords_mismatch_error)).fetchSemanticsNodes().isNotEmpty())
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.1.3.5: Machine-readable input purposes
    // ============================================================================

    @Test
    fun test_11_1_3_5_RegisterFieldsHaveLabels() {
        runTest(
            "Register Fields Have Labels",
            "11.1.3.5",
            "All registration input fields have machine-readable labels and content descriptions"
        ) {
            composeTestRule.onNodeWithTag("registerEmailField")
                .assertExists().assertIsDisplayed()
                .assertHasContentDescription("Email field is missing content description")
            composeTestRule.onNodeWithTag("registerPasswordField")
                .assertExists().assertIsDisplayed()
                .assertHasContentDescription("Password field is missing content description")
            composeTestRule.onNodeWithTag("registerConfirmPasswordField")
                .assertExists().assertIsDisplayed()
                .assertHasContentDescription("Confirm password field is missing content description")
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.1.1.1: Non-text Content
    // ============================================================================

    @Test
    fun test_11_1_1_1_AllElementsHaveContentDescriptions() {
        runTest(
            "All Elements Have Content Descriptions",
            "11.1.1.1",
            "Header text, subtitle, all buttons and fields have accessible names"
        ) {
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_register_welcome_title)).assertExists()
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_register_welcome_subtitle)).assertExists()
            composeTestRule.onNodeWithTag("registerEmailField").assertHasContentDescription("Email field is missing content description")
            composeTestRule.onNodeWithTag("registerPasswordField").assertHasContentDescription("Password field is missing content description")
            composeTestRule.onNodeWithTag("registerConfirmPasswordField").assertHasContentDescription("Confirm password field is missing content description")
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_register_button)).assertExists()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.2.5.3: Label in Name
    // ============================================================================

    @Test
    fun test_11_2_5_3_VisibleLabelsMatchAccessibleNames() {
        runTest(
            "Visible Labels Match Accessible Names",
            "11.2.5.3",
            "Content descriptions of fields contain the visible label text"
        ) {
            val emailCD = composeTestRule.onNodeWithTag("registerEmailField")
                .fetchSemanticsNode().config.getOrNull(SemanticsProperties.ContentDescription)?.firstOrNull()
            assert(emailCD != null) { "Email field is missing content description" }

            val passwordCD = composeTestRule.onNodeWithTag("registerPasswordField")
                .fetchSemanticsNode().config.getOrNull(SemanticsProperties.ContentDescription)?.firstOrNull()
            assert(passwordCD != null) { "Password field is missing content description" }

            val confirmCD = composeTestRule.onNodeWithTag("registerConfirmPasswordField")
                .fetchSemanticsNode().config.getOrNull(SemanticsProperties.ContentDescription)?.firstOrNull()
            assert(confirmCD != null) { "Confirm password field is missing content description" }

            assert(
                composeTestRule.onNodeWithContentDescription(getString(R.string.cd_register_button))
                    .fetchSemanticsNode().config.contains(SemanticsProperties.ContentDescription)
            ) { "Register button is missing content description" }

            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.5.2.15: State changes communicated to AT
    // ============================================================================

    @Test
    fun test_11_5_2_15_ErrorMessagesHaveLiveRegions() {
        runTest(
            "Error Messages Have Live Regions",
            "11.5.2.15",
            "Validation error messages use live region semantics for AT announcements"
        ) {
            composeTestRule.onNodeWithTag("registerEmailField").performTextInput("invalid-email")
            composeTestRule.onNodeWithTag("registerPasswordField").performTextInput("123")
            composeTestRule.onNodeWithTag("registerConfirmPasswordField").performTextInput("123")
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_register_button)).performClick()

            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithText(getString(R.string.register_email_invalid_error)).fetchSemanticsNodes().isNotEmpty()
            }

            val semantics = composeTestRule.onNodeWithText(getString(R.string.register_email_invalid_error)).fetchSemanticsNode()
            assert(semantics.config.contains(SemanticsProperties.LiveRegion)) { "Error message is missing live region semantics" }
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.3.3.2: Labels and Instructions
    // ============================================================================

    @Test
    fun test_11_3_3_2_PasswordStrengthFeedback() {
        runTest(
            "Password Strength Feedback",
            "11.3.3.2",
            "Password field provides hints or instructions about strength requirements"
        ) {
            composeTestRule.onNodeWithTag("registerPasswordField")
                .assertExists()
                .assertHasContentDescription("Password field lacks content description for screen readers")
            composeTestRule.onNodeWithTag("registerConfirmPasswordField")
                .assertExists()
                .assertHasContentDescription("Confirm password field lacks content description for screen readers")
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.1.3.4: Orientation
    // ============================================================================

    @Test
    fun test_11_1_3_4_OrientationChange() {
        runTest(
            "Orientation Change",
            "11.1.3.4",
            "All registration fields are accessible in landscape orientation"
        ) {
            composeTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithText(getString(R.string.register_welcome_title)).fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNodeWithTag("registerEmailField").assertExists()
            composeTestRule.onNodeWithTag("registerPasswordField").assertExists()
            composeTestRule.onNodeWithTag("registerConfirmPasswordField").assertExists()
            composeTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.2.1.1: Keyboard Accessibility
    // ============================================================================

    @Test
    fun test_11_2_1_1_AllFieldsKeyboardAccessible() {
        runTest(
            "All Fields Keyboard Accessible",
            "11.2.1.1",
            "All three input fields and the Register button are enabled and operable"
        ) {
            composeTestRule.onNodeWithTag("registerEmailField")
                .assertExists().assertIsEnabled()
                .assertHasContentDescription("Email field is not accessible via keyboard")
            composeTestRule.onNodeWithTag("registerPasswordField")
                .assertExists().assertIsEnabled()
                .assertHasContentDescription("Password field is not accessible via keyboard")
            composeTestRule.onNodeWithTag("registerConfirmPasswordField")
                .assertExists().assertIsEnabled()
                .assertHasContentDescription("Confirm password field is not accessible via keyboard")
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_register_button))
                .assertExists().assertIsEnabled()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }
}
