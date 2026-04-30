package com.makhabatusen.access_lab_app.ui.notes

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.makhabatusen.access_lab_app.data.notes.local.Note
import com.makhabatusen.access_lab_app.ui.notes.NotesPage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Minimal fake ViewModel that mimics the real NoteViewModel API
class FakeNoteViewModel(
    notes: List<Note>
) : ViewModel() {

    private val _notes = MutableStateFlow(notes)
    val notes: StateFlow<List<Note>> = _notes

    fun openNewNotePage() {}
    fun deleteNote(note: Note) {}
}

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
