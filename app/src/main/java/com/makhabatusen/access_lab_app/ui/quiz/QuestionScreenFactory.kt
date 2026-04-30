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
    
    /**
     * Gets the screen type for a given question ID.
     * Useful for analytics, testing, or conditional logic.
     */
    fun getScreenType(questionId: Int): QuestionScreenType {
        val screen = QuestionScreenRegistry.getScreen(questionId)
        return screen.screenType
    }
    
    /**
     * Checks if a question ID has a custom screen implementation.
     */
    fun hasCustomScreen(questionId: Int): Boolean {
        return QuestionScreenRegistry.hasScreen(questionId) && questionId != 0
    }
    
    /**
     * Gets all available screen types.
     */
    fun getAllScreenTypes(): List<QuestionScreenType> {
        return QuestionScreenRegistry.getAllScreens().map { it.screenType }
    }
    
    /**
     * Registers a new question screen.
     * 
     * @param screen The question screen to register
     */
    fun registerScreen(screen: QuestionScreen) {
        QuestionScreenRegistry.register(screen)
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