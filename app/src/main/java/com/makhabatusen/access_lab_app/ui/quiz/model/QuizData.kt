package com.makhabatusen.access_lab_app.ui.quiz.model

import androidx.compose.runtime.Immutable

/**
 * Represents a single quiz question with multiple choice answers
 */
@Immutable
data class QuizQuestion(
    val id: Int,
    val questionText: String,
    val answers: List<QuizAnswer>,
    val correctAnswerIds: List<Int> // Multiple correct answers supported
)

/**
 * Represents a single answer option for a quiz question
 */
@Immutable
data class QuizAnswer(
    val id: Int,
    val text: String,
    val isCorrect: Boolean
)

/**
 * Quiz questions for accessibility knowledge testing
 * Questions are created in the composable to support string resources
 */
object QuizData {
    // This object is kept for compatibility but questions are now created in the composable
} 