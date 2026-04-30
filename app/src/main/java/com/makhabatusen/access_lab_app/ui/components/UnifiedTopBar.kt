package com.makhabatusen.access_lab_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.makhabatusen.access_lab_app.R

@Composable
fun UnifiedTopBar(
    title: String,
    topBarContentDescription: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(MaterialTheme.colorScheme.background)
            .semantics { contentDescription = topBarContentDescription },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = 16.dp)
                .semantics { heading() }
        )
    }
}

@Composable
fun HomeTopBar() {
    UnifiedTopBar(
        title = stringResource(R.string.home_topbar_title),
        topBarContentDescription = stringResource(R.string.cd_home_topbar)
    )
}

@Composable
fun NotesTopBar() {
    UnifiedTopBar(
        title = stringResource(R.string.notes_topbar_title),
        topBarContentDescription = stringResource(R.string.cd_notes_topbar)
    )
}

@Composable
fun LibraryTopBar() {
    UnifiedTopBar(
        title = stringResource(R.string.library_page_title),
        topBarContentDescription = stringResource(R.string.cd_library_topbar)
    )
}

@Composable
fun QuizTopBar() {
    UnifiedTopBar(
        title = stringResource(R.string.quiz_topbar_title),
        topBarContentDescription = stringResource(R.string.cd_quiz_topbar)
    )
}

@Composable
fun ProfileTopBar() {
    UnifiedTopBar(
        title = stringResource(R.string.settings_title),
                        topBarContentDescription = stringResource(R.string.cd_settings_topbar)
    )
} 