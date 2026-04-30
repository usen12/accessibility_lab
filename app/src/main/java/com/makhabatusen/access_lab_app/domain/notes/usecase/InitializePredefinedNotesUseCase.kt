package com.makhabatusen.access_lab_app.domain.notes.usecase

import com.makhabatusen.access_lab_app.domain.notes.NotesRepository
import com.makhabatusen.access_lab_app.data.notes.seed.PredefinedNotesData

class InitializePredefinedNotesUseCase(
    private val repository: NotesRepository
) {
    suspend operator fun invoke() {
        val protectedCount = repository.getProtectedNotesCount()
        if (protectedCount == 0) {
            repository.insertNotes(PredefinedNotesData.predefinedNotes)
        }
    }
}