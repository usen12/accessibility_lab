package com.makhabatusen.access_lab_app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.ui.notes.NoteViewModel
import com.makhabatusen.access_lab_app.ui.components.HomeTopBar
import com.makhabatusen.access_lab_app.ui.util.ResponsiveSpacing
import com.makhabatusen.access_lab_app.ui.util.SpacingContext
import com.makhabatusen.access_lab_app.ui.components.UnifiedButton
import com.makhabatusen.access_lab_app.ui.components.UnifiedButtonType
import com.makhabatusen.access_lab_app.ui.components.UnifiedOutlinedButton

@Composable
fun HomeScreen(
    onStartQuiz: () -> Unit,
    onSeeResults: () -> Unit,
    onNoteClick: (noteId: Int?) -> Unit,
    onLearnMoreClick: () -> Unit
) {
    val application = LocalContext.current.applicationContext as android.app.Application
    val noteViewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory(application))
    val uiState by noteViewModel.uiState.collectAsStateWithLifecycle()

    // Get string resources for content descriptions
    val startQuizButtonCd = stringResource(R.string.cd_home_start_quiz_button)
    val seeResultsButtonCd = stringResource(R.string.cd_home_see_results_button)
    val accessLabImageCd = stringResource(R.string.cd_home_access_lab_image)
    val notesCarouselCd = stringResource(R.string.cd_home_notes_carousel)
    val actionButtonsCd = stringResource(R.string.cd_home_action_buttons)

    // Use responsive spacing system
    val maxContentWidth = ResponsiveSpacing.getMaxContentWidth()

    Scaffold(
        topBar = {
            HomeTopBar()
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = maxContentWidth)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Main illustration image
                Image(
                    painter = painterResource(id = R.drawable.access_lab),
                    contentDescription = accessLabImageCd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.6f)
                        .padding(
                            top = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION),
                            bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION)
                        )
                )

                // Notes Carousel section
                NotesCarousel(
                    notes = uiState.notes,
                    onNoteClick = onNoteClick,
                    onLearnMoreClick = onLearnMoreClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = notesCarouselCd
                        }
                )

                // Action buttons container
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(ResponsiveSpacing.getElementSpacing(SpacingContext.BUTTON)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION),
                            bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION)
                        )
                        .semantics { contentDescription = actionButtonsCd }
                ) {
                    // Start Quiz Button
                    UnifiedButton(
                        onClick = onStartQuiz,
                        text = stringResource(R.string.home_start_quiz_button),
                        contentDescription = startQuizButtonCd,
                        buttonType = UnifiedButtonType.PRIMARY,
                        maxWidth = true
                    )

                    // Settings Button
                    UnifiedOutlinedButton(
                        onClick = onSeeResults,
                        text = stringResource(R.string.home_see_results_button),
                        contentDescription = seeResultsButtonCd,
                        maxWidth = true
                    )
                }
            }
        }
    }
}