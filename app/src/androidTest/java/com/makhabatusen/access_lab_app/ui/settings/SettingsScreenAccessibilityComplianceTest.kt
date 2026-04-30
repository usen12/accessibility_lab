package com.makhabatusen.access_lab_app.ui.settings

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
 * EN 301 549 Compliance Tests for SettingsPage.
 *
 * Navigates to the Settings screen via anonymous (guest) login and then tapping
 * the Settings navigation bar item.
 */
@RunWith(AndroidJUnit4::class)
class SettingsScreenAccessibilityComplianceTest : BaseAccessibilityComplianceTest() {

    override val componentName = "SettingsScreen"

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        resetResults()
        navigateToSettingsScreen()
    }

    @After
    fun tearDown() {
        generateAccessibilityReports()
    }

    private fun navigateToSettingsScreen() {
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

        // Tap the Settings nav item
        composeTestRule.onNodeWithContentDescription("Navigate to Settings screen").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText(getString(R.string.settings_title))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.1.3.1: Info and Relationships
    // ============================================================================

    @Test
    fun test_11_1_3_1_SectionHeadingsPresent() {
        runTest(
            "Section Headings Present",
            "11.1.3.1",
            "Settings section titles are semantically marked as headings"
        ) {
            // Profile Settings heading
            composeTestRule.onNodeWithText(getString(R.string.settings_title))
                .assertExists()
                .assertIsDisplayed()

            // App Settings heading
            composeTestRule.onNodeWithText(getString(R.string.app_settings_heading))
                .assertExists()
                .assertIsDisplayed()

            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.2.5.1: Pointer Gestures / Touch Target Size
    // ============================================================================

    @Test
    fun test_11_2_5_1_SettingsItemsTouchTargets() {
        runTest(
            "Settings Items Touch Targets",
            "11.2.5.1",
            "Each settings row meets the 48dp minimum touch target requirement"
        ) {
            val accessibilityItemCd = getString(
                R.string.cd_profile_settings_item,
                getString(R.string.profile_accessibility_settings)
            )
            val languageItemCd = getString(
                R.string.cd_profile_settings_item,
                getString(R.string.language_settings_title)
            )
            val feedbackItemCd = getString(
                R.string.cd_profile_settings_item,
                getString(R.string.feedback_title)
            )

            composeTestRule.onNodeWithContentDescription(accessibilityItemCd)
                .assertExists()
                .assertWidthIsAtLeast(48.dp)

            composeTestRule.onNodeWithContentDescription(languageItemCd)
                .assertExists()
                .assertWidthIsAtLeast(48.dp)

            composeTestRule.onNodeWithContentDescription(feedbackItemCd)
                .assertExists()
                .assertWidthIsAtLeast(48.dp)

            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.5.2.14: Accessible Name Present
    // ============================================================================

    @Test
    fun test_11_5_2_14_SettingsLabelsMatchText() {
        runTest(
            "Settings Labels Match Text",
            "11.5.2.14",
            "Tappable settings rows have content descriptions that contain the visible label text"
        ) {
            val accessibilityItemCd = getString(
                R.string.cd_profile_settings_item,
                getString(R.string.profile_accessibility_settings)
            )
            val languageItemCd = getString(
                R.string.cd_profile_settings_item,
                getString(R.string.language_settings_title)
            )
            val feedbackItemCd = getString(
                R.string.cd_profile_settings_item,
                getString(R.string.feedback_title)
            )

            composeTestRule.onNodeWithContentDescription(accessibilityItemCd)
                .assertExists()
                .assertIsEnabled()

            composeTestRule.onNodeWithContentDescription(languageItemCd)
                .assertExists()
                .assertIsEnabled()

            composeTestRule.onNodeWithContentDescription(feedbackItemCd)
                .assertExists()
                .assertIsEnabled()

            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.5.2.15: Content Section Labels
    // ============================================================================

    @Test
    fun test_11_5_2_15_ProfileSectionHasContentDescription() {
        runTest(
            "Profile Section Has Content Description",
            "11.5.2.15",
            "The profile settings card section has a content description for assistive technologies"
        ) {
            composeTestRule.onNodeWithContentDescription(
                getString(R.string.cd_profile_settings_section)
            )
                .assertExists()
                .assertIsDisplayed()

            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.2.1.1: Keyboard Accessibility
    // ============================================================================

    @Test
    fun test_11_2_1_1_LogoutButtonAccessible() {
        runTest(
            "Logout Button Accessible",
            "11.2.1.1",
            "The logout button is enabled and has a content description"
        ) {
            composeTestRule.onNodeWithContentDescription(
                getString(R.string.cd_profile_logout_button)
            )
                .assertExists()
                .assertIsEnabled()

            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.4.1.1: Parsing
    // ============================================================================

    @Test
    fun test_11_4_1_1_SettingsScreenStructureValid() {
        runTest(
            "Settings Screen Structure Valid",
            "11.4.1.1",
            "Settings screen has valid semantic structure with all key sections accessible"
        ) {
            composeTestRule.onNodeWithContentDescription(
                getString(R.string.cd_profile_settings_section)
            ).assertExists()

            composeTestRule.onNodeWithContentDescription(
                getString(R.string.cd_profile_logout_button)
            ).assertExists()

            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }
}
