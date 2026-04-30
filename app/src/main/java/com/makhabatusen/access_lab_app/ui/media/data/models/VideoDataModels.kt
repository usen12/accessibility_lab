package com.makhabatusen.access_lab_app.ui.media.data.models

data class VideoPlayerState(
    val isPlaying: Boolean = false,
    val isFullscreen: Boolean = false, // Kept for compatibility but fullscreen functionality is disabled
    val showControls: Boolean = true,
    val currentPosition: Float = 0f,   // in seconds
    val duration: Float = 0f,          // in seconds
    val volume: Float = 1f, 
    val isMuted: Boolean = false,
    val isCaptionsEnabled: Boolean = false, // Default to disabled
    val hasSubtitles: Boolean = false,
    val currentSubtitleText: String? = null,
    val subtitleAlpha: Float = 1.0f,
    // Additional state for new features:
    val isScrubbing: Boolean = false,
    val scrubPosition: Float = 0f,
    val showPlayPauseOverlay: Boolean = false,
    // Caption language support
    val selectedCaptionLanguage: String = "en", // Default to English
    val availableCaptionLanguages: List<CaptionLanguage> = emptyList(),
    // Interaction tracking for improved auto-hide
    val lastUserInteraction: Long = 0L, // Timestamp of last user interaction
    val isUserInteracting: Boolean = false, // Whether user is currently interacting
    val autoHideEnabled: Boolean = true // Whether auto-hide is enabled
)

data class VideoItem(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val duration: String,
    val views: String,
    val videoUrl: String = "",
    val captionsUrl: String = "", // Legacy field for backward compatibility
    val captions: List<CaptionLanguage> = emptyList() // New field for multiple languages
)

/**
 * Represents a caption language option
 */
data class CaptionLanguage(
    val languageCode: String, // e.g., "en", "de"
    val displayName: String,  // e.g., "English", "Deutsch"
    val captionsUrl: String   // URL to the WebVTT file
)