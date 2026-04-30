package com.makhabatusen.access_lab_app.data.notes.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT COUNT(*) FROM notes WHERE isProtected = 1")
    suspend fun getProtectedNotesCount(): Int

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertNotes(notes: List<Note>)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}