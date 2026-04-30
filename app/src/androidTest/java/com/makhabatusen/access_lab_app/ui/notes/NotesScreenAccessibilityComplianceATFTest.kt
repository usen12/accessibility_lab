package com.makhabatusen.access_lab_app.ui.notes

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.tryPerformAccessibilityChecks
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.makhabatusen.access_lab_app.BaseAccessibilityComplianceTest
import com.makhabatusen.access_lab_app.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotesScreenAccessibilityComplianceATFTest : BaseAccessibilityComplianceTest() {

    override val componentName = "NotesScreen ATF"

    @get:Rule
    val composeTestRule = createAndroidComposeRule<NotesScreenTestActivity>()

    @Before
    fun setUp() {
        resetResults()
    }

    @After
    fun tearDown() {
        generateAccessibilityReports()
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.2.5.1: Pointer Gestures / Touch Target Size
    // ============================================================================

    @Test
    fun test_11_2_5_1_FAB_TouchTargetSize_And_ContentDescription() {
        runTest(
            "FAB Accessibility Test - EN 301 549 11.2.5.1",
            "11.2.5.1",
            "Floating Action Button must have content description and sufficient touch target size"
        ) {
            composeTestRule
                .onNodeWithContentDescription(getString(R.string.cd_note_add_action))
                .assertExists()
                .assertIsDisplayed()
                .assertHasClickAction()
                .assertWidthIsAtLeast(48.dp)
                .assertHeightIsAtLeast(48.dp)
                .performClick()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.1.3.1: Info and Relationships
    // ============================================================================

    @Test
    fun test_11_1_3_1_EmptyStateMessage_InfoAndRelationships() {
        runTest(
            "Empty State Accessibility Test - EN 301 549 11.1.3.1",
            "11.1.3.1",
            "Empty state message must be properly labeled and accessible"
        ) {
            composeTestRule.setContent {
                NotesScreenTestWrapper(notes = emptyList())
            }
            composeTestRule
                .onNodeWithContentDescription(getString(R.string.cd_note_empty_state_message))
                .assertExists()
                .assertIsDisplayed()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    @Test
    fun test_11_1_3_1_NotesListContainer_And_Items_InfoAndRelationships() {
        runTest(
            "Notes List Accessibility Test - EN 301 549 11.1.3.1",
            "11.1.3.1",
            "Notes list container and note items must be accessible"
        ) {
            composeTestRule
                .onNodeWithContentDescription(getString(R.string.cd_notes_list))
                .assertExists()
                .assertIsDisplayed()
            composeTestRule
                .onAllNodes(hasClickAction())
                .onFirst()
                .assertExists()
                .assertIsDisplayed()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    @Test
    fun test_11_2_5_1_DeleteIcon_TouchTargetSize_And_ContentDescription() {
        runTest(
            "Delete Icon Accessibility Test - EN 301 549 11.2.5.1",
            "11.2.5.1",
            "Delete icon must be clickable, properly labeled, and meet touch target requirements"
        ) {
            composeTestRule
                .onAllNodesWithContentDescription(getString(R.string.cd_note_delete_action))
                .onFirst()
                .assertExists()
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    @Test
    fun test_11_1_3_1_ProtectedBadge_And_LockIcon_InfoAndRelationships() {
        runTest(
            "Protected Badge and Lock Icon Test - EN 301 549 11.1.3.1",
            "11.1.3.1",
            "Protected note badge and lock icon must be properly labeled and accessible"
        ) {
            composeTestRule
                .onAllNodesWithContentDescription(getString(R.string.cd_note_protected_badge))
                .onFirst()
                .assertExists()
                .assertIsDisplayed()
            composeTestRule
                .onAllNodesWithContentDescription(getString(R.string.cd_note_protected_icon))
                .onFirst()
                .assertExists()
                .assertIsDisplayed()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    @Test
    fun test_11_1_3_1_TimestampLabel_InfoAndRelationships() {
        runTest(
            "Timestamp Label Accessibility Test - EN 301 549 11.1.3.1",
            "11.1.3.1",
            "Timestamp label must be properly labeled and accessible"
        ) {
            composeTestRule
                .onAllNodesWithContentDescription(getString(R.string.cd_note_created_time))
                .onFirst()
                .assertExists()
                .assertIsDisplayed()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }
}
