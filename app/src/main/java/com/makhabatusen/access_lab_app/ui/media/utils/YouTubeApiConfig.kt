package com.makhabatusen.access_lab_app.ui.media.utils

import com.makhabatusen.access_lab_app.BuildConfig

object YouTubeApiConfig {
    val API_KEY: String get() = BuildConfig.YOUTUBE_API_KEY

    // YouTube API endpoints
    const val BASE_URL = "https://www.googleapis.com/youtube/v3/"
    const val SEARCH_ENDPOINT = "search"
    const val VIDEOS_ENDPOINT = "videos"

    // Default search parameters
    const val DEFAULT_MAX_RESULTS = 20L
    const val DEFAULT_VIDEO_DURATION = "medium" // short, medium, long
    const val DEFAULT_ORDER = "relevance" // date, rating, relevance, title, videoCount, viewCount

    // Accessibility-focused search queries
    val ACCESSIBILITY_QUERIES = listOf(
        "accessibility",
        "web accessibility",
        "mobile accessibility",
        "screen reader",
        "assistive technology",
        "inclusive design",
        "WCAG guidelines",
        "accessibility testing",
        "voice over",
        "talkback"
    )

    /**
     * Get a random accessibility query for variety
     */
    fun getRandomAccessibilityQuery(): String {
        return ACCESSIBILITY_QUERIES.random()
    }

    /**
     * Check if API key is configured
     */
    fun isApiKeyConfigured(): Boolean = API_KEY.isNotEmpty()
}