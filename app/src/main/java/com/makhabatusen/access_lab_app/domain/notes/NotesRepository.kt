package com.makhabatusen.access_lab_app.domain.notes

import com.makhabatusen.access_lab_app.data.notes.local.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getAllNotes(): Flow<List<Note>>

    suspend fun getProtectedNotesCount(): Int

    suspend fun insertNote(note: Note): Long

    suspend fun insertNotes(notes: List<Note>)

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(note: Note)
}