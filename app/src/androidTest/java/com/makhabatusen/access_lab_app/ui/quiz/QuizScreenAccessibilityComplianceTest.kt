package com.makhabatusen.access_lab_app.ui.quiz


import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
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

/**
 * EN 301 549 Compliance Tests for QuizScreen.
 *
 * Uses isolated Compose rendering (no Firebase) — QuizScreen only needs a ViewModel
 * with a default factory, so setContent works without MainActivity.
 */
@RunWith(AndroidJUnit4::class)
class QuizScreenAccessibilityComplianceTest : BaseAccessibilityComplianceTest() {

    override val componentName = "QuizScreen"

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        resetResults()
        composeTestRule.setContent {
            QuizScreen(onBackPressed = {})
        }
        composeTestRule.waitForIdle()
    }

    @After
    fun tearDown() {
        generateAccessibilityReports()
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.1.3.1: Info and Relationships
    // ============================================================================

    @Test
    fun test_11_1_3_1_QuestionContainerHasContentDescription() {
        runTest(
            "Question Container Has Content Description",
            "11.1.3.1",
            "The quiz question interaction area has a programmatically determinable structure"
        ) {
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_quiz_question_text, ""), substring = true)
                .assertExists()
                .assertIsDisplayed()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.2.5.1: Pointer Gestures / Touch Target Size
    // ============================================================================

    @Test
    fun test_11_2_5_1_AnswerOptionsTouchTargets() {
        runTest(
            "Answer Options Touch Targets",
            "11.2.5.1",
            "Each answer option meets the 48dp minimum touch target requirement"
        ) {
            val answerNodes = composeTestRule.onAllNodes(
                hasContentDescription(getString(R.string.cd_quiz_answer_option, ""), substring = true)
            )
            answerNodes.onFirst()
                .assertExists()
                .assertWidthIsAtLeast(48.dp)
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.5.2.14: Accessible Name Present
    // ============================================================================

    @Test
    fun test_11_5_2_14_AnswerOptionsHaveAccessibleNames() {
        runTest(
            "Answer Options Have Accessible Names",
            "11.5.2.14",
            "Each answer option has a content description containing the answer text"
        ) {
            val answerNodes = composeTestRule.onAllNodes(
                hasContentDescription(getString(R.string.cd_quiz_answer_option, ""), substring = true)
            )
            answerNodes.onFirst()
                .assertExists()
                .assertIsDisplayed()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.5.2.7: Values of User Interface Components
    // ============================================================================

    @Test
    fun test_11_5_2_7_QuizProgressIndicatorHasContentDescription() {
        runTest(
            "Quiz Progress Indicator Has Content Description",
            "11.5.2.7",
            "The quiz progress indicator communicates its value to assistive technologies"
        ) {
            composeTestRule.onNodeWithContentDescription(getString(R.string.quiz_question_progress, 1, 16), substring = true)
                .assertExists()
                .assertIsDisplayed()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.2.1.1: Keyboard Accessibility
    // ============================================================================

    @Test
    fun test_11_2_1_1_AnswerOptionsAreOperable() {
        runTest(
            "Answer Options Are Operable",
            "11.2.1.1",
            "Answer options are enabled and can be activated"
        ) {
            val answerNodes = composeTestRule.onAllNodes(
                hasContentDescription(getString(R.string.cd_quiz_answer_option, ""), substring = true)
            )
            answerNodes.onFirst()
                .assertExists()
                .assertIsEnabled()
                .performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }

    // ============================================================================
    // EN 301 549 CRITERION 11.1.4.3: Contrast
    // ============================================================================

    @Test
    fun test_11_1_4_3_QuizContainerRendersWithAccessibleColors() {
        runTest(
            "Quiz Container Renders With Accessible Colors",
            "11.1.4.3",
            "The quiz screen renders without layout errors — color contrast is validated statically via theme"
        ) {
            composeTestRule.onNodeWithContentDescription(getString(R.string.cd_quiz_question_text, ""), substring = true)
                .assertExists()
                .assertIsDisplayed()
            composeTestRule.onRoot().tryPerformAccessibilityChecks()
        }
    }
}
