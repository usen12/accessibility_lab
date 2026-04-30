package com.makhabatusen.access_lab_app.core.util

import java.util.Locale

/**
 * Utility functions for time formatting and manipulation
 */
object TimeUtils {
    
    /**
     * Formats milliseconds into a human-readable time string (MM:SS or HH:MM:SS)
     * 
     * @param milliseconds The time in milliseconds
     * @return Formatted time string (e.g., "3:45" or "1:23:45")
     */
    fun formatTime(milliseconds: Long): String {
        if (milliseconds < 0) return "0:00"
        
        val totalSeconds = milliseconds / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        
        return when {
            hours > 0 -> String.format(Locale.ROOT, "%d:%02d:%02d", hours, minutes, seconds)
            else -> String.format(Locale.ROOT, "%d:%02d", minutes, seconds)
        }
    }
    
    /**
     * Formats seconds into a human-readable time string (MM:SS or HH:MM:SS)
     * 
     * @param seconds The time in seconds
     * @return Formatted time string (e.g., "3:45" or "1:23:45")
     */
    fun formatTimeFromSeconds(seconds: Long): String {
        return formatTime(seconds * 1000)
    }
    
    /**
     * Formats a duration in seconds to a human-readable string
     * 
     * @param durationSeconds Duration in seconds
     * @return Formatted duration string
     */
    fun formatDuration(durationSeconds: Long): String {
        return formatTimeFromSeconds(durationSeconds)
    }
} 