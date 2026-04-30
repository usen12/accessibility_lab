package com.makhabatusen.access_lab_app.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.ui.components.UnifiedButton
import com.makhabatusen.access_lab_app.ui.components.UnifiedButtonType
import com.makhabatusen.access_lab_app.ui.components.UnifiedOutlinedButton

@Composable
fun QuizResultsScreen(
    viewModel: QuizViewModel,
    onRetakeQuiz: () -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val score = viewModel.getScore()
    val totalQuestions = viewModel.totalQuestions
    val percentage = if (totalQuestions > 0) {
        (score.toFloat() / totalQuestions.toFloat()) * 100
    } else 0f
    
    val isPassing = percentage >= 70f
    val incorrectQuestions = viewModel.getIncorrectQuestions()
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Step 1: Accessible Screen Title
            Text(
                text = stringResource(R.string.quiz_results_title),
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .semantics { heading() }
            )
            
                        // Step 2: Results Card with Enhanced Accessibility
            val resultsSummaryCd = stringResource(R.string.cd_quiz_results_summary)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = resultsSummaryCd
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Step 4: Enhanced Result Icon Accessibility
                    val iconContentDescription = if (isPassing) {
                        stringResource(R.string.cd_quiz_results_passed)
                    } else {
                        stringResource(R.string.cd_quiz_results_completed)
                    }
                    Icon(
                        imageVector = if (isPassing) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = iconContentDescription,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(bottom = 16.dp),
                        tint = if (isPassing) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    )
                    
                    // Result Title
                    Text(
                        text = if (isPassing) stringResource(R.string.quiz_results_congratulations) else stringResource(R.string.quiz_results_completed),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Step 2: Enhanced Score Display with Accessibility
                    val scoreContentDescription = stringResource(R.string.cd_quiz_results_score, score, totalQuestions)
                    Text(
                        text = "$score out of $totalQuestions correct",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .semantics {
                                contentDescription = scoreContentDescription
                            }
                    )
                    
                    // Enhanced Percentage with Accessibility
                    val percentageContentDescription = stringResource(R.string.cd_quiz_results_percentage, percentage)
                    Text(
                        text = "${String.format("%.1f", percentage)}%",
                        style = MaterialTheme.typography.displaySmall,
                        color = if (isPassing) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .semantics {
                                contentDescription = percentageContentDescription
                            }
                    )
                    
                    // Result Message
                    Text(
                        text = if (isPassing) {
                            stringResource(R.string.quiz_results_passed_message)
                        } else {
                            stringResource(R.string.quiz_results_failed_message)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Step 6 (Optional): Performance Progress Bar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                val progressContentDescription = stringResource(R.string.cd_quiz_results_progress, percentage)
                LinearProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .semantics {
                            contentDescription = progressContentDescription
                        },
                    color = if (isPassing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            }
            
            // Step 3: Topics to Review Section
            if (incorrectQuestions.isNotEmpty()) {
                val topicsReviewContentDescription = stringResource(R.string.cd_quiz_results_topics_review)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = topicsReviewContentDescription },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.quiz_results_topics_to_review),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.semantics { heading() }
                        )
                        
                        incorrectQuestions.forEach { question ->
                            val reviewQuestionContentDescription = stringResource(R.string.cd_quiz_results_review_question, question.questionText)
                            Text(
                                text = "• ${question.questionText}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.semantics {
                                    contentDescription = reviewQuestionContentDescription
                                }
                            )
                        }
                    }
                }
            }
            
            // Step 5: Action Buttons (Already accessible)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val retakeButtonText = stringResource(R.string.quiz_results_retake_button)
                val retakeButtonContentDescription = stringResource(R.string.cd_quiz_results_retake_button)
                val homeButtonText = stringResource(R.string.quiz_results_home_button)
                val homeButtonContentDescription = stringResource(R.string.cd_quiz_results_home_button)
                
                UnifiedButton(
                    onClick = onRetakeQuiz,
                    text = retakeButtonText,
                    contentDescription = retakeButtonContentDescription,
                    buttonType = UnifiedButtonType.PRIMARY,
                    maxWidth = true
                )
                
                UnifiedOutlinedButton(
                    onClick = onBackToHome,
                    text = homeButtonText,
                    contentDescription = homeButtonContentDescription,
                    maxWidth = true
                )
            }
        }
    }
} 