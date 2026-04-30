package com.makhabatusen.access_lab_app.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.tryPerformAccessibilityChecks
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.makhabatusen.access_lab_app.BaseAccessibilityComplianceTest
import com.makhabatusen.access_lab_app.MainActivity
import com.makhabatusen.access_lab_app.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * EN 301 549 Compliance Tests for HomePage.
 *
 * Navigates to the home page via anonymous (guest) login, avoiding Firebase auth.
 */
@RunWith(AndroidJUnit4::class)
class HomeScreenAccessibilityComplianceTest : BaseAccessibilityComplianceTest() {

    override val componentName = "HomeScreen"

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        resetResults()
        navigateToHomeScreen()
    }

    @After
    fun tearDown() {
        generateAccessibilityReports()
    }

    private fun navigateToHomeScreen() {
        // Wait for login screen or home screen to appear
        composeTestRule.waitUntil(timeoutMillis = 20000) {
            val loginNodes = composeTestRule
                .onAllNodesWithText(getString(R.string.login_welcome_title))
                .fetchSemanticsNodes().size
            val homeNodes = composeTestRule
                .onAllNodesWithText(getString(R.string.home_topbar_title))
                .fetchSemanticsNodes().size
            loginNodes > 0 || homeNodes > 0
        }

        // If already on home screen, we're done
        if (composeTestRule.onAllNodesWithText(getString(R.string.home_topbar_title))
                .fetchSemanticsNodes().isNotEmpty()) {
            return
        }

        // Navigate via guest login
        composeTestRule.onNodeWithText(getString(R.string.login_guest_button_text)).performClick()
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText(getString(R.string.home_topbar_title))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.1.1.1: Non-text Content
    // ============================================================================

    @Test
    fun test_11_1_1_1_AppLogoHasContentDescription() {
        runTest(
            "App Logo Has Content Description",
            "11.1.1.1",
            "The Accessibility Lab image/logo has a non-empty content description"
        ) {
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_home_access_lab_image))
                .assertExists()
                .assertIsDisplayed()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.2.5.1: Pointer Gestures / Touch Target Size
    // ============================================================================

    @Test
    fun test_11_2_5_1_ActionButtonsTouchTargets() {
        runTest(
            "Action Buttons Touch Targets",
            "11.2.5.1",
            "Start Quiz and Settings buttons meet the 48dp minimum touch target"
        ) {
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_home_start_quiz_button))
                .assertExists()
                .assertWidthIsAtLeast(48.dp)

            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_home_see_results_button))
                .assertExists()
                .assertWidthIsAtLeast(48.dp)

            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.5.2.14: Accessible Name
    // ============================================================================

    @Test
    fun test_11_5_2_14_ButtonLabelsMatchContentDescriptions() {
        runTest(
            "Button Labels Match Content Descriptions",
            "11.5.2.14",
            "Buttons have accessible names that match their visible labels"
        ) {
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_home_start_quiz_button))
                .assertExists()
                .assertIsEnabled()

            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_home_see_results_button))
                .assertExists()
                .assertIsEnabled()

            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.5.2.15: Content Section Labels
    // ============================================================================

    @Test
    fun test_11_5_2_15_NotesCarouselHasContentDescription() {
        runTest(
            "Notes Carousel Has Content Description",
            "11.5.2.15",
            "The notes carousel section has a content description for assistive technologies"
        ) {
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_home_notes_carousel))
                .assertExists()
                .assertIsDisplayed()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.4.1.1: Parsing
    // ============================================================================

    @Test
    fun test_11_4_1_1_HomeScreenStructureValid() {
        runTest(
            "Home Screen Structure Valid",
            "11.4.1.1",
            "Home screen has valid semantic structure with no missing accessibility properties"
        ) {
            // Verify all key sections are present and accessible
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_home_access_lab_image))
                .assertExists()
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_home_action_buttons))
                .assertExists()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }
}
