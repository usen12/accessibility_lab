package com.makhabatusen.access_lab_app.ui.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.ui.quiz.model.QuizAnswer
import com.makhabatusen.access_lab_app.ui.quiz.model.QuizQuestion
import com.makhabatusen.access_lab_app.ui.util.Constants
import com.makhabatusen.access_lab_app.ui.components.QuizTopBar
import com.makhabatusen.access_lab_app.ui.util.ResponsiveSpacing
import com.makhabatusen.access_lab_app.ui.util.SpacingContext
import com.makhabatusen.access_lab_app.ui.components.UnifiedButton
import com.makhabatusen.access_lab_app.ui.components.UnifiedButtonType
import com.makhabatusen.access_lab_app.ui.util.isLandscape
import com.makhabatusen.access_lab_app.ui.util.isTablet


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = viewModel(),
    onBackPressed: () -> Unit = {}
) {
    val isLandscape = isLandscape()
    val isTablet = isTablet()
    val context = LocalContext.current
    
    // Handle back navigation
    BackHandler {
        if (viewModel.currentQuestionIndex > 0) {
            // If not on first question, go to previous question
            viewModel.previousQuestion()
        } else {
            // If on first question, exit quiz
            onBackPressed()
        }
    }

    
    var totalDrag by remember { mutableStateOf(0f) }
    val canProceed = viewModel.canProceedToNext
    
    // Add rate limiting for gesture navigation
    var lastGestureTime by remember { mutableStateOf(0L) }
    val gestureCooldownMs = 500L // 500ms cooldown between gestures

    val draggableState = rememberDraggableState(
        onDelta = { delta ->
            totalDrag += delta
        }
    )

    val density = LocalDensity.current

    // Generate questions first
    val questions = generateQuizQuestions()
    
    // Remember the questions to prevent recreation on recomposition
    val allQuestions = remember {
        questions
    }

    // Initialize the quiz with questions immediately
    LaunchedEffect(Unit) {
        // Initialize the quiz with shuffled questions
        viewModel.initializeQuiz(allQuestions)
    }

    // Create content description for accessibility
    val quizPageContentDescription = stringResource(
        R.string.cd_quiz_page, 
        viewModel.currentQuestionIndex + 1, 
        viewModel.totalQuestions
    )

    // Show results if quiz is completed
    if (viewModel.isQuizCompleted) {
        QuizResultsScreen(
            viewModel = viewModel,
            onRetakeQuiz = { viewModel.resetQuiz() },
            onBackToHome = onBackPressed,
            modifier = modifier
        )
        return
    }

    Scaffold(
        topBar = { QuizTopBar() },
        bottomBar = {
            if (!isLandscape) {
                QuizFooter(
                    canProceedToNext = viewModel.canProceedToNext,
                    canFinishQuiz = viewModel.canFinishQuiz,
                    onNextQuestion = { viewModel.nextQuestion() }
                )
            }
        },
        modifier = modifier
            .semantics { 
                contentDescription = quizPageContentDescription
            }
            .draggable(
                orientation = Orientation.Horizontal,
                state = draggableState,
                onDragStarted = {
                    totalDrag = 0f
                },
                onDragStopped = {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastGestureTime < gestureCooldownMs) {
                        return@draggable // Ignore gesture if too soon after last one
                    }
                    
                    val swipeThresholdPx = with(density) { Constants.Gesture.SWIPE_THRESHOLD.toPx() }

                    if (canProceed && totalDrag < -swipeThresholdPx) {
                        lastGestureTime = currentTime
                        viewModel.nextQuestion()
                    } else if (totalDrag > swipeThresholdPx) {
                        lastGestureTime = currentTime
                        viewModel.previousQuestion()
                    }
                }
            )
    ) { innerPadding ->
        // Use LazyColumn for scrollable content, especially important in landscape
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT)),
            contentPadding = PaddingValues(
                vertical = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT)
            ),
            verticalArrangement = Arrangement.spacedBy(ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
        ) {
            // Progress indicator
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                ) {
                    LinearProgressIndicator(
                        progress = { 
                            if (viewModel.totalQuestions > 0) {
                                (viewModel.currentQuestionIndex + 1).toFloat() / viewModel.totalQuestions.toFloat()
                            } else {
                                0f
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Constants.Heights.PROGRESS_INDICATOR.dp)
                            .clip(RoundedCornerShape(Constants.CornerRadius.SM.dp))
                    )
                    Text(
                        text = if (viewModel.totalQuestions > 0) {
                            stringResource(
                                R.string.quiz_question_progress,
                                viewModel.currentQuestionIndex + 1,
                                viewModel.totalQuestions
                            )
                        } else {
                            stringResource(R.string.quiz_loading_question)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Question text
            item {
                QuizTaskText(
                    question = viewModel.currentQuestion,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Answer options
            item {
                AnswerOptions(
                    question = viewModel.currentQuestion,
                    selectedAnswers = viewModel.selectedAnswers,
                    onAnswerToggle = { viewModel.toggleAnswer(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Add extra space at the bottom for better scrolling experience
            item {
                Spacer(modifier = Modifier.height(ResponsiveSpacing.getElementSpacing(SpacingContext.MEDIA)))
            }
        }
    }
}

@Composable
private fun QuizTaskText(
    question: QuizQuestion?,
    modifier: Modifier = Modifier
) {
    // Get string resources for content descriptions
    val questionText = question?.questionText ?: stringResource(R.string.quiz_loading_question)
    val questionLabel = stringResource(R.string.quiz_question_label, question?.id ?: 0)
    val questionTextCd = stringResource(R.string.cd_quiz_question_text, questionText)
    
    Card(
        modifier = modifier.semantics { 
            contentDescription = questionTextCd
        },
        elevation = CardDefaults.cardElevation(defaultElevation = Constants.Elevation.SM.dp)
    ) {
        Column(
            modifier = Modifier.padding(ResponsiveSpacing.getElementSpacing(SpacingContext.FORM)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = questionLabel,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
            )
            
            Text(
                text = questionText,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { 
                    contentDescription = questionTextCd
                }
            )
        }
    }
}

@Composable
private fun AnswerOptions(
    question: QuizQuestion?,
    selectedAnswers: Set<Int>,
    onAnswerToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Constants.Spacing.SM.dp)
    ) {
        question?.answers?.forEach { answer ->
            val isSelected = selectedAnswers.contains(answer.id)
            val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
            val answerCd = stringResource(
                R.string.cd_quiz_answer_option_with_state, 
                answer.text, 
                if (isSelected) stringResource(R.string.cd_quiz_answer_selected) else stringResource(R.string.cd_quiz_answer_not_selected)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Constants.CornerRadius.LG.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(Constants.CornerRadius.LG.dp))
                    .background(backgroundColor)
                    .clickable { onAnswerToggle(answer.id) }
                    .padding(
                        horizontal = Constants.Spacing.LG.dp,
                        vertical = Constants.Spacing.MD.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null,
                )
                Spacer(modifier = Modifier.width(Constants.Spacing.MD.dp))
                Text(
                    text = answer.text,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.semantics {
                        contentDescription = answerCd
                    }
                )
            }
        }
    }
}

@Composable
fun QuizFooter(
    canProceedToNext: Boolean,
    canFinishQuiz: Boolean,
    onNextQuestion: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = Constants.Elevation.XL.dp
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = Constants.Spacing.LG.dp,
                vertical = Constants.Spacing.SM.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UnifiedButton(
                onClick = onNextQuestion,
                text = if (canFinishQuiz) {
                    stringResource(R.string.quiz_finish_quiz)
                } else {
                    stringResource(R.string.quiz_next_question)
                },
                enabled = canProceedToNext,
                contentDescription = if (canFinishQuiz) {
                    stringResource(R.string.cd_quiz_finish_button)
                } else {
                    stringResource(R.string.cd_quiz_next_button)
                },
                buttonType = UnifiedButtonType.PRIMARY,
                maxWidth = true
            )

            if (!canProceedToNext) {
                Spacer(modifier = Modifier.height(Constants.Spacing.XS.dp))
                Text(
                    text = stringResource(R.string.quiz_select_answer_hint_long),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun generateQuizQuestions(): List<QuizQuestion> {
    // Create True/False questions with stable IDs
    val trueFalseQuestions = listOf(
        QuizQuestion(
            id = 1,
            questionText = stringResource(R.string.quiz_question_1),
            answers = listOf(
                QuizAnswer(id = 1, text = stringResource(R.string.quiz_answer_true), isCorrect = true),
                QuizAnswer(id = 2, text = stringResource(R.string.quiz_answer_false), isCorrect = false)
            ),
            correctAnswerIds = listOf(1)
        ),
        QuizQuestion(
            id = 2,
            questionText = stringResource(R.string.quiz_question_2),
            answers = listOf(
                QuizAnswer(id = 1, text = stringResource(R.string.quiz_answer_true), isCorrect = false),
                QuizAnswer(id = 2, text = stringResource(R.string.quiz_answer_false), isCorrect = true)
            ),
            correctAnswerIds = listOf(2)
        ),
        QuizQuestion(
            id = 3,
            questionText = stringResource(R.string.quiz_question_3),
            answers = listOf(
                QuizAnswer(id = 1, text = stringResource(R.string.quiz_answer_true), isCorrect = false),
                QuizAnswer(id = 2, text = stringResource(R.string.quiz_answer_false), isCorrect = true)
            ),
            correctAnswerIds = listOf(2)
        ),
        QuizQuestion(
            id = 4,
            questionText = stringResource(R.string.quiz_question_4),
            answers = listOf(
                QuizAnswer(id = 1, text = stringResource(R.string.quiz_answer_true), isCorrect = false),
                QuizAnswer(id = 2, text = stringResource(R.string.quiz_answer_false), isCorrect = true)
            ),
            correctAnswerIds = listOf(2)
        ),
        QuizQuestion(
            id = 5,
            questionText = stringResource(R.string.quiz_question_5),
            answers = listOf(
                QuizAnswer(id = 1, text = stringResource(R.string.quiz_answer_true), isCorrect = true),
                QuizAnswer(id = 2, text = stringResource(R.string.quiz_answer_false), isCorrect = false)
            ),
            correctAnswerIds = listOf(1)
        ),
        QuizQuestion(
            id = 6,
            questionText = stringResource(R.string.quiz_question_6),
            answers = listOf(
                QuizAnswer(id = 1, text = stringResource(R.string.quiz_answer_true), isCorrect = true),
                QuizAnswer(id = 2, text = stringResource(R.string.quiz_answer_false), isCorrect = false)
            ),
            correctAnswerIds = listOf(1)
        ),
        QuizQuestion(
            id = 7,
            questionText = stringResource(R.string.quiz_question_7),
            answers = listOf(
                QuizAnswer(id = 1, text = stringResource(R.string.quiz_answer_true), isCorrect = false),
                QuizAnswer(id = 2, text = stringResource(R.string.quiz_answer_false), isCorrect = true)
            ),
            correctAnswerIds = listOf(2)
        )
    )
    
    // Create Multiple Choice questions with stable IDs
    val multipleChoiceQuestions = listOf(
        QuizQuestion(
            id = 8,
            questionText = stringResource(R.string.quiz_question_8),
            answers = listOf(
                QuizAnswer(
                    id = 1,
                    text = stringResource(R.string.quiz_question_8_option_a),
                    isCorrect = false
                ),
                QuizAnswer(
                    id = 2,
                    text = stringResource(R.string.quiz_question_8_option_b),
                    isCorrect = true
                ),
                QuizAnswer(
                    id = 3,
                    text = stringResource(R.string.quiz_question_8_option_c),
                    isCorrect = true
                ),
                QuizAnswer(
                    id = 4,
                    text = stringResource(R.string.quiz_question_8_option_d),
                    isCorrect = true
                )
            ),
            correctAnswerIds = listOf(2, 3, 4)
        ),
        QuizQuestion(
            id = 9,
            questionText = stringResource(R.string.quiz_question_9),
            answers = listOf(
                QuizAnswer(
                    id = 1,
                    text = stringResource(R.string.quiz_question_9_option_a),
                    isCorrect = false
                ),
                QuizAnswer(
                    id = 2,
                    text = stringResource(R.string.quiz_question_9_option_b),
                    isCorrect = false
                ),
                QuizAnswer(
                    id = 3,
                    text = stringResource(R.string.quiz_question_9_option_c),
                    isCorrect = true
                ),
                QuizAnswer(
                    id = 4,
                    text = stringResource(R.string.quiz_question_9_option_d),
                    isCorrect = true
                )
            ),
            correctAnswerIds = listOf(3, 4)
        ),
        QuizQuestion(
            id = 10,
            questionText = stringResource(R.string.quiz_question_10),
            answers = listOf(
                QuizAnswer(
                    id = 1,
                    text = stringResource(R.string.quiz_question_10_option_a),
                    isCorrect = false
                ),
                QuizAnswer(
                    id = 2,
                    text = stringResource(R.string.quiz_question_10_option_b),
                    isCorrect = true
                ),
                QuizAnswer(
                    id = 3,
                    text = stringResource(R.string.quiz_question_10_option_c),
                    isCorrect = true
                ),
                QuizAnswer(
                    id = 4,
                    text = stringResource(R.string.quiz_question_10_option_d),
                    isCorrect = true
                )
            ),
            correctAnswerIds = listOf(2, 3, 4)
        ),
        QuizQuestion(
            id = 11,
            questionText = stringResource(R.string.quiz_question_11),
            answers = listOf(
                QuizAnswer(
                    id = 1,
                    text = stringResource(R.string.quiz_question_11_option_a),
                    isCorrect = true
                ),
                QuizAnswer(
                    id = 2,
                    text = stringResource(R.string.quiz_question_11_option_b),
                    isCorrect = false
                ),
                QuizAnswer(
                    id = 3,
                    text = stringResource(R.string.quiz_question_11_option_c),
                    isCorrect = false
                ),
                QuizAnswer(
                    id = 4,
                    text = stringResource(R.string.quiz_question_11_option_d),
                    isCorrect = false
                )
            ),
            correctAnswerIds = listOf(1)
        ),
        QuizQuestion(
            id = 12,
            questionText = stringResource(R.string.quiz_question_12),
            answers = listOf(
                QuizAnswer(
                    id = 1,
                    text = stringResource(R.string.quiz_question_12_option_a),
                    isCorrect = false
                ),
                QuizAnswer(
                    id = 2,
                    text = stringResource(R.string.quiz_question_12_option_b),
                    isCorrect = false
                ),
                QuizAnswer(
                    id = 3,
                    text = stringResource(R.string.quiz_question_12_option_c),
                    isCorrect = true
                ),
                QuizAnswer(
                    id = 4,
                    text = stringResource(R.string.quiz_question_12_option_d),
                    isCorrect = false
                )
            ),
            correctAnswerIds = listOf(3)
        ),
        QuizQuestion(
            id = 13,
            questionText = stringResource(R.string.quiz_question_13),
            answers = listOf(
                QuizAnswer(
                    id = 1,
                    text = stringResource(R.string.quiz_question_13_option_a),
                    isCorrect = true
                ),
                QuizAnswer(
                    id = 2,
                    text = stringResource(R.string.quiz_question_13_option_b),
                    isCorrect = true
                ),
                QuizAnswer(
                    id = 3,
                    text = stringResource(R.string.quiz_question_13_option_c),
                    isCorrect = false
                ),
                QuizAnswer(
                    id = 4,
                    text = stringResource(R.string.quiz_question_13_option_d),
                    isCorrect = true
                )
            ),
            correctAnswerIds = listOf(1, 2, 4)
        ),
        QuizQuestion(
            id = 14,
            questionText = stringResource(R.string.quiz_question_14),
            answers = listOf(
                QuizAnswer(
                    id = 1,
                    text = stringResource(R.string.quiz_question_14_option_a),
                    isCorrect = true
                ),
                QuizAnswer(
                    id = 2,
                    text = stringResource(R.string.quiz_question_14_option_b),
                    isCorrect = false
                ),
                QuizAnswer(
                    id = 3,
                    text = stringResource(R.string.quiz_question_14_option_c),
                    isCorrect = true
                ),
                QuizAnswer(
                    id = 4,
                    text = stringResource(R.string.quiz_question_14_option_d),
                    isCorrect = false
                )
            ),
            correctAnswerIds = listOf(1, 3)
        ),
        QuizQuestion(
            id = 15,
            questionText = stringResource(R.string.quiz_question_15),
            answers = listOf(
                QuizAnswer(
                    id = 1,
                    text = stringResource(R.string.quiz_question_15_option_a),
                    isCorrect = false
                ),
                QuizAnswer(
                    id = 2,
                    text = stringResource(R.string.quiz_question_15_option_b),
                    isCorrect = true
                ),
                QuizAnswer(
                    id = 3,
                    text = stringResource(R.string.quiz_question_15_option_c),
                    isCorrect = false
                ),
                QuizAnswer(
                    id = 4,
                    text = stringResource(R.string.quiz_question_15_option_d),
                    isCorrect = false
                )
            ),
            correctAnswerIds = listOf(2)
        ),
        QuizQuestion(
            id = 16,
            questionText = stringResource(R.string.quiz_question_16),
            answers = listOf(
                QuizAnswer(
                    id = 1,
                    text = stringResource(R.string.quiz_question_16_option_a),
                    isCorrect = false
                ),
                QuizAnswer(
                    id = 2,
                    text = stringResource(R.string.quiz_question_16_option_b),
                    isCorrect = false
                ),
                QuizAnswer(
                    id = 3,
                    text = stringResource(R.string.quiz_question_16_option_c),
                    isCorrect = true
                ),
                QuizAnswer(
                    id = 4,
                    text = stringResource(R.string.quiz_question_16_option_d),
                    isCorrect = true
                )
            ),
            correctAnswerIds = listOf(3, 4)
        )
    )
    
    // Combine all questions
    return trueFalseQuestions + multipleChoiceQuestions
}

