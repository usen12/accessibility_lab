package com.makhabatusen.access_lab_app.ui.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.data.notes.local.Note


@Composable
fun NotesCarousel(
    notes: List<Note>,
    onNoteClick: (noteId: Int?) -> Unit,
    onLearnMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    
    // Get string resources
    val carouselContainerCd = stringResource(R.string.cd_notes_carousel_container)
    val carouselTitleCd = stringResource(R.string.cd_notes_carousel_title)
    val carouselScrollableCd = stringResource(R.string.cd_notes_carousel_scrollable)
    val notesLoadingCd = stringResource(R.string.cd_notes_loading)
    val notesEmptyStateCd = stringResource(R.string.cd_notes_empty_state)
    val notesLearnMoreCd = stringResource(R.string.cd_notes_learn_more_button)
    val carouselTitle = stringResource(R.string.notes_carousel_title)
    val learnMoreText = stringResource(R.string.notes_learn_more)
    val emptyStateText = stringResource(R.string.notes_empty_state)
    val noteItemCd = stringResource(R.string.cd_note_item)
    val notePlaceholderCd = stringResource(R.string.cd_note_placeholder)
    
    // Simulate loading state for better UX
    LaunchedEffect(notes) {
        if (notes.isNotEmpty()) {
            isLoading = false
        }
    }
    
    val displayNotes = if (notes.isNotEmpty()) {
        notes.take(10)
    } else {
        List(3) { i -> Note(id = -1 * (i + 1), content = stringResource(R.string.notes_placeholder_text, i + 1)) }
    }
    
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { 
                contentDescription = carouselContainerCd
            }
    ) {
        // Section header
        Text(
            text = carouselTitle,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .semantics { 
                    heading()
                    contentDescription = carouselTitleCd
                }
        )
        
        // Loading state
        if (isLoading && notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.semantics { 
                        contentDescription = notesLoadingCd
                    }
                )
            }
        } else {
            // Notes carousel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .semantics { 
                        contentDescription = carouselScrollableCd
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                displayNotes.forEach { note ->
                    val noteContentDescription = if (notes.isNotEmpty()) {
                        "$noteItemCd ${note.content.take(30)}"
                    } else {
                        "$notePlaceholderCd ${note.content}"
                    }
                    
                    OutlinedButton(
                        onClick = { 
                            if (notes.isNotEmpty()) {
                                onNoteClick(note.id)
                            }
                        },
                        enabled = notes.isNotEmpty(),
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                            .width(160.dp)
                            .height(80.dp)
                            .semantics {
                                contentDescription = noteContentDescription
                            }
                    ) {
                        Text(
                            text = note.content,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                // Learn more button
                OutlinedButton(
                    onClick = onLearnMoreClick,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 12.dp)
                        .width(160.dp)
                        .height(80.dp)
                        .semantics { 
                            contentDescription = notesLearnMoreCd
                        }
                ) {
                    Text(
                        text = learnMoreText,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        
        // Empty state message
        if (!isLoading && notes.isEmpty()) {
            Text(
                text = emptyStateText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .semantics { 
                        contentDescription = notesEmptyStateCd
                    }
            )
        }
    }
} 