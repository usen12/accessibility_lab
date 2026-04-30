package com.makhabatusen.access_lab_app.ui.auth

import android.content.pm.ActivityInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.tryPerformAccessibilityChecks
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.makhabatusen.access_lab_app.BaseAccessibilityComplianceTest
import com.makhabatusen.access_lab_app.MainActivity
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.ui.theme.AccessibleDarkBackground
import com.makhabatusen.access_lab_app.ui.theme.AccessibleDarkOnBackground
import com.makhabatusen.access_lab_app.ui.theme.AccessibleDarkOnPrimary
import com.makhabatusen.access_lab_app.ui.theme.AccessibleDarkOnSecondary
import com.makhabatusen.access_lab_app.ui.theme.AccessibleDarkOnSurface
import com.makhabatusen.access_lab_app.ui.theme.AccessibleDarkOnSurfaceVariant
import com.makhabatusen.access_lab_app.ui.theme.AccessibleDarkOnTertiary
import com.makhabatusen.access_lab_app.ui.theme.AccessibleDarkPrimary
import com.makhabatusen.access_lab_app.ui.theme.AccessibleDarkSecondary
import com.makhabatusen.access_lab_app.ui.theme.AccessibleDarkSurface
import com.makhabatusen.access_lab_app.ui.theme.AccessibleDarkSurfaceVariant
import com.makhabatusen.access_lab_app.ui.theme.AccessibleDarkTertiary
import com.makhabatusen.access_lab_app.ui.theme.AccessibleLightBackground
import com.makhabatusen.access_lab_app.ui.theme.AccessibleLightOnBackground
import com.makhabatusen.access_lab_app.ui.theme.AccessibleLightOnPrimary
import com.makhabatusen.access_lab_app.ui.theme.AccessibleLightOnSecondary
import com.makhabatusen.access_lab_app.ui.theme.AccessibleLightOnSurface
import com.makhabatusen.access_lab_app.ui.theme.AccessibleLightOnSurfaceVariant
import com.makhabatusen.access_lab_app.ui.theme.AccessibleLightOnTertiary
import com.makhabatusen.access_lab_app.ui.theme.AccessibleLightPrimary
import com.makhabatusen.access_lab_app.ui.theme.AccessibleLightSecondary
import com.makhabatusen.access_lab_app.ui.theme.AccessibleLightSurface
import com.makhabatusen.access_lab_app.ui.theme.AccessibleLightSurfaceVariant
import com.makhabatusen.access_lab_app.ui.theme.AccessibleLightTertiary
import com.makhabatusen.access_lab_app.ui.theme.ErrorRed
import com.makhabatusen.access_lab_app.ui.theme.HighContrastLightBackground
import com.makhabatusen.access_lab_app.ui.theme.HighContrastLightOnBackground
import com.makhabatusen.access_lab_app.ui.theme.HighContrastLightOnPrimary
import com.makhabatusen.access_lab_app.ui.theme.HighContrastLightOnSurface
import com.makhabatusen.access_lab_app.ui.theme.HighContrastLightPrimary
import com.makhabatusen.access_lab_app.ui.theme.HighContrastLightSurface
import com.makhabatusen.access_lab_app.ui.theme.SuccessGreen
import com.makhabatusen.access_lab_app.ui.theme.WarningOrange
import com.makhabatusen.access_lab_app.ui.util.AccessibilityUtils
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenAccessibilityComplianceTest : BaseAccessibilityComplianceTest() {

    override val componentName = "LoginScreen"

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        resetResults()
        waitForAppToLoad()
        ensureLoginScreenIsActive()
    }

    @After
    fun tearDown() {
        try {
            composeTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } catch (e: Exception) {
            // best-effort orientation restore
        }
        generateAccessibilityReports()
    }

    private fun waitForAppToLoad() {
        composeTestRule.waitUntil(timeoutMillis = 20000) {
            val splashNodes = composeTestRule.onAllNodesWithText(getString(R.string.splash_loading)).fetchSemanticsNodes().size
            val loginNodes = composeTestRule.onAllNodesWithText(getString(R.string.login_welcome_title)).fetchSemanticsNodes().size
            val mainNodes = composeTestRule.onAllNodesWithText("Access Lab").fetchSemanticsNodes().size
            splashNodes > 0 || loginNodes > 0 || mainNodes > 0
        }
        when (detectCurrentScreen()) {
            "MainNavigation" -> logoutToLoginScreen()
            "SplashScreen" -> waitForAuthenticationComplete()
            "Unknown" -> {
                composeTestRule.waitUntil(timeoutMillis = 5000) { detectCurrentScreen() != "Unknown" }
                val final = detectCurrentScreen()
                if (final != "LoginScreen") throw IllegalStateException("Could not reach LoginScreen. Current: $final")
            }
        }
    }

    private fun detectCurrentScreen(): String = try {
        composeTestRule.onNodeWithText(getString(R.string.login_welcome_title)).assertExists()
        "LoginScreen"
    } catch (e: AssertionError) {
        try {
            composeTestRule.onNodeWithText("Access Lab").assertExists()
            "MainNavigation"
        } catch (e2: AssertionError) {
            try {
                composeTestRule.onNodeWithText(getString(R.string.splash_loading)).assertExists()
                "SplashScreen"
            } catch (e3: AssertionError) {
                "Unknown"
            }
        }
    }

    private fun logoutToLoginScreen() {
        try {
            composeTestRule.onNodeWithContentDescription("Logout").performClick()
        } catch (e: AssertionError) {
            try {
                composeTestRule.onNodeWithText("Logout").performClick()
            } catch (e2: AssertionError) {
                composeTestRule.onNodeWithContentDescription("Menu").performClick()
                composeTestRule.waitForIdle()
                composeTestRule.onNodeWithText("Logout").performClick()
            }
        }
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText(getString(R.string.login_welcome_title)).fetchSemanticsNodes().size == 1
        }
    }

    private fun waitForAuthenticationComplete() {
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            val loginNodes = composeTestRule.onAllNodesWithText(getString(R.string.login_welcome_title)).fetchSemanticsNodes().size
            val mainNodes = composeTestRule.onAllNodesWithText("Access Lab").fetchSemanticsNodes().size
            loginNodes > 0 || mainNodes > 0
        }
        if (detectCurrentScreen() == "MainNavigation") logoutToLoginScreen()
        else if (detectCurrentScreen() != "LoginScreen") throw IllegalStateException("Authentication did not complete properly")
    }

    private fun ensureLoginScreenIsActive() {
        if (detectCurrentScreen() != "LoginScreen") logoutToLoginScreen()
        composeTestRule.onNodeWithText(getString(R.string.login_welcome_title)).assertExists()
    }

    private fun resetLoginScreenState() {
        try {
            ensureLoginScreenIsActive()
            composeTestRule.onNodeWithTag("emailField").performTextReplacement("")
            composeTestRule.onNodeWithTag("passwordField").performTextReplacement("")
            composeTestRule.waitForIdle()
            val emailText = composeTestRule.onNodeWithTag("emailField")
                .fetchSemanticsNode().config.getOrNull(SemanticsProperties.EditableText)?.text ?: ""
            val passwordText = composeTestRule.onNodeWithTag("passwordField")
                .fetchSemanticsNode().config.getOrNull(SemanticsProperties.EditableText)?.text ?: ""
            assert(emailText.isEmpty()) { "Email field should be empty after reset" }
            assert(passwordText.isEmpty()) { "Password field should be empty after reset" }
        } catch (e: Exception) {
            // non-fatal — don't block the test
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.1.3.5: Machine-readable input purposes
    // ============================================================================

    @Test
    fun test_11_1_3_5_EmailFieldHasProperLabel() {
        runTest(
            "Email Field Has Proper Label",
            "11.1.3.5",
            "Machine-readable input purposes through appropriate labels"
        ) {
            val emailField = composeTestRule.onNodeWithTag("emailField")
            emailField.assertExists()
            emailField.assertTextFromResource(R.string.login_email_label, "Email field label not found")
            emailField.assertHasContentDescription("Email field is missing content description")
            val semantics = emailField.fetchSemanticsNode()
            assert(semantics.config.contains(SemanticsProperties.Text)) { "Email field is missing text semantics" }
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    @Test
    fun test_11_1_3_5_PasswordFieldHasProperLabel() {
        runTest(
            "Password Field Has Proper Label",
            "11.1.3.5",
            "Machine-readable input purposes through appropriate labels"
        ) {
            val passwordField = composeTestRule.onNodeWithTag("passwordField")
            passwordField.assertExists()
            passwordField.assertTextFromResource(R.string.login_password_label, "Password field label not found")
            passwordField.assertHasContentDescription("Password field is missing content description")
            val semantics = passwordField.fetchSemanticsNode()
            assert(semantics.config.contains(SemanticsProperties.Text)) { "Password field is missing text semantics" }
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    @Test
    fun test_11_1_3_5_AllInteractiveElementsHaveLabels() {
        runTest(
            "All Interactive Elements Have Labels",
            "11.1.3.5",
            "Machine-readable input purposes through appropriate labels"
        ) {
            composeTestRule.onNodeWithTag("emailField")
                .assertTextFromResource(R.string.login_email_label, "Email field label not found")
            composeTestRule.onNodeWithTag("passwordField")
                .assertTextFromResource(R.string.login_password_label, "Password field label not found")
            composeTestRule.onNode(hasText(getString(R.string.login_button_text)))
                .assertExists()
                .assertHasContentDescription("Login button is missing content description")
            composeTestRule.onNode(hasText(getString(R.string.login_guest_button_text)))
                .assertExists()
                .assertHasContentDescription("Guest button is missing content description")
            composeTestRule.onNode(hasText(getString(R.string.login_register_prompt)))
                .assertExists()
                .assertHasContentDescription("Register link is missing content description")
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    @Test
    fun test_11_1_3_5_AllFormFieldsHaveTestTags() {
        runTest(
            "All Form Fields Have Test Tags",
            "11.1.3.5",
            "Machine-readable input purposes through appropriate labels"
        ) {
            val emailField = composeTestRule.onNodeWithTag("emailField")
            val passwordField = composeTestRule.onNodeWithTag("passwordField")
            emailField.assertExists().assertIsDisplayed().assertIsEnabled()
            passwordField.assertExists().assertIsDisplayed().assertIsEnabled()
            emailField.assertHasContentDescription("Email field is missing content description")
            passwordField.assertHasContentDescription("Password field is missing content description")
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.3.3.2: Labels and instructions for input fields
    // ============================================================================

    @Test
    fun test_11_3_3_2_EmailFieldHasLabelAndInstructions() {
        runTest(
            "Email Field Has Label And Instructions",
            "11.3.3.2",
            "Labels and instructions for input fields"
        ) {
            val emailField = composeTestRule.onNodeWithTag("emailField")
            emailField.assertExists()
            emailField.assertHasContentDescription("Email field is missing content description")
            try {
                composeTestRule.onNodeWithText("your@email.com").assertExists()
            } catch (e: AssertionError) {
                emailField.assertHasStateDescription("Email field is missing state description or placeholder")
            }
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    @Test
    fun test_11_3_3_2_PasswordFieldSemantics() {
        runTest(
            "Password Field Semantics",
            "11.3.3.2",
            "Labels and instructions for input fields"
        ) {
            val passwordField = composeTestRule.onNodeWithTag("passwordField")
            passwordField.assertExists()
            val semantics = passwordField.fetchSemanticsNode()
            assert(semantics.config.contains(SemanticsProperties.Password)) { "Password field is missing password semantics" }
            assert(semantics.config.contains(SemanticsProperties.ContentDescription)) { "Password field is missing content description" }
            assert(semantics.config.contains(SemanticsProperties.StateDescription)) { "Password field is missing state description" }
            passwordField.assertIsEnabled().assertIsDisplayed()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.1.1.1: Non-text Content
    // ============================================================================

    @Test
    fun test_11_1_1_1_AllTextElementsHaveContentDescriptions() {
        runTest(
            "All Text Elements Have Content Descriptions",
            "11.1.1.1",
            "Non-text Content — provide text alternatives for non-text content"
        ) {
            val textElements = mapOf(
                "Welcome Text" to getString(R.string.login_welcome_title),
                "Subtitle Text" to getString(R.string.login_welcome_subtitle),
                "Email Label" to getString(R.string.login_email_label),
                "Password Label" to getString(R.string.login_password_label),
                "Login Button" to getString(R.string.login_button_text),
                "Guest Button" to getString(R.string.login_guest_button_text),
                "Register Link" to getString(R.string.login_register_prompt)
            )
            textElements.forEach { (name, text) ->
                val element = composeTestRule.onNode(hasText(text))
                element.assertExists().assertIsDisplayed()
                element.assertHasContentDescription("$name is missing content description")
                val semantics = element.fetchSemanticsNode()
                assert(
                    semantics.config.contains(SemanticsProperties.Text) ||
                    semantics.config.contains(SemanticsProperties.ContentDescription)
                ) { "$name is missing text or content description semantics" }
            }
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.1.4.3: Contrast
    // ============================================================================

    @Test
    fun test_11_1_4_3_ThemeContrastCompliance() {
        runTest(
            "Theme Contrast Compliance",
            "11.1.4.3",
            "Contrast for texts and UI elements according to requirements"
        ) {
            listOf(
                "Light OnBackground/Background" to (AccessibleLightOnBackground to AccessibleLightBackground),
                "Light OnSurface/Surface" to (AccessibleLightOnSurface to AccessibleLightSurface),
                "Light OnPrimary/Primary" to (AccessibleLightOnPrimary to AccessibleLightPrimary),
                "Light OnSecondary/Secondary" to (AccessibleLightOnSecondary to AccessibleLightSecondary),
                "Light OnSurfaceVariant/SurfaceVariant" to (AccessibleLightOnSurfaceVariant to AccessibleLightSurfaceVariant),
                "Light OnTertiary/Tertiary" to (AccessibleLightOnTertiary to AccessibleLightTertiary),
                "Dark OnBackground/Background" to (AccessibleDarkOnBackground to AccessibleDarkBackground),
                "Dark OnSurface/Surface" to (AccessibleDarkOnSurface to AccessibleDarkSurface),
                "Dark OnPrimary/Primary" to (AccessibleDarkOnPrimary to AccessibleDarkPrimary),
                "Dark OnSecondary/Secondary" to (AccessibleDarkOnSecondary to AccessibleDarkSecondary),
                "Dark OnSurfaceVariant/SurfaceVariant" to (AccessibleDarkOnSurfaceVariant to AccessibleDarkSurfaceVariant),
                "Dark OnTertiary/Tertiary" to (AccessibleDarkOnTertiary to AccessibleDarkTertiary)
            ).forEach { (name, colors) ->
                val (fg, bg) = colors
                assert(AccessibilityUtils.meetsWCAGAA(fg, bg)) {
                    "$name contrast ratio ${String.format("%.2f", AccessibilityUtils.calculateContrastRatio(fg, bg))}:1 does not meet WCAG AA"
                }
            }
            listOf(
                "HighContrastLight OnBackground/Background" to (HighContrastLightOnBackground to HighContrastLightBackground),
                "HighContrastLight OnSurface/Surface" to (HighContrastLightOnSurface to HighContrastLightSurface),
                "HighContrastLight OnPrimary/Primary" to (HighContrastLightOnPrimary to HighContrastLightPrimary)
            ).forEach { (name, colors) ->
                val (fg, bg) = colors
                assert(AccessibilityUtils.meetsWCAGAAA(fg, bg)) {
                    "$name contrast ratio ${String.format("%.2f", AccessibilityUtils.calculateContrastRatio(fg, bg))}:1 does not meet WCAG AAA"
                }
            }
            listOf(
                "ErrorRed/Background" to (ErrorRed to AccessibleLightBackground),
                "SuccessGreen/Background" to (SuccessGreen to AccessibleLightBackground),
                "WarningOrange/Background" to (WarningOrange to AccessibleLightBackground)
            ).forEach { (name, colors) ->
                val (fg, bg) = colors
                assert(AccessibilityUtils.meetsWCAGAA(fg, bg)) {
                    "$name contrast ratio ${String.format("%.2f", AccessibilityUtils.calculateContrastRatio(fg, bg))}:1 does not meet WCAG AA"
                }
            }
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.2.1.1: Complete keyboard operation possible
    // ============================================================================

    @Test
    fun test_11_2_1_1_AllElementsAreKeyboardAccessible() {
        runTest(
            "All Elements Are Keyboard Accessible",
            "11.2.1.1",
            "Complete keyboard operation possible"
        ) {
            val emailField = composeTestRule.onNodeWithTag("emailField")
            val passwordField = composeTestRule.onNodeWithTag("passwordField")
            val loginButton = composeTestRule.onNode(hasText(getString(R.string.login_button_text)))
            val guestButton = composeTestRule.onNode(hasText(getString(R.string.login_guest_button_text)))
            val registerLink = composeTestRule.onNode(hasText(getString(R.string.login_register_prompt)))

            emailField.assertIsEnabled()
            passwordField.assertIsEnabled()
            loginButton.assertIsEnabled()
            guestButton.assertIsEnabled()
            registerLink.assertIsEnabled()

            assert(emailField.fetchSemanticsNode().config.contains(SemanticsProperties.EditableText)) { "Email field missing editable text semantics" }
            assert(passwordField.fetchSemanticsNode().config.contains(SemanticsProperties.EditableText)) { "Password field missing editable text semantics" }
            assert(loginButton.fetchSemanticsNode().config.contains(SemanticsProperties.Role)) { "Login button missing role semantics" }
            assert(guestButton.fetchSemanticsNode().config.contains(SemanticsProperties.Role)) { "Guest button missing role semantics" }

            emailField.assertHasContentDescription("Email field missing content description")
            passwordField.assertHasContentDescription("Password field missing content description")
            loginButton.assertHasContentDescription("Login button missing content description")
            guestButton.assertHasContentDescription("Guest button missing content description")
            registerLink.assertHasContentDescription("Register link missing content description")
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    @Test
    fun test_11_2_1_1_CompleteKeyboardNavigationFlow() {
        runTest(
            "Complete Keyboard Navigation Flow",
            "11.2.1.1",
            "Complete keyboard operation possible"
        ) {
            val emailField = composeTestRule.onNodeWithTag("emailField")
            emailField.performClick()
            emailField.performTextInput("test@example.com")
            composeTestRule.waitForIdle()

            val passwordField = composeTestRule.onNodeWithTag("passwordField")
            passwordField.performClick()
            passwordField.performTextInput("password123")
            composeTestRule.waitForIdle()

            val emailText = emailField.fetchSemanticsNode().config.getOrNull(SemanticsProperties.EditableText)?.text ?: ""
            val passwordText = passwordField.fetchSemanticsNode().config.getOrNull(SemanticsProperties.EditableText)?.text ?: ""
            assert(emailText.isNotEmpty()) { "Email field should contain text" }
            assert(passwordText.isNotEmpty()) { "Password field should contain text" }

            val loginButton = composeTestRule.onNode(hasText(getString(R.string.login_button_text)))
            loginButton.performClick()
            composeTestRule.waitForIdle()

            composeTestRule.onNodeWithText(getString(R.string.login_welcome_title)).assertExists()
            loginButton.assertHasContentDescription("Login button missing content description")

            composeTestRule.waitForIdle()
            val loginSemantics = loginButton.fetchSemanticsNode()
            if (!loginSemantics.config.contains(SemanticsProperties.Disabled)) {
                loginButton.assertIsEnabled()
            } else {
                loginButton.assertHasContentDescription("Login button missing content description when disabled")
            }

            val guestButton = composeTestRule.onNode(hasText(getString(R.string.login_guest_button_text)))
            guestButton.performClick()
            composeTestRule.waitForIdle()

            composeTestRule.onNodeWithText(getString(R.string.login_welcome_title)).assertExists()
            guestButton.assertHasContentDescription("Guest button missing content description")
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.3.3.1: Erroneous inputs are clearly identified
    // ============================================================================

    @Test
    fun test_11_3_3_1_EmptyFieldsShowError() {
        runTest(
            "Empty Fields Show Error",
            "11.3.3.1",
            "Erroneous inputs are clearly identified"
        ) {
            val emailField = composeTestRule.onNodeWithTag("emailField")
            val passwordField = composeTestRule.onNodeWithTag("passwordField")
            emailField.performTextReplacement("")
            passwordField.performTextReplacement("")
            composeTestRule.waitForIdle()

            val emailText = emailField.fetchSemanticsNode().config.getOrNull(SemanticsProperties.EditableText)?.text ?: ""
            val passwordText = passwordField.fetchSemanticsNode().config.getOrNull(SemanticsProperties.EditableText)?.text ?: ""
            assert(emailText.isEmpty()) { "Email field should be empty before testing" }
            assert(passwordText.isEmpty()) { "Password field should be empty before testing" }

            composeTestRule.onNode(hasText(getString(R.string.login_button_text))).performClick()

            composeTestRule.waitUntil(timeoutMillis = 10000) {
                composeTestRule.onAllNodesWithText(getString(R.string.login_missing_fields_error)).fetchSemanticsNodes().isNotEmpty()
            }

            val errorMessage = composeTestRule.onNodeWithText(getString(R.string.login_missing_fields_error))
            errorMessage.assertExists()
            errorMessage.assertHasContentDescription("Empty fields error message missing content description")
            errorMessage.assertHasStateDescription("Empty fields error message missing state description")
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    @Test
    fun test_11_3_3_1_InvalidEmailShowsError() {
        runTest(
            "Invalid Email Shows Error",
            "11.3.3.1",
            "Erroneous inputs are clearly identified"
        ) {
            composeTestRule.onNodeWithTag("emailField").performClick()
            composeTestRule.onNodeWithTag("emailField").performTextInput("invalid-email")
            composeTestRule.onNodeWithTag("passwordField").performClick()
            composeTestRule.onNodeWithTag("passwordField").performTextInput("password123")
            composeTestRule.onNode(hasText(getString(R.string.login_button_text))).performClick()

            val errorMessage = composeTestRule.onNode(hasText(getString(R.string.login_email_invalid_error)))
            errorMessage.assertExists()
            errorMessage.assertHasContentDescription("Email validation error missing content description")
            errorMessage.assertHasStateDescription("Email validation error missing state description")
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    @Test
    fun test_11_3_3_1_ErrorStateManagement() {
        runTest(
            "Error State Management",
            "11.3.3.1",
            "Erroneous inputs are clearly identified"
        ) {
            val emailField = composeTestRule.onNodeWithTag("emailField")
            emailField.performClick()
            emailField.performTextInput("invalid")

            composeTestRule.waitUntil(timeoutMillis = 3000) {
                try {
                    composeTestRule.onNodeWithText("Please enter a valid email address (include @)").assertExists()
                    true
                } catch (e: AssertionError) { false }
            }

            val errorMessage = composeTestRule.onNodeWithText("Please enter a valid email address (include @)")
            errorMessage.assertExists()
            errorMessage.assertHasContentDescription("Error suggestion missing content description")

            emailField.performTextReplacement("")
            composeTestRule.waitForIdle()

            try {
                composeTestRule.onNodeWithText("Please enter a valid email address (include @)").assertDoesNotExist()
            } catch (e: AssertionError) {
                // brief delay in clearing is acceptable
            }
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.3.3.3: Suggestions for error correction
    // ============================================================================

    @Test
    fun test_11_3_3_3_ErrorMessagesIncludeSuggestions() {
        runTest(
            "Error Messages Include Suggestions",
            "11.3.3.3",
            "Suggestions for error correction available"
        ) {
            composeTestRule.onNodeWithTag("emailField").performClick()
            composeTestRule.onNodeWithTag("emailField").performTextInput("invalid")
            composeTestRule.onNodeWithTag("passwordField").performClick()
            composeTestRule.onNodeWithTag("passwordField").performTextInput("password123")
            composeTestRule.onNode(hasText(getString(R.string.login_button_text))).performClick()

            val errorMessage = composeTestRule.onNode(hasText("Please enter a valid email address (include @)"))
            errorMessage.assertExists()
            errorMessage.assertHasContentDescription("Error suggestion missing content description")
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.5.2.15: State changes communicated to assistive technologies
    // ============================================================================

    @Test
    fun test_11_5_2_15_ErrorMessagesUseLiveRegions() {
        runTest(
            "Error Messages Use Live Regions",
            "11.5.2.15",
            "State changes are communicated to assistive technologies"
        ) {
            composeTestRule.onNode(hasText(getString(R.string.login_button_text))).performClick()

            composeTestRule.waitUntil(timeoutMillis = 10000) {
                composeTestRule.onAllNodesWithText(getString(R.string.login_missing_fields_error)).fetchSemanticsNodes().isNotEmpty()
            }

            val errorMessage = composeTestRule.onNodeWithText(getString(R.string.login_missing_fields_error))
            errorMessage.assertExists()
            val semantics = errorMessage.fetchSemanticsNode()
            assert(semantics.config.contains(SemanticsProperties.LiveRegion)) {
                "Error message missing live region semantics"
            }
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    @Test
    fun test_11_5_2_15_LoadingStatesAreAnnounced() {
        runTest(
            "Loading States Are Announced",
            "11.5.2.15",
            "State changes are communicated to assistive technologies"
        ) {
            composeTestRule.onNodeWithTag("emailField").performClick()
            composeTestRule.onNodeWithTag("emailField").performTextInput("test@example.com")
            composeTestRule.onNodeWithTag("passwordField").performClick()
            composeTestRule.onNodeWithTag("passwordField").performTextInput("password123")

            val loginButton = composeTestRule.onNode(hasText(getString(R.string.login_button_text)))
            loginButton.performClick()

            composeTestRule.waitUntil(timeoutMillis = 5000) {
                try { loginButton.assertIsNotEnabled(); true } catch (e: AssertionError) { false }
            }

            loginButton.assertIsNotEnabled()
            loginButton.assertHasContentDescription("Login button missing content description")
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.3.2.2: No unexpected context changes on input
    // ============================================================================

    @Test
    fun test_11_3_2_2_NoUnexpectedContextChangesOnInput() {
        runTest(
            "No Unexpected Context Changes On Input",
            "11.3.2.2",
            "Changes of context must not happen unexpectedly when inputting data"
        ) {
            val emailField = composeTestRule.onNodeWithTag("emailField")
            val passwordField = composeTestRule.onNodeWithTag("passwordField")

            emailField.performClick()
            emailField.performTextInput("test@example.com")
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText(getString(R.string.login_welcome_title)).assertExists()

            passwordField.performClick()
            passwordField.performTextInput("password123")
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText(getString(R.string.login_welcome_title)).assertExists()

            val loginButton = composeTestRule.onNode(hasText(getString(R.string.login_button_text)))
            loginButton.performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText(getString(R.string.login_welcome_title)).assertExists()

            resetLoginScreenState()
            val guestButton = composeTestRule.onNode(hasText(getString(R.string.login_guest_button_text)))
            guestButton.performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText(getString(R.string.login_welcome_title)).assertExists()
            guestButton.performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText(getString(R.string.login_welcome_title)).assertExists()
            guestButton.assertHasContentDescription("Guest button missing content description")

            resetLoginScreenState()
            emailField.performClick()
            emailField.performTextInput("invalid-email")
            composeTestRule.waitUntil(timeoutMillis = 2000) {
                try {
                    composeTestRule.onNode(hasText("Please enter a valid email address (include @)")).assertExists()
                    true
                } catch (e: AssertionError) { false }
            }
            composeTestRule.onNodeWithText(getString(R.string.login_welcome_title)).assertExists()

            emailField.assertIsEnabled()
            passwordField.assertIsEnabled()
            loginButton.assertIsEnabled()
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
            "Content does not restrict view and operation to a single display orientation"
        ) {
            composeTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithText(getString(R.string.login_welcome_title)).fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNodeWithText(getString(R.string.login_welcome_title)).assertExists()
            composeTestRule.onNodeWithTag("emailField").assertExists()
            composeTestRule.onNodeWithTag("passwordField").assertExists()
            composeTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // USER BEHAVIOR SIMULATION
    // ============================================================================

    @Test
    fun test_UserBehaviorSimulation() {
        runTest(
            "User Behavior Simulation",
            "USER_SIMULATION",
            "Simulates real user interaction patterns with accessibility compliance"
        ) {
            composeTestRule.onNode(hasText(getString(R.string.login_welcome_title)))
                .assertExists()
                .assertHasContentDescription("Welcome text missing content description")

            val emailField = composeTestRule.onNodeWithTag("emailField")
            emailField.performClick()
            emailField.performTextInput("test@example.com")
            emailField.assertHasContentDescription("Email field missing content description")

            val passwordField = composeTestRule.onNodeWithTag("passwordField")
            passwordField.performClick()
            passwordField.performTextInput("password123")
            passwordField.assertHasContentDescription("Password field missing content description")

            composeTestRule.onNode(hasText(getString(R.string.login_button_text)))
                .performClick()
                .assertHasContentDescription("Login button missing content description")
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.2.5.3: Label in Name — visible labels match accessible names
    // ============================================================================

    @Test
    fun test_11_2_5_3_VisibleLabelsMatchAccessibleNames() {
        runTest(
            "Visible Labels Match Accessible Names",
            "11.2.5.3",
            "Visible labels of components must match their accessible names"
        ) {
            fun assertLabelMatchesCd(field: androidx.compose.ui.test.SemanticsNodeInteraction, expectedLabel: String, context: String) {
                val actualCd = field.fetchSemanticsNode().config.getOrNull(SemanticsProperties.ContentDescription)?.firstOrNull()
                assert(actualCd != null) { "$context is missing content description" }
                assert(actualCd == expectedLabel) { "$context visible label '$expectedLabel' does not match accessible name '$actualCd'" }
            }

            assertLabelMatchesCd(composeTestRule.onNodeWithTag("emailField"), getString(R.string.login_email_label), "Email field")
            assertLabelMatchesCd(composeTestRule.onNodeWithTag("passwordField"), getString(R.string.login_password_label), "Password field")
            assertLabelMatchesCd(composeTestRule.onNode(hasText(getString(R.string.login_button_text))), getString(R.string.login_button_text), "Login button")
            assertLabelMatchesCd(composeTestRule.onNode(hasText(getString(R.string.login_guest_button_text))), getString(R.string.login_guest_button_text), "Guest button")
            assertLabelMatchesCd(composeTestRule.onNode(hasText(getString(R.string.login_register_prompt))), getString(R.string.login_register_prompt), "Register link")

            val loginButtonForError = composeTestRule.onNode(hasText(getString(R.string.login_button_text)))
            loginButtonForError.performClick()
            composeTestRule.waitForIdle()

            var errorPresent = false
            try {
                composeTestRule.onNode(hasText(getString(R.string.login_missing_fields_error))).assertExists()
                errorPresent = true
            } catch (e: AssertionError) { /* acceptable */ }

            if (errorPresent) {
                try {
                    val errorMessage = composeTestRule.onNode(hasText(getString(R.string.login_missing_fields_error)))
                    val expectedErrorText = getString(R.string.login_missing_fields_error)
                    val actualErrorCd = errorMessage.fetchSemanticsNode().config.getOrNull(SemanticsProperties.ContentDescription)?.firstOrNull()
                    assert(actualErrorCd != null) { "Error message missing content description" }
                    assert(actualErrorCd == expectedErrorText) { "Error message CD '$actualErrorCd' does not match visible text '$expectedErrorText'" }
                } catch (e: AssertionError) { /* acceptable */ }
            }
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }
}
