package com.makhabatusen.access_lab_app.ui.quiz

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Factory for creating question screens based on question ID.
 * This follows the Factory pattern for easy maintenance and screen replacement.
 */
object QuestionScreenFactory {
    
    /**
     * Creates the appropriate question screen based on the question ID.
     * 
     * @param questionId The ID of the question
     * @param modifier The modifier to apply to the screen
     * @return The composable question screen
     */
    @Composable
    fun createQuestionScreen(
        questionId: Int,
        modifier: Modifier = Modifier
    ): @Composable () -> Unit {
        return {
            val screen = QuestionScreenRegistry.getScreen(questionId)
            screen.asComposable(modifier = modifier)
        }
    }

}

/**
 * Enum representing different types of question screens.
 * Useful for analytics, testing, and screen categorization.
 */
enum class QuestionScreenType {
    VISUAL_ACCESSIBILITY,
    WCAG_PRINCIPLES,
    DEFAULT
} 