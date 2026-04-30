package com.makhabatusen.access_lab_app.domain.notes.usecase

import com.makhabatusen.access_lab_app.domain.notes.NotesRepository

class ObserveNotesUseCase(
    private val repository: NotesRepository
) {
    operator fun invoke() = repository.getAllNotes()
}