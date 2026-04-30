package com.makhabatusen.access_lab_app.ui.media.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makhabatusen.access_lab_app.ui.media.data.models.YoutubeVideoItem
import com.makhabatusen.access_lab_app.ui.media.data.services.YouTubeApiService
import com.makhabatusen.access_lab_app.ui.media.utils.YouTubeApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing video search functionality
 */
class VideoViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(VideoUiState())
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()
    
    private val youtubeApiService = YouTubeApiService(YouTubeApiConfig.API_KEY)
    
    init {
        // Load placeholder videos on initialization
        loadPlaceholderVideos()
    }
    
    /**
     * Update the search query
     */
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
    
    /**
     * Clear the search and show placeholder videos
     */
    fun clearSearch() {
        _uiState.update { 
            it.copy(
                searchQuery = "",
                videos = YoutubeVideoItem.getPlaceholderVideos(),
                isLoading = false,
                error = null
            )
        }
    }
    
    /**
     * Search for videos using the current query
     */
    fun searchVideos() {
        val query = _uiState.value.searchQuery.trim()
        
        if (query.isEmpty()) {
            loadPlaceholderVideos()
            return
        }
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                val result = youtubeApiService.searchAccessibilityVideos(
                    query = query,
                    maxResults = 20
                )
                
                result.fold(
                    onSuccess = { youtubeVideos ->
                        val youtubeVideoItems = youtubeVideos.map { youtubeVideo ->
                            YoutubeVideoItem.fromYouTubeVideo(youtubeVideo)
                        }
                        
                        _uiState.update { 
                            it.copy(
                                videos = youtubeVideoItems,
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Failed to search videos"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }
    
    /**
     * Handle video click - opens video player dialog
     */
    fun onVideoClick(video: YoutubeVideoItem) {
        _uiState.update { 
            it.copy(
                selectedVideo = video,
                showVideoPlayer = true
            )
        }
    }
    
    /**
     * Close video player dialog
     */
    fun closeVideoPlayer() {
        _uiState.update { 
            it.copy(
                selectedVideo = null,
                showVideoPlayer = false
            )
        }
    }
    
    /**
     * Load placeholder videos from the accessibility playlist
     */
    private fun loadPlaceholderVideos() {
        _uiState.update { 
            it.copy(
                videos = YoutubeVideoItem.getPlaceholderVideos(),
                isLoading = false,
                error = null
            )
        }
    }
    
    /**
     * Format view count for display
     */
    private fun formatViewCount(viewCount: Long): String {
        return when {
            viewCount >= 1_000_000 -> "${viewCount / 1_000_000}M views"
            viewCount >= 1_000 -> "${viewCount / 1_000}K views"
            else -> "$viewCount views"
        }
    }
}

/**
 * UI State for the Video page
 */
data class VideoUiState(
    val searchQuery: String = "",
    val videos: List<YoutubeVideoItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedVideo: YoutubeVideoItem? = null,
    val showVideoPlayer: Boolean = false
) 