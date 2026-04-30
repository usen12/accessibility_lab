package com.makhabatusen.access_lab_app.ui.quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.makhabatusen.access_lab_app.ui.quiz.model.QuizQuestion
import com.makhabatusen.access_lab_app.ui.util.Constants

/**
 * ViewModel for managing quiz state and logic
 */
class QuizViewModel : ViewModel() {
    
    // Make quizQuestions a Compose state variable to trigger recomposition
    private var quizQuestions by mutableStateOf<List<QuizQuestion>>(emptyList())
    
    // Current question index
    var currentQuestionIndex by mutableStateOf(0)
        private set
    
    // Selected answers for current question (multiple selection supported)
    var selectedAnswers by mutableStateOf(setOf<Int>())
        private set
    
    // Quiz completion status
    var isQuizCompleted by mutableStateOf(false)
        private set
    
    // User answers for all questions
    private val userAnswers = mutableMapOf<Int, Set<Int>>()
    
    /**
     * Initialize the quiz with questions
     */
    fun initializeQuiz(questions: List<QuizQuestion>) {
        quizQuestions = questions.shuffled()
        resetQuiz()
    }
    
    // Current question
    val currentQuestion: QuizQuestion?
        get() = if (currentQuestionIndex < quizQuestions.size) {
            quizQuestions[currentQuestionIndex]
        } else null
    
    // Total questions count
    val totalQuestions: Int
        get() = quizQuestions.size
    
    // Progress percentage
    val progressPercentage: Float
        get() = if (totalQuestions > 0) {
            (currentQuestionIndex + 1).toFloat() / totalQuestions.toFloat()
        } else 0f
    
    // Can proceed to next question
    val canProceedToNext: Boolean
        get() = selectedAnswers.size >= Constants.Quiz.MIN_ANSWERS_REQUIRED
    
    // Can finish quiz
    val canFinishQuiz: Boolean
        get() = currentQuestionIndex == totalQuestions - 1 && selectedAnswers.size >= Constants.Quiz.MIN_ANSWERS_REQUIRED
    
    /**
     * Select or deselect an answer with improved logic
     */
    fun toggleAnswer(answerId: Int) {
        val question = currentQuestion ?: return
        
        // Check if this is a True/False question by examining answer texts
        // Note: This check needs to be updated to handle localized strings
        val isTrueFalseQuestion = question.answers.size == 2 && 
            question.answers.all { answer ->
                val text = answer.text.trim().lowercase()
                text in setOf("true", "false", "wahr", "falsch")
            }
        
        if (isTrueFalseQuestion) {
            // True/False question: only one can be selected at a time
            selectedAnswers = setOf(answerId)
        } else {
            // Multiple choice question: toggle selection
            selectedAnswers = if (selectedAnswers.contains(answerId)) {
                selectedAnswers - answerId
            } else {
                selectedAnswers + answerId
            }
        }
    }
    
    /**
     * Move to the next question
     */
    fun nextQuestion() {
        val question = currentQuestion ?: return
        
        // Save current answers
        userAnswers[question.id] = selectedAnswers
        
        // Move to next question
        if (currentQuestionIndex < totalQuestions - 1) {
            currentQuestionIndex++
            selectedAnswers = userAnswers[quizQuestions[currentQuestionIndex].id] ?: emptySet()
        } else {
            // Quiz completed
            isQuizCompleted = true
        }
    }
    
    /**
     * Move to the previous question
     */
    fun previousQuestion() {
        if (currentQuestionIndex > 0) {
            // Save current answers
            val question = currentQuestion
            if (question != null) {
                userAnswers[question.id] = selectedAnswers
            }
            
            currentQuestionIndex--
            selectedAnswers = userAnswers[quizQuestions[currentQuestionIndex].id] ?: emptySet()
        }
    }
    
    /**
     * Reset quiz to start
     */
    fun resetQuiz() {
        currentQuestionIndex = 0
        selectedAnswers = emptySet()
        isQuizCompleted = false
        userAnswers.clear()
        
        // Reshuffle questions for a new quiz session
        if (quizQuestions.isNotEmpty()) {
            quizQuestions = quizQuestions.shuffled()
        }
    }
    
    /**
     * Get user's score
     */
    fun getScore(): Int {
        var correctAnswers = 0
        userAnswers.forEach { (questionId, userAnswerIds) ->
            val question = quizQuestions.find { it.id == questionId }
            if (question != null) {
                val correctAnswerIds = question.correctAnswerIds.toSet()
                if (userAnswerIds == correctAnswerIds) {
                    correctAnswers++
                }
            }
        }
        return correctAnswers
    }
    
    /**
     * Check if current question is answered correctly
     */
    fun isCurrentQuestionCorrect(): Boolean {
        val question = currentQuestion ?: return false
        return selectedAnswers == question.correctAnswerIds.toSet()
    }
    
    /**
     * Get questions that were answered incorrectly
     */
    fun getIncorrectQuestions(): List<QuizQuestion> {
        return quizQuestions.filter { question ->
            val userSelected = userAnswers[question.id].orEmpty()
            userSelected != question.correctAnswerIds.toSet()
        }
    }
    
    /**
     * Clean up resources when ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        // Clear memory-intensive data
        userAnswers.clear()
        quizQuestions = emptyList()
    }
} 