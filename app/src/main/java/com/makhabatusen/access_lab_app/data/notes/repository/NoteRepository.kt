package com.makhabatusen.access_lab_app.data.notes.repository

import com.makhabatusen.access_lab_app.data.notes.local.Note
import com.makhabatusen.access_lab_app.data.notes.local.NoteDao
import com.makhabatusen.access_lab_app.domain.notes.NotesRepository

class NoteRepository(
    private val noteDao: NoteDao
) : NotesRepository {
    override fun getAllNotes() = noteDao.getAllNotes()

    override suspend fun getProtectedNotesCount() = noteDao.getProtectedNotesCount()

    override suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)

    override suspend fun insertNotes(notes: List<Note>) = noteDao.insertNotes(notes)

    override suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    override suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
}