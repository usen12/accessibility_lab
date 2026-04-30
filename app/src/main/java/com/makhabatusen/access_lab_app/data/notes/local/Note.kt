package com.makhabatusen.access_lab_app.data.notes.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isProtected: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Note) return false
        return id == other.id && content == other.content && timestamp == other.timestamp && isProtected == other.isProtected
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + content.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + isProtected.hashCode()
        return result
    }
}