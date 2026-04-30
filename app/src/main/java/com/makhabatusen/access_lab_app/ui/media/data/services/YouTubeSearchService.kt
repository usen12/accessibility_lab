package com.makhabatusen.access_lab_app.ui.media.data.services

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * YouTube API Service for fetching video data
 * Handles search, video details, and accessibility-focused content
 */
class YouTubeApiService(private val apiKey: String) {
    
    private val youtube: YouTube by lazy {
        YouTube.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            null
        )
            .setApplicationName("Access Lab App")
            .build()
    }
    
    /**
     * Search for accessibility-related videos
     */
    suspend fun searchAccessibilityVideos(
        query: String = "accessibility",
        maxResults: Long = 20
    ): Result<List<YouTubeVideo>> = withContext(Dispatchers.IO) {
        try {
            val searchRequest = youtube.search().list(listOf("snippet"))
                .setKey(apiKey)
                .setQ(query)
                .setType(listOf("video"))
                .setMaxResults(maxResults)
                .setVideoDuration("medium") // Prefer medium length videos
                .setVideoEmbeddable("true")
                .setRelevanceLanguage("en")
                .setOrder("relevance")
            
            val response: SearchListResponse = searchRequest.execute()
            val videos = response.items?.mapNotNull { searchResult ->
                searchResult.toYouTubeVideo()
            } ?: emptyList()
            
            Result.success(videos)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private fun SearchResult.toYouTubeVideo(): YouTubeVideo? {
        return try {
            YouTubeVideo(
                id = id.videoId,
                title = snippet.title,
                description = snippet.description,
                thumbnailUrl = snippet.thumbnails.high?.url ?: snippet.thumbnails.medium?.url,
                channelTitle = snippet.channelTitle,
                publishedAt = snippet.publishedAt.toString(),
                duration = "Unknown" // Will be updated with detailed info
            )
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * YouTube Video data class for search results
 */
data class YouTubeVideo(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String?,
    val channelTitle: String,
    val publishedAt: String,
    val duration: String
)
