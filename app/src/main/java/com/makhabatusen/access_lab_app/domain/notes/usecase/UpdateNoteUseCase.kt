package com.makhabatusen.access_lab_app.domain.notes.usecase

import com.makhabatusen.access_lab_app.data.notes.local.Note
import com.makhabatusen.access_lab_app.domain.notes.NotesRepository

class UpdateNoteUseCase(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(note: Note) = repository.updateNote(note)
}