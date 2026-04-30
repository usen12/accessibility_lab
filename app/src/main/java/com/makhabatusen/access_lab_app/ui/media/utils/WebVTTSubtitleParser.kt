package com.makhabatusen.access_lab_app.ui.media.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.regex.Pattern

/**
 * Subtitle entry with timing and text
 */
data class SubtitleEntry(
    val startTime: Long, // milliseconds
    val endTime: Long,   // milliseconds
    val text: String
)

/**
 * Subtitle parser for WebVTT format
 */
class SubtitleParser(private val context: Context) {
    
    /**
     * Parse WebVTT subtitle file from URL or Android resource
     */
    suspend fun parseWebVTTFromUrl(url: String): Result<List<SubtitleEntry>> = withContext(Dispatchers.IO) {
        try {
            val content = if (url.startsWith("android.resource://")) {
                // Handle Android resource URLs
                val resourceName = url.substringAfterLast("/")
                val resourceId = context.resources.getIdentifier(
                    resourceName, "raw", context.packageName
                )
                if (resourceId != 0) {
                    context.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
                } else {
                    throw Exception("Resource not found: $resourceName")
                }
            } else {
                // Handle regular URLs
                val connection = URL(url).openConnection()
                val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
                val content = reader.readText()
                reader.close()
                content
            }
            
            val entries = parseWebVTTContent(content)
            Result.success(entries)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Parse WebVTT content string
     */
    private fun parseWebVTTContent(content: String): List<SubtitleEntry> {
        val entries = mutableListOf<SubtitleEntry>()
        val lines = content.split("\n")
        
        var i = 0
        while (i < lines.size) {
            val line = lines[i].trim()
            
            // Skip empty lines and WEBVTT header
            if (line.isEmpty() || line.startsWith("WEBVTT") || line.startsWith("NOTE")) {
                i++
                continue
            }
            
            // Look for timestamp line (format: 00:00:00.000 --> 00:00:00.000)
            val timestampPattern = Pattern.compile("(\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s*-->\\s*(\\d{2}:\\d{2}:\\d{2}\\.\\d{3})")
            val matcher = timestampPattern.matcher(line)
            
            if (matcher.find()) {
                val startTime = parseTimestamp(matcher.group(1) ?: continue)
                val endTime = parseTimestamp(matcher.group(2) ?: continue)
                
                // Collect subtitle text
                val subtitleText = StringBuilder()
                i++ // Move to next line
                
                while (i < lines.size && lines[i].trim().isNotEmpty()) {
                    if (subtitleText.isNotEmpty()) {
                        subtitleText.append("\n")
                    }
                    subtitleText.append(lines[i].trim())
                    i++
                }
                
                if (subtitleText.isNotEmpty()) {
                    entries.add(SubtitleEntry(startTime, endTime, subtitleText.toString()))
                }
            } else {
                i++
            }
        }
        
        return entries
    }
    
    /**
     * Parse timestamp string to milliseconds
     */
    private fun parseTimestamp(timestamp: String): Long {
        val parts = timestamp.split(":")
        val hours = parts[0].toLong()
        val minutes = parts[1].toLong()
        val secondsAndMillis = parts[2].split(".")
        val seconds = secondsAndMillis[0].toLong()
        val millis = secondsAndMillis[1].toLong()
        
        return (hours * 3600000) + (minutes * 60000) + (seconds * 1000) + millis
    }
    
    /**
     * Get subtitle text for current playback time
     */
    fun getSubtitleText(entries: List<SubtitleEntry>, currentTimeMs: Long): String? {
        return entries.find { entry ->
            currentTimeMs >= entry.startTime && currentTimeMs <= entry.endTime
        }?.text
    }
    
    /**
     * Get subtitle text for current playback time with fade effect
     */
    fun getSubtitleTextWithFade(entries: List<SubtitleEntry>, currentTimeMs: Long): SubtitleDisplay? {
        val entry = entries.find { entry ->
            currentTimeMs >= entry.startTime && currentTimeMs <= entry.endTime
        }
        
        return entry?.let {
            val fadeInDuration = 300L // 300ms fade in
            val fadeOutDuration = 300L // 300ms fade out
            
            val fadeInProgress = if (currentTimeMs - it.startTime < fadeInDuration) {
                (currentTimeMs - it.startTime).toFloat() / fadeInDuration
            } else {
                1.0f
            }
            
            val fadeOutProgress = if (it.endTime - currentTimeMs < fadeOutDuration) {
                (it.endTime - currentTimeMs).toFloat() / fadeOutDuration
            } else {
                1.0f
            }
            
            val alpha = minOf(fadeInProgress, fadeOutProgress)
            
            SubtitleDisplay(it.text, alpha)
        }
    }
}

/**
 * Subtitle display with alpha for fade effects
 */
data class SubtitleDisplay(
    val text: String,
    val alpha: Float
) 