package com.makhabatusen.access_lab_app.ui.notes

import androidx.compose.runtime.Composable
import com.makhabatusen.access_lab_app.data.notes.local.Note

@Composable
fun NotesScreenTestWrapper(
    notes: List<Note> = listOf(
        Note(id = 1, content = "Buy groceries", isProtected = false),
        Note(id = 2, content = "Secret note", isProtected = true)
    )
) {
    // Optionally, if your NotesScreen expects a ViewModel via viewModel(),
    // you could use a composition local or replace that logic entirely in the test build variant.
    NotesScreen(
        onEditNote = {} // no-op for test
    )
}
