package com.makhabatusen.access_lab_app.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.ui.quiz.model.QuizQuestion
import com.makhabatusen.access_lab_app.ui.util.Constants

/**
 * Displays the appropriate question screen based on the current question.
 * This is the main entry point for question screen rendering.
 */
@Composable
fun QuestionScreen(
    currentQuestion: QuizQuestion?,
    modifier: Modifier = Modifier
) {
    val questionId = currentQuestion?.id ?: 0
    val screen = QuestionScreenFactory.createQuestionScreen(questionId, modifier)
    screen()
}

/**
 * First question screen - Accessibility features for visual impairments
 */
@Composable
fun FirstQuestionScreen(
    modifier: Modifier = Modifier
) {
    VisualAccessibilityScreen().Content(modifier = modifier, question = null)
}

/**
 * Second question screen - WCAG 2.1 principles
 */
@Composable
fun SecondQuestionScreen(
    modifier: Modifier = Modifier
) {
    WCAGPrinciplesScreen().Content(modifier = modifier, question = null)
}

/**
 * Default question screen for any other questions
 */
@Composable
fun DefaultQuestionScreen(
    modifier: Modifier = Modifier
) {
    DefaultQuestionScreenImpl.Content(modifier = modifier, question = null)
}

/**
 * Visual Accessibility Question Screen Implementation
 */
class VisualAccessibilityScreen : BaseQuestionScreen() {
    override val questionId: Int = 1
    override val screenType: QuestionScreenType = QuestionScreenType.VISUAL_ACCESSIBILITY
    override fun contentDescription(): String = "First question screen showing visual accessibility features"
    
    @Composable
    override fun ScreenContent(
        modifier: Modifier,
        question: QuizQuestion?
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(Constants.Spacing.LG.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.question_screen_first_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(Constants.Spacing.MD.dp))
            
            Text(
                text = stringResource(R.string.question_screen_visual_features),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(Constants.Spacing.LG.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = Constants.Elevation.MD.dp)
            ) {
                Column(
                    modifier = Modifier.padding(Constants.Spacing.MD.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.question_screen_visual_description),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(Constants.Spacing.SM.dp))
                    
                    Text(
                        text = stringResource(R.string.question_screen_features_include),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(Constants.Spacing.XS.dp))
                    
                    Text(
                        text = stringResource(R.string.question_screen_visual_features_list),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

/**
 * WCAG Principles Question Screen Implementation
 */
class WCAGPrinciplesScreen : BaseQuestionScreen() {
    override val questionId: Int = 2
    override val screenType: QuestionScreenType = QuestionScreenType.WCAG_PRINCIPLES
    override fun contentDescription(): String = "Second question screen showing WCAG 2.1 principles"
    
    @Composable
    override fun ScreenContent(
        modifier: Modifier,
        question: QuizQuestion?
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(Constants.Spacing.LG.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.question_screen_second_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(Constants.Spacing.MD.dp))
            
            Text(
                text = stringResource(R.string.question_screen_quiz_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(Constants.Spacing.LG.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = Constants.Elevation.MD.dp)
            ) {
                Column(
                    modifier = Modifier.padding(Constants.Spacing.MD.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.question_screen_wcag_description),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(Constants.Spacing.SM.dp))
                    
                    Text(
                        text = stringResource(R.string.question_screen_pour_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(Constants.Spacing.XS.dp))
                    
                    Text(
                        text = stringResource(R.string.question_screen_pour_list),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

/**
 * Default Question Screen Implementation
 */
object DefaultQuestionScreenImpl : BaseQuestionScreen() {
    override val questionId: Int = 0
    override val screenType: QuestionScreenType = QuestionScreenType.DEFAULT
    override fun contentDescription(): String = "Default question screen placeholder"
    
    @Composable
    override fun ScreenContent(
        modifier: Modifier,
        question: QuizQuestion?
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(Constants.Spacing.LG.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.question_screen_default_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(Constants.Spacing.MD.dp))
            
            Text(
                text = stringResource(R.string.question_screen_quiz_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(Constants.Spacing.LG.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = Constants.Elevation.MD.dp)
            ) {
                Column(
                    modifier = Modifier.padding(Constants.Spacing.MD.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.question_screen_placeholder_description),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(Constants.Spacing.SM.dp))
                    
                    Text(
                        text = stringResource(R.string.question_screen_placeholder_content),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
} 