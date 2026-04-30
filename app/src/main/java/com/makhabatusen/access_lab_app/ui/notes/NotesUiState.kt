package com.makhabatusen.access_lab_app.ui.notes

import com.makhabatusen.access_lab_app.data.notes.local.Note

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)