package com.makhabatusen.access_lab_app.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertHeightIsAtLeast
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
 * EN 301 549 Compliance Tests for the main navigation bar/rail.
 *
 * Navigates to the main navigation structure via anonymous (guest) login and
 * validates that all five navigation items are accessible and meet EN 301 549
 * criteria for touch targets, accessible names, and operable controls.
 */
@RunWith(AndroidJUnit4::class)
class NavigationAccessibilityComplianceTest : BaseAccessibilityComplianceTest() {

    override val componentName = "MainNavigation"

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        resetResults()
        navigateToMainNavigation()
    }

    @After
    fun tearDown() {
        generateAccessibilityReports()
    }

    private fun navigateToMainNavigation() {
        // Wait for login page or home page
        composeTestRule.waitUntil(timeoutMillis = 20000) {
            val loginNodes = composeTestRule
                .onAllNodesWithText(getString(R.string.login_welcome_title))
                .fetchSemanticsNodes().size
            val homeNodes = composeTestRule
                .onAllNodesWithText(getString(R.string.home_topbar_title))
                .fetchSemanticsNodes().size
            loginNodes > 0 || homeNodes > 0
        }

        // Sign in as guest if still on login page
        if (composeTestRule.onAllNodesWithText(getString(R.string.login_welcome_title))
                .fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onNodeWithText(getString(R.string.login_guest_button_text)).performClick()
            composeTestRule.waitUntil(timeoutMillis = 10000) {
                composeTestRule.onAllNodesWithText(getString(R.string.home_topbar_title))
                    .fetchSemanticsNodes().isNotEmpty()
            }
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.2.4.3: Focus Order / Nav Items Are Focusable
    // ============================================================================

    @Test
    fun test_11_2_4_3_NavItemsAreFocusable() {
        runTest(
            "Nav Items Are Focusable",
            "11.2.4.3",
            "All five navigation bar items exist, are displayed, and are enabled"
        ) {
            composeTestRule.onNodeWithContentDescription("Navigate to Home screen")
                .assertExists()
                .assertIsEnabled()

            composeTestRule.onNodeWithContentDescription("Navigate to Notes screen")
                .assertExists()
                .assertIsEnabled()

            composeTestRule.onNodeWithContentDescription("Navigate to Library screen")
                .assertExists()
                .assertIsEnabled()

            composeTestRule.onNodeWithContentDescription("Navigate to Quiz screen")
                .assertExists()
                .assertIsEnabled()

            composeTestRule.onNodeWithContentDescription("Navigate to Settings screen")
                .assertExists()
                .assertIsEnabled()

            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.2.5.1: Pointer Gestures / Touch Target Size
    // ============================================================================

    @Test
    fun test_11_2_5_1_NavItemsTouchTargets() {
        runTest(
            "Nav Items Touch Targets",
            "11.2.5.1",
            "Each navigation bar item meets the 48dp minimum touch target height"
        ) {
            composeTestRule.onNodeWithContentDescription("Navigate to Home screen")
                .assertExists()
                .assertHeightIsAtLeast(48.dp)

            composeTestRule.onNodeWithContentDescription("Navigate to Notes screen")
                .assertExists()
                .assertHeightIsAtLeast(48.dp)

            composeTestRule.onNodeWithContentDescription("Navigate to Library screen")
                .assertExists()
                .assertHeightIsAtLeast(48.dp)

            composeTestRule.onNodeWithContentDescription("Navigate to Quiz screen")
                .assertExists()
                .assertHeightIsAtLeast(48.dp)

            composeTestRule.onNodeWithContentDescription("Navigate to Settings screen")
                .assertExists()
                .assertHeightIsAtLeast(48.dp)

            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.5.2.14: Accessible Name Present
    // ============================================================================

    @Test
    fun test_11_5_2_14_NavItemsHaveAccessibleNames() {
        runTest(
            "Nav Items Have Accessible Names",
            "11.5.2.14",
            "Each navigation item has a content description matching its destination"
        ) {
            composeTestRule.onNodeWithContentDescription("Navigate to Home screen")
                .assertExists()

            composeTestRule.onNodeWithContentDescription("Navigate to Notes screen")
                .assertExists()

            composeTestRule.onNodeWithContentDescription("Navigate to Library screen")
                .assertExists()

            composeTestRule.onNodeWithContentDescription("Navigate to Quiz screen")
                .assertExists()

            composeTestRule.onNodeWithContentDescription("Navigate to Settings screen")
                .assertExists()

            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.5.2.15: Content Section Labels
    // ============================================================================

    @Test
    fun test_11_5_2_15_NavBarHasContentDescription() {
        runTest(
            "Nav Bar Has Content Description",
            "11.5.2.15",
            "The bottom navigation bar container has a content description for assistive technologies"
        ) {
            composeTestRule.onNodeWithContentDescription("Bottom navigation bar")
                .assertExists()
                .assertIsDisplayed()

            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.2.1.1: Keyboard Accessibility / Operability
    // ============================================================================

    @Test
    fun test_11_2_1_1_NavItemsAreOperable() {
        runTest(
            "Nav Items Are Operable",
            "11.2.1.1",
            "Tapping a navigation item navigates to the destination screen"
        ) {
            // Tap Notes nav item and verify Notes screen appears
            composeTestRule.onNodeWithContentDescription("Navigate to Notes screen")
                .assertExists()
                .performClick()

            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithText(getString(R.string.nav_notes))
                    .fetchSemanticsNodes().isNotEmpty()
            }

            // Tap Home to return
            composeTestRule.onNodeWithContentDescription("Navigate to Home screen")
                .assertExists()
                .performClick()

            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithText(getString(R.string.home_topbar_title))
                    .fetchSemanticsNodes().isNotEmpty()
            }

            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }
}
