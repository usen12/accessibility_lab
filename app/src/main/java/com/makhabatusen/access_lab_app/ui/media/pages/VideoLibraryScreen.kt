package com.makhabatusen.access_lab_app.ui.media.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.ui.media.components.browse.VideoCard
import com.makhabatusen.access_lab_app.ui.media.components.player.AccessibleVideoPlayer
import com.makhabatusen.access_lab_app.ui.media.components.player.SimpleYouTubePlayer
import com.makhabatusen.access_lab_app.ui.media.data.models.VideoPlayerState
import com.makhabatusen.access_lab_app.ui.media.data.models.YoutubeVideoItem
import com.makhabatusen.access_lab_app.ui.media.data.models.CaptionLanguage
import com.makhabatusen.access_lab_app.ui.media.utils.SubtitleEntry
import com.makhabatusen.access_lab_app.ui.media.utils.SubtitleParser
import com.makhabatusen.access_lab_app.ui.media.viewmodels.VideoViewModel
import com.makhabatusen.access_lab_app.ui.util.Constants
import com.makhabatusen.access_lab_app.ui.components.LibraryTopBar
import com.makhabatusen.access_lab_app.ui.util.ResponsiveSpacing
import com.makhabatusen.access_lab_app.ui.util.SpacingContext
import com.makhabatusen.access_lab_app.ui.util.isLandscape
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.makhabatusen.access_lab_app.ui.media.data.models.VideoItem as MediaVideoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoLibraryScreen(
    modifier: Modifier = Modifier,
    viewModel: VideoViewModel = viewModel()
) {
    // Initialize string resources for accessibility
    val cdVideoLoading = stringResource(R.string.cd_video_loading)
    val cdVideoSearchField = stringResource(R.string.cd_video_search_field)
    val videoSearchIcon = stringResource(R.string.video_search_icon)
    val cdVideoClearSearch = stringResource(R.string.cd_video_clear_search)
    val videoClearSearch = stringResource(R.string.video_clear_search)
    val cdFeaturedVideoCard = stringResource(R.string.cd_featured_video_card)
    val cdVideoThumbnail = stringResource(R.string.cd_video_thumbnail)
    val cdVideoError = stringResource(R.string.cd_video_error)
    val cdVideoRetry = stringResource(R.string.cd_video_retry)
    val cdVideoEmpty = stringResource(R.string.cd_video_empty)
    val cdVideoEmptyMessage = stringResource(R.string.cd_video_empty_message)
    
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val isLandscapeMode = isLandscape()
    
    // Featured video data using string resources
    val featuredVideoData = createFeaturedVideoData()
    
    // Featured video player state
    var selectedFeaturedVideo by remember { mutableStateOf<MediaVideoItem?>(null) }
    var videoPlayerState by remember { mutableStateOf(VideoPlayerState(duration = 100f, isCaptionsEnabled = false, selectedCaptionLanguage = "en")) }
    
    // Function to update video player state
    fun updateVideoPlayerState(update: VideoPlayerState.() -> VideoPlayerState) {
        videoPlayerState = videoPlayerState.update()
    }
    
    // Improved auto-hide logic with interaction tracking
    LaunchedEffect(videoPlayerState.isPlaying, videoPlayerState.isUserInteracting, videoPlayerState.showControls) {
        if (videoPlayerState.isPlaying && videoPlayerState.showControls && !videoPlayerState.isUserInteracting && videoPlayerState.autoHideEnabled) {
            // Start auto-hide timer only when playing, controls are visible, and user is not interacting
            delay(2000) // 2 second delay before hiding
            // Check again before hiding to ensure conditions haven't changed
            if (videoPlayerState.isPlaying && videoPlayerState.showControls && !videoPlayerState.isUserInteracting && videoPlayerState.autoHideEnabled) {
                updateVideoPlayerState { copy(showControls = false) }
            }
        }
    }
    
    // Subtitle state
    var subtitleEntries by remember { mutableStateOf<List<SubtitleEntry>>(emptyList()) }
    val context = LocalContext.current
    val subtitleParser = remember { SubtitleParser(context) }
    
    // Load subtitles when video is selected
    LaunchedEffect(selectedFeaturedVideo) {
        selectedFeaturedVideo?.let { video ->
            // Initialize available caption languages
            updateVideoPlayerState { 
                copy(availableCaptionLanguages = video.captions) 
            }
            
            // Load subtitles for the selected language (default to English)
            val selectedLanguage = videoPlayerState.selectedCaptionLanguage
            val captionLanguage = video.captions.find { it.languageCode == selectedLanguage } 
                ?: video.captions.firstOrNull()
            
            if (captionLanguage != null) {
                try {
                    val result = subtitleParser.parseWebVTTFromUrl(captionLanguage.captionsUrl)
                    result.fold(
                        onSuccess = { entries ->
                            subtitleEntries = entries
                            updateVideoPlayerState { copy(hasSubtitles = entries.isNotEmpty()) }
                        },
                        onFailure = { exception ->
                            subtitleEntries = emptyList()
                            updateVideoPlayerState { copy(hasSubtitles = false) }
                        }
                    )
                } catch (e: Exception) {
                    subtitleEntries = emptyList()
                    updateVideoPlayerState { copy(hasSubtitles = false) }
                }
            } else {
                subtitleEntries = emptyList()
                updateVideoPlayerState { copy(hasSubtitles = false) }
            }
        }
    }
    
        // Handle subtitle updates based on current position
    LaunchedEffect(videoPlayerState.currentPosition, videoPlayerState.isCaptionsEnabled) {
        if (videoPlayerState.isCaptionsEnabled && subtitleEntries.isNotEmpty()) {
            val currentTimeMs = (videoPlayerState.currentPosition * 1000).toLong()
            val subtitleDisplay = subtitleParser.getSubtitleTextWithFade(subtitleEntries, currentTimeMs)
            
            updateVideoPlayerState {
                copy(
                    currentSubtitleText = subtitleDisplay?.text,
                    subtitleAlpha = subtitleDisplay?.alpha ?: 1.0f
                )
            }
        } else {
            updateVideoPlayerState {
                copy(
                    currentSubtitleText = null,
                    subtitleAlpha = 1.0f
                )
            }
        }
    }
    
    // Handle caption language changes
    LaunchedEffect(videoPlayerState.selectedCaptionLanguage) {
        selectedFeaturedVideo?.let { video ->
            val captionLanguage = video.captions.find { it.languageCode == videoPlayerState.selectedCaptionLanguage }
            if (captionLanguage != null) {
                try {
                    val result = subtitleParser.parseWebVTTFromUrl(captionLanguage.captionsUrl)
                    result.fold(
                        onSuccess = { entries ->
                            subtitleEntries = entries
                            updateVideoPlayerState { copy(hasSubtitles = entries.isNotEmpty()) }
                        },
                        onFailure = { exception ->
                            subtitleEntries = emptyList()
                            updateVideoPlayerState { copy(hasSubtitles = false) }
                        }
                    )
                } catch (e: Exception) {
                    subtitleEntries = emptyList()
                    updateVideoPlayerState { copy(hasSubtitles = false) }
                }
            }
        }
    }
    
    // Handle orientation changes and sync with fullscreen state
    LaunchedEffect(Unit) {
        // This will be triggered when the composable is first launched
        // We can add orientation change detection here if needed
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                LibraryTopBar()
            }
        ) { paddingValues ->
            // Use LazyColumn for scrollable content, especially important in landscape
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    horizontal = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT),
                    vertical = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT)
                ),
                verticalArrangement = Arrangement.spacedBy(ResponsiveSpacing.getElementSpacing(SpacingContext.MEDIA))
            ) {
                // Featured Video Card
                item {
                    FeaturedVideoCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            selectedFeaturedVideo = featuredVideoData
                        }
                    )
                }

                // Search Header
                item {
                    SearchHeader(
                        searchQuery = uiState.searchQuery,
                        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                        onSearch = {
                            viewModel.searchVideos()
                            focusManager.clearFocus()
                        },
                        onClearSearch = {
                            viewModel.clearSearch()
                            focusRequester.requestFocus()
                        },
                        focusRequester = focusRequester,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Video Results - Handle different states
                when {
                    uiState.isLoading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.semantics {
                                        contentDescription = cdVideoLoading
                                    }
                                )
                            }
                        }
                    }
                    uiState.error != null -> {
                        item {
                            ErrorState(
                                error = uiState.error.orEmpty(),
                                onRetry = { viewModel.searchVideos() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    uiState.videos.isEmpty() -> {
                        item {
                            EmptyState(modifier = Modifier.fillMaxWidth())
                        }
                    }
                    else -> {
                        // Video results as individual items for better scrolling
                        items(
                            items = uiState.videos,
                            key = { it.id }
                        ) { video ->
                            VideoCard(
                                video = video,
                                onClick = { viewModel.onVideoClick(video) }
                            )
                        }
                    }
                }
            }
        }

        // Video Player Dialog
        uiState.selectedVideo?.let { video ->
            if (uiState.showVideoPlayer) {
                SimpleYouTubePlayer(
                    videoId = video.id,
                    videoTitle = video.title,
                    onDismiss = { viewModel.closeVideoPlayer() }
                )
            }
        }

        // Video player rendered fullscreen conditionally
        selectedFeaturedVideo?.let { video ->
            AccessibleVideoPlayer(
                video = video,
                videoPlayerState = videoPlayerState,
                onDismiss = {
                    // Orientation restoration is handled in AccessibleVideoPlayerComponent
                    selectedFeaturedVideo = null
                    videoPlayerState = VideoPlayerState(duration = 100f, isCaptionsEnabled = false, selectedCaptionLanguage = "en")
                    subtitleEntries = emptyList()
                },
                onPlayPause = {
                    val currentTime = System.currentTimeMillis()
                    updateVideoPlayerState { 
                        copy(
                            isPlaying = !isPlaying,
                            lastUserInteraction = currentTime,
                            isUserInteracting = true
                        ) 
                    }
                    // Reset interaction state after a short delay
                    scope.launch {
                        delay(500)
                        updateVideoPlayerState { copy(isUserInteracting = false) }
                    }
                },
                onSeek = { position ->
                    val currentTime = System.currentTimeMillis()
                    updateVideoPlayerState { 
                        copy(
                            currentPosition = position,
                            lastUserInteraction = currentTime,
                            isUserInteracting = true
                        ) 
                    }
                    // Reset interaction state after a short delay
                    scope.launch {
                        delay(500)
                        updateVideoPlayerState { copy(isUserInteracting = false) }
                    }
                },
                onVolumeChange = { volume ->
                    val currentTime = System.currentTimeMillis()
                    updateVideoPlayerState { 
                        copy(
                            volume = volume,
                            lastUserInteraction = currentTime,
                            isUserInteracting = true
                        ) 
                    }
                    // Reset interaction state after a short delay
                    scope.launch {
                        delay(500)
                        updateVideoPlayerState { copy(isUserInteracting = false) }
                    }
                },
                onToggleMute = {
                    val currentTime = System.currentTimeMillis()
                    updateVideoPlayerState { 
                        copy(
                            isMuted = !isMuted,
                            lastUserInteraction = currentTime,
                            isUserInteracting = true
                        ) 
                    }
                    // Reset interaction state after a short delay
                    scope.launch {
                        delay(500)
                        updateVideoPlayerState { copy(isUserInteracting = false) }
                    }
                },
                onToggleCaptions = {
                    val currentTime = System.currentTimeMillis()
                    updateVideoPlayerState { 
                        copy(
                            isCaptionsEnabled = !isCaptionsEnabled,
                            lastUserInteraction = currentTime,
                            isUserInteracting = true
                        ) 
                    }
                    // Reset interaction state after a short delay
                    scope.launch {
                        delay(500)
                        updateVideoPlayerState { copy(isUserInteracting = false) }
                    }
                },
                onCaptionLanguageChange = { languageCode ->
                    val currentTime = System.currentTimeMillis()
                    updateVideoPlayerState { 
                        copy(
                            selectedCaptionLanguage = languageCode,
                            lastUserInteraction = currentTime,
                            isUserInteracting = true
                        ) 
                    }
                    // Reset interaction state after a short delay
                    scope.launch {
                        delay(500)
                        updateVideoPlayerState { copy(isUserInteracting = false) }
                    }
                },
                onToggleFullscreen = {
                    val currentTime = System.currentTimeMillis()
                    updateVideoPlayerState { 
                        copy(
                            isFullscreen = !isFullscreen,
                            lastUserInteraction = currentTime,
                            isUserInteracting = true
                        ) 
                    }
                    // Reset interaction state after a short delay
                    scope.launch {
                        delay(500)
                        updateVideoPlayerState { copy(isUserInteracting = false) }
                    }
                },
                onShowControls = { show ->
                    val currentTime = System.currentTimeMillis()
                    updateVideoPlayerState { 
                        copy(
                            showControls = show,
                            lastUserInteraction = currentTime,
                            isUserInteracting = true
                        ) 
                    }
                    // Reset interaction state after a short delay
                    scope.launch {
                        delay(500) // Consider interaction active for 500ms
                        updateVideoPlayerState { copy(isUserInteracting = false) }
                    }
                },
                onHideControlsAfterDelay = {
                    // This callback is no longer needed as auto-hide is handled by LaunchedEffect
                },
                onUpdatePosition = { position ->
                    updateVideoPlayerState { copy(currentPosition = position) }
                },
                onUpdateDuration = { duration ->
                    updateVideoPlayerState { copy(duration = duration) }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClearSearch: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    // Initialize string resources for accessibility
    val cdVideoSearchField = stringResource(R.string.cd_video_search_field)
    val videoSearchIcon = stringResource(R.string.video_search_icon)
    val cdVideoClearSearch = stringResource(R.string.cd_video_clear_search)
    val videoClearSearch = stringResource(R.string.video_clear_search)
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Search TextField
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .semantics {
                        contentDescription = cdVideoSearchField
                    },
                placeholder = {
                    Text(stringResource(R.string.video_search_placeholder))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = videoSearchIcon
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = onClearSearch,
                            modifier = Modifier
                                .size(Constants.Heights.ICON_BUTTON_MIN.dp)
                                .semantics {
                                    contentDescription = cdVideoClearSearch
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = videoClearSearch
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { onSearch() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

@Composable
private fun FeaturedVideoCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // Initialize string resources for accessibility
    val cdFeaturedVideoCard = stringResource(R.string.cd_featured_video_card)
    // Create a sample video for the featured card
    val featuredVideo = YoutubeVideoItem(
        id = "GRV1kucMqIo",
        title = stringResource(R.string.featured_video_fallback_title),
        description = stringResource(R.string.featured_video_description),
        thumbnailUrl = "https://img.youtube.com/vi/GRV1kucMqIo/hqdefault.jpg",
        channelTitle = stringResource(R.string.accessibility_lab_channel),
        publishedAt = "2024-01-15T10:00:00Z",
        duration = stringResource(R.string.featured_video_duration),
        viewCount = stringResource(R.string.featured_video_views)
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
                    .semantics {
            contentDescription = cdFeaturedVideoCard
        },
        shape = RoundedCornerShape(Constants.CornerRadius.LG.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Constants.Elevation.MD.dp
        )
    ) {
        Column {
            // Thumbnail
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = Constants.CornerRadius.LG.dp,
                            topEnd = Constants.CornerRadius.LG.dp
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_media),
                    contentDescription = stringResource(R.string.cd_video_thumbnail),
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                // Duration badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT)),
                    color = Color.Black.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(Constants.CornerRadius.SM.dp)
                ) {
                    Text(
                        text = featuredVideo.duration,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(
                            horizontal = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT),
                            vertical = 2.dp
                        )
                    )
                }
            }

            // Video info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ResponsiveSpacing.getElementSpacing(SpacingContext.FORM))
            ) {
                Text(
                    text = if (featuredVideo.title.isNullOrBlank()) stringResource(R.string.featured_video_fallback_title) else featuredVideo.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT)))

                Text(
                    text = featuredVideo.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT)))

                Text(
                    text = featuredVideo.viewCount,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}



@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Initialize string resources for accessibility
    val cdVideoError = stringResource(R.string.cd_video_error)
    val cdVideoRetry = stringResource(R.string.cd_video_retry)
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
        ) {
                            Text(
                    text = stringResource(R.string.video_error_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.semantics {
                        contentDescription = cdVideoError
                    }
                )
            
            Spacer(modifier = Modifier.height(ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT)))
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT)))
            
                            Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .height(Constants.Heights.BUTTON_STANDARD.dp)
                        .semantics {
                            contentDescription = cdVideoRetry
                        }
                ) {
                Text(stringResource(R.string.video_retry_button))
            }
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier
) {
    // Initialize string resources for accessibility
    val cdVideoEmpty = stringResource(R.string.cd_video_empty)
    val cdVideoEmptyMessage = stringResource(R.string.cd_video_empty_message)
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
        ) {
            Text(
                text = stringResource(R.string.video_empty_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.semantics {
                    contentDescription = cdVideoEmpty
                }
            )
            
            Spacer(modifier = Modifier.height(ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT)))
            
            Text(
                text = stringResource(R.string.video_empty_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.semantics {
                    contentDescription = cdVideoEmptyMessage
                }
            )
        }
    }
}

@Composable
private fun createFeaturedVideoData(): MediaVideoItem {
    return MediaVideoItem(
        id = "accessible_media",
        title = stringResource(R.string.featured_video_title),
        description = stringResource(R.string.featured_video_description),
        thumbnailUrl = "https://img.youtube.com/vi/GRV1kucMqIo/hqdefault.jpg",
        duration = stringResource(R.string.featured_video_duration),
        views = stringResource(R.string.featured_video_views),
        videoUrl = "android.resource://com.makhabatusen.access_lab_app/raw/accessible_media",
        captionsUrl = "android.resource://com.makhabatusen.access_lab_app/raw/accessible_media_text", // Legacy field
        captions = listOf(
            CaptionLanguage(
                languageCode = "en",
                displayName = stringResource(R.string.video_player_caption_language_english),
                captionsUrl = "android.resource://com.makhabatusen.access_lab_app/raw/accessible_media_text"
            ),
            CaptionLanguage(
                languageCode = "de",
                displayName = stringResource(R.string.video_player_caption_language_german),
                captionsUrl = "android.resource://com.makhabatusen.access_lab_app/raw/accessible_media_text_german"
            )
        )
    )
}