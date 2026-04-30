package com.makhabatusen.access_lab_app.ui.notes


import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.makhabatusen.access_lab_app.data.notes.local.Note
import com.makhabatusen.access_lab_app.data.notes.local.NoteDatabase
import com.makhabatusen.access_lab_app.domain.notes.NotesRepository
import com.makhabatusen.access_lab_app.data.notes.repository.NoteRepository
import com.makhabatusen.access_lab_app.domain.notes.usecase.DeleteNoteUseCase
import com.makhabatusen.access_lab_app.domain.notes.usecase.ObserveNotesUseCase
import com.makhabatusen.access_lab_app.domain.notes.usecase.InsertNoteUseCase
import com.makhabatusen.access_lab_app.domain.notes.usecase.UpdateNoteUseCase
import com.makhabatusen.access_lab_app.domain.notes.usecase.InitializePredefinedNotesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(
    private val observeNotesUseCase: ObserveNotesUseCase,
    private val insertNoteUseCase: InsertNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val initializePredefinedNotesUseCase: InitializePredefinedNotesUseCase
) : ViewModel() {

    val uiState: StateFlow<NotesUiState> = observeNotesUseCase()
        .map { notes ->
            NotesUiState(
                notes = notes,
                isLoading = false,
                errorMessage = null
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            NotesUiState(isLoading = true)
        )

    init {
        initializePredefinedNotes()
    }

    fun initializePredefinedNotes() {
        viewModelScope.launch {
            initializePredefinedNotesUseCase()
        }
    }


    fun addNote(content: String) {
        viewModelScope.launch {
            insertNoteUseCase(Note(content = content))
        }
    }

    /**
     * Saves a note with content validation.
     * Returns true if the note was saved successfully, false if the note is empty.
     */
    fun saveNote(content: String): Boolean {
        val trimmedContent = content.trim()
        if (trimmedContent.isEmpty()) {
            return false
        }
        addNote(trimmedContent)
        return true
    }


    fun updateNote(note: Note) {
        viewModelScope.launch {
            // Only allow updating non-protected notes
            if (!note.isProtected) {
               updateNoteUseCase(note)
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            // Only allow deleting non-protected notes
            if (!note.isProtected) {
                deleteNoteUseCase(note)
            }
        }
    }

    fun getNoteById(noteId: Int): Note? {
        return uiState.value.notes.find { it.id == noteId }
    }


    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
                val db = NoteDatabase.getDatabase(application)
                val repository: NotesRepository = NoteRepository(db.noteDao())

                val observeNotesUseCase = ObserveNotesUseCase(repository)
                val insertNoteUseCase = InsertNoteUseCase(repository)
                val updateNoteUseCase = UpdateNoteUseCase(repository)
                val deleteNoteUseCase = DeleteNoteUseCase(repository)
                val initializePredefinedNotesUseCase = InitializePredefinedNotesUseCase(repository)

                @Suppress("UNCHECKED_CAST")
                return NoteViewModel(
                    observeNotesUseCase = observeNotesUseCase,
                    insertNoteUseCase = insertNoteUseCase,
                    updateNoteUseCase = updateNoteUseCase,
                    deleteNoteUseCase = deleteNoteUseCase,
                    initializePredefinedNotesUseCase = initializePredefinedNotesUseCase
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}