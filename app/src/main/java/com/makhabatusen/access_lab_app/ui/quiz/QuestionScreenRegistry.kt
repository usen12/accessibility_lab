package com.makhabatusen.access_lab_app.ui.quiz

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.makhabatusen.access_lab_app.ui.quiz.model.QuizQuestion

/**
 * Registry for managing question screens.
 * Provides a centralized way to register and retrieve question screen implementations.
 */
object QuestionScreenRegistry {
    
    private val screens = mutableMapOf<Int, QuestionScreen>()
    
    init {
        // Register default screens
        register(VisualAccessibilityScreen())
        register(WCAGPrinciplesScreen())
        register(DefaultQuestionScreenImpl)
    }
    
    /**
     * Registers a question screen implementation.
     * 
     * @param screen The question screen to register
     */
    fun register(screen: QuestionScreen) {
        screens[screen.questionId] = screen
    }
    
    /**
     * Gets a question screen by ID.
     * 
     * @param questionId The ID of the question
     * @return The question screen implementation, or default screen if not found
     */
    fun getScreen(questionId: Int): QuestionScreen {
        return screens[questionId] ?: screens[0] ?: DefaultQuestionScreenImpl
    }


}

/**
 * Extension function to create a composable from a QuestionScreen.
 */
@Composable
fun QuestionScreen.asComposable(
    modifier: Modifier = Modifier,
    question: QuizQuestion? = null
) {
    this.Content(modifier = modifier, question = question)
} 