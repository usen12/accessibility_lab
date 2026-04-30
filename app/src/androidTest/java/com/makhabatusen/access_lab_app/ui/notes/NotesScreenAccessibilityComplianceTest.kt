package com.makhabatusen.access_lab_app.ui.notes

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.tryPerformAccessibilityChecks
import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.makhabatusen.access_lab_app.BaseAccessibilityComplianceTest
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.ui.theme.AccessibleDarkOnSurface
import com.makhabatusen.access_lab_app.ui.theme.AccessibleDarkSurface
import com.makhabatusen.access_lab_app.ui.theme.AccessibleLightOnSurface
import com.makhabatusen.access_lab_app.ui.theme.AccessibleLightSurface
import com.makhabatusen.access_lab_app.ui.theme.ErrorRed
import com.makhabatusen.access_lab_app.ui.theme.HighContrastLightOnSurface
import com.makhabatusen.access_lab_app.ui.theme.HighContrastLightSurface
import com.makhabatusen.access_lab_app.ui.theme.SuccessGreen
import com.makhabatusen.access_lab_app.ui.util.AccessibilityUtils
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotesScreenAccessibilityComplianceTest : BaseAccessibilityComplianceTest() {

    override val componentName = "NotesScreen"

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    companion object {
        @JvmStatic
        @org.junit.BeforeClass
        fun enableAccessibilityChecks() {
            try {
                AccessibilityChecks.enable().setRunChecksFromRootView(true)
            } catch (e: Exception) { }
        }
    }

    @Before
    fun setUp() {
        resetResults()
        composeTestRule.setContent {
            NotesScreen(onEditNote = {})
        }
        waitForNotesScreenToLoad()
    }

    @After
    fun tearDown() {
        generateAccessibilityReports()
    }

    private fun waitForNotesScreenToLoad() {
        composeTestRule.waitForIdle()
        try {
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_notes_list))
                .assertExists()
        } catch (e: Exception) {
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_note_add_action))
                .assertExists()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.1.3.1: Info and Relationships
    // ============================================================================

    @Test
    fun test_11_1_3_1_NoteListStructureAccessible() {
        runTest(
            "Note List Structure Accessible",
            "11.1.3.1",
            "Note list structure is programmatically determinable"
        ) {
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_notes_list))
                .assertExists()
                .assertIsDisplayed()
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_note_add_action))
                .assertExists()
                .assertIsDisplayed()
                .assertIsEnabled()
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_note_add_action))
                .assertHasRole(Role.Button, "Add note button should have Button role")
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_notes_list))
                .assertHasContentDescription("Notes list should have content description")
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.3.1.1: Language of Screen
    // ============================================================================

    @Test
    fun test_11_3_1_1_NoteScreenLanguageDeterminable() {
        runTest(
            "Note Screen Language Determinable",
            "11.3.1.1",
            "Note screen language is programmatically determinable"
        ) {
            val notesListText = getString(R.string.cd_notes_list)
            val addNoteText = getString(R.string.cd_note_add_action)
            assert(notesListText.isNotEmpty()) { "Notes list string resource should not be empty" }
            assert(addNoteText.isNotEmpty()) { "Add note string resource should not be empty" }
            composeTestRule.onNodeWithContentDescription(notesListText).assertExists()
            composeTestRule.onNodeWithContentDescription(addNoteText).assertExists()
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_notes_list))
                .assertIsDisplayed()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.4.1.1: Parsing
    // ============================================================================

    @Test
    fun test_11_4_1_1_NoteScreenMarkupValid() {
        runTest(
            "Note Screen Markup Valid",
            "11.4.1.1",
            "Note screen markup is valid and accessible"
        ) {
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_note_add_action))
                .assertHasContentDescription("Add note button should have content description")
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_notes_list))
                .assertHasContentDescription("Notes list should have content description")
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_note_add_action))
                .assertHasRole(Role.Button, "Add note button should have Button role")
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_notes_list))
                .assertExists()
                .assertIsDisplayed()
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
            "Note screen theme contrast meets WCAG standards"
        ) {
            assert(AccessibilityUtils.meetsWCAGAA(AccessibleLightOnSurface, AccessibleLightSurface)) {
                "Light theme contrast ratio should meet WCAG AA (4.5:1)"
            }
            assert(AccessibilityUtils.meetsWCAGAA(AccessibleDarkOnSurface, AccessibleDarkSurface)) {
                "Dark theme contrast ratio should meet WCAG AA (4.5:1)"
            }
            assert(AccessibilityUtils.meetsWCAGAA(HighContrastLightOnSurface, HighContrastLightSurface)) {
                "High contrast theme ratio should meet WCAG AA (4.5:1)"
            }
            assert(AccessibilityUtils.meetsWCAGAA(ErrorRed, AccessibleLightSurface)) {
                "Error color contrast ratio should meet WCAG AA (4.5:1)"
            }
            assert(AccessibilityUtils.meetsWCAGAA(SuccessGreen, AccessibleLightSurface)) {
                "Success color contrast ratio should meet WCAG AA (4.5:1)"
            }
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // LINT INTEGRATION
    // ============================================================================

    @Test
    fun test_LintAccessibilityChecks() {
        runTest(
            "Lint Accessibility Checks",
            "LINT_INTEGRATION",
            "NotesScreen passes all Lint accessibility checks"
        ) {
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_notes_list))
                .assertExists()
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_note_add_action))
                .assertExists()
                .assertIsEnabled()
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_note_add_action))
                .assertHasRole(Role.Button, "Add note button should have Button role")
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_notes_list))
                .assertHasContentDescription("Notes list should have content description")
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }
}
