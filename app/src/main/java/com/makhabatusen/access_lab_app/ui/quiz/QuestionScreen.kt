package com.makhabatusen.access_lab_app.ui.quiz

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.makhabatusen.access_lab_app.ui.quiz.model.QuizQuestion

/**
 * Base interface for all question screens.
 * Ensures consistency across different question screen implementations.
 */
interface QuestionScreen {

    /**
     * The unique identifier for this question screen.
     */
    val questionId: Int

    /**
     * The type of this question screen.
     */
    val screenType: QuestionScreenType

    /**
     * Renders the question screen content.
     *
     * @param modifier The modifier to apply to the screen
     * @param question The question data (optional, for future extensibility)
     */
    @Composable
    fun Content(
        modifier: Modifier,
        question: QuizQuestion?
    )
}

/**
 * Abstract base class for question screens with common functionality.
 * Provides default implementations and common patterns.
 */
abstract class BaseQuestionScreen : QuestionScreen {

    /**
     * Default implementation that wraps the content with common modifiers.
     */
    @Composable
    override fun Content(
        modifier: Modifier,
        question: QuizQuestion?
    ) {
        QuestionScreenWrapper(
            modifier = modifier,
            contentDescription = contentDescription(),
            questionId = questionId
        ) {
            ScreenContent(modifier = Modifier.fillMaxSize(), question = question)
        }
    }

    /**
     * The actual screen content to be implemented by subclasses.
     */
    @Composable
    protected abstract fun ScreenContent(
        modifier: Modifier,
        question: QuizQuestion?
    )

    /**
     * The content description for this question screen.
     */
    protected abstract fun contentDescription(): String
}

/**
 * Wrapper composable that provides common functionality for all question screens.
 * Handles accessibility, common modifiers, and layout patterns.
 */
@Composable
private fun QuestionScreenWrapper(
    modifier: Modifier,
    contentDescription: String,
    questionId: Int,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                this.contentDescription = contentDescription
            }
    ) {
        content()
    }
} 