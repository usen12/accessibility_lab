package com.makhabatusen.access_lab_app.ui.media.components.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.ui.media.data.models.VideoItem
import com.makhabatusen.access_lab_app.ui.media.data.models.VideoPlayerState
import com.makhabatusen.access_lab_app.ui.util.Constants
import com.makhabatusen.access_lab_app.core.util.TimeUtils.formatTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Enhanced accessible video player with scrubbing preview, play/pause overlay icons,
 * and full keyboard navigation support. Compliant with BITV 2.0 accessibility standards.
 */
@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AccessibleVideoPlayer(
    video: VideoItem,
    videoPlayerState: VideoPlayerState,
    onDismiss: () -> Unit,
    onPlayPause: () -> Unit,
    onSeek: (Float) -> Unit,                // called when user confirms a seek
    onVolumeChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onToggleCaptions: () -> Unit,
    onCaptionLanguageChange: (String) -> Unit = {}, // new callback for caption language changes
    onToggleFullscreen: () -> Unit,         // kept for compatibility but not used
    onShowControls: (Boolean) -> Unit,
    onHideControlsAfterDelay: () -> Unit,
    onUpdatePosition: (Float) -> Unit = {}, // updates current position in state
    onUpdateDuration: (Float) -> Unit = {}, // updates video duration in state
    modifier: Modifier = Modifier
) {
    // Initialize string resources for accessibility
    val videoPlayerEnterFullscreen = stringResource(R.string.video_player_enter_fullscreen)
    val videoPlayerExitFullscreen = stringResource(R.string.video_player_exit_fullscreen)
    val videoPlayerCcButton = stringResource(R.string.video_player_cc_button)
    val videoPlayerErrorMessage = stringResource(R.string.video_player_error_message)
    
    // Handle back navigation - dismiss video player and return to Library
    BackHandler {
        onDismiss()
    }
    val context = LocalContext.current
    val activity = context as? Activity
    
    // Play/Pause overlay icon state (local to this composable)
    var showOverlayIcon by remember { mutableStateOf(false) }
    var overlayIconRes by remember { mutableStateOf(R.drawable.ic_play) }
    
    // Lock orientation when player is opened - prevent device rotation from changing orientation
    LaunchedEffect(Unit) {
        activity?.let { act ->
            // Lock to current orientation when player opens
            val currentOrientation = act.requestedOrientation
            if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                // If no orientation is set, lock to portrait initially
                act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }
    
    // Watch for play/pause state changes to trigger overlay icon
    LaunchedEffect(videoPlayerState.isPlaying) {
        // Determine which icon to show based on the action taken
        overlayIconRes = if (videoPlayerState.isPlaying) {
            R.drawable.ic_pause   // icon to show when pause pressed
        } else {
            R.drawable.ic_play    // icon to show when play pressed
        }
        // Show icon briefly
        showOverlayIcon = true
        delay(800)  // show for 0.8s (can tweak between 500-1000ms)
        showOverlayIcon = false
    }
    
    // Handle fullscreen state changes and immersive mode
    LaunchedEffect(videoPlayerState.isFullscreen) {
        activity?.let { act ->
            if (videoPlayerState.isFullscreen) {
                // Enter fullscreen - lock to landscape and immersive mode
                act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                WindowCompat.setDecorFitsSystemWindows(act.window, false)
                val controller = WindowCompat.getInsetsController(act.window, act.window.decorView)
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = 
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                // Exit fullscreen - lock to portrait and exit immersive mode
                act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                WindowCompat.setDecorFitsSystemWindows(act.window, true)
                val controller = WindowCompat.getInsetsController(act.window, act.window.decorView)
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }
    
    // Show controls when paused, auto-hide is now handled by the parent component
    LaunchedEffect(videoPlayerState.isPlaying) {
        if (!videoPlayerState.isPlaying) {
            onShowControls(true)         // if paused, ensure controls are visible
        }
    }
    
    // Cleanup: restore system UI and orientation when player is dismissed
    DisposableEffect(Unit) {
        onDispose {
            activity?.let { act ->
                // Restore system UI when leaving fullscreen
                WindowCompat.setDecorFitsSystemWindows(act.window, true)
                val controller = WindowCompat.getInsetsController(act.window, act.window.decorView)
                controller.show(WindowInsetsCompat.Type.systemBars())
                // Restore default orientation behavior - allow device rotation again
                act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }
    
    // Main UI - Direct implementation without Dialog for proper fullscreen support
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black) // Use black background for proper fullscreen appearance
            // Capture all touch events to prevent interaction with elements underneath
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // Tapping anywhere on the screen shows controls and toggles play/pause
                        onShowControls(true)
                        onPlayPause()
                    }
                )
            }
            // Ensure this component captures all focus and prevents interaction with background
            .focusable(
                enabled = true,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        if (!videoPlayerState.isFullscreen) {
            // ** Portrait Mode UI - Centered and Fixed Position **
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)) {
                
                // Top bar: Back button and title (visible only when controls are shown)
                AnimatedVisibility(
                    visible = videoPlayerState.showControls,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(48.dp)
                                .semantics { contentDescription = "Close video player" }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back), 
                                contentDescription = null, 
                                tint = Color.White, 
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = video.title.ifBlank { "Untitled Video" },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }
                
                // Video display area - Centered vertically with increased width
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp) // Reduced padding for more width
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Black)
                        .align(Alignment.Center), // Center vertically
                    contentAlignment = Alignment.Center
                ) {
                    // The actual video playback surface and overlay content
                    VideoContentArea(
                        video = video,
                        videoPlayerState = videoPlayerState,
                        onPlayPause = onPlayPause,
                        onSeekChanged = { position ->   // preview scrubbing
                            // Update preview frame as user scrubs (throttle inside implementation)
                        },
                        onSeekFinished = { position ->
                            onSeek(position)            // commit seek on release
                        },
                        onVolumeChange = onVolumeChange,
                        onToggleMute = onToggleMute,
                        onUpdatePosition = onUpdatePosition,
                        onUpdateDuration = onUpdateDuration,
                        onShowControls = onShowControls
                    )
                    // Subtitle overlay (if captions enabled)
                    videoPlayerState.currentSubtitleText?.let { subtitle ->
                        if (videoPlayerState.isCaptionsEnabled) {
                            SubtitleOverlay(text = subtitle, alpha = 1.0f)
                        }
                    }
                    // Play/Pause center overlay icon
                    if (showOverlayIcon) {
                        Icon(
                            painter = painterResource(id = overlayIconRes),
                            contentDescription = null,  // decorative
                            modifier = Modifier.size(100.dp),
                            tint = Color.White
                        )
                    }
                }
                
                // Bottom Controls - Positioned closer to player, doesn't affect player position
                AnimatedVisibility(
                    visible = videoPlayerState.showControls,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    // Timeline slider + controls
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)) {
                        
                        // Timeline + duration Row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatTime((videoPlayerState.currentPosition * 1000).toLong()),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            // ** Scrubbing Slider with Preview **
                            var sliderWidthPx by remember { mutableStateOf(1) }
                            var previewBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
                            var isScrubbing by remember { mutableStateOf(false) }
                            // When scrubbing, launch a side effect to periodically fetch preview frames
                            if (isScrubbing) {
                                LaunchedEffect(videoPlayerState.currentPosition) {
                                    // Throttle frame extraction (e.g., 200ms delay)
                                    delay(200)
                                    val frameTimeUs = (videoPlayerState.currentPosition * 1000_000).toLong()
                                    // Use MediaMetadataRetriever or similar to get frame (on IO thread)
                                    withContext(Dispatchers.IO) {
                                        try {
                                            val retriever = MediaMetadataRetriever()
                                            retriever.setDataSource(context, Uri.parse(video.videoUrl))
                                            val bmp = retriever.getFrameAtTime(frameTimeUs, MediaMetadataRetriever.OPTION_CLOSEST)
                                            previewBitmap = bmp?.asImageBitmap()
                                            retriever.release()
                                        } catch (e: Exception) {
                                            // handle or log error
                                        }
                                    }
                                }
                            }
                            // Slider component
                            Slider(
                                value = videoPlayerState.currentPosition,
                                onValueChange = { pos ->
                                    // User is actively dragging
                                    isScrubbing = true
                                    onShowControls(true)               // keep controls visible
                                    // Update scrub position in state (for preview)
                                    onUpdatePosition(pos)              // update current position live
                                    // Also store scrub position if separate from actual (if needed)
                                },
                                onValueChangeFinished = {
                                    // User finished seeking
                                    isScrubbing = false
                                    onSeek(videoPlayerState.currentPosition)  // commit seek to this position
                                },
                                valueRange = 0f..videoPlayerState.duration,
                                modifier = Modifier
                                    .weight(6f)
                                    .onGloballyPositioned { coords ->
                                        sliderWidthPx = coords.size.width
                                    },
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                            )
                            // Preview thumbnail image (appears above thumb during scrubbing)
                            val currentPreviewBitmap = previewBitmap
                            if (isScrubbing && currentPreviewBitmap != null) {
                                val fraction = if (videoPlayerState.duration > 0)
                                    videoPlayerState.currentPosition / videoPlayerState.duration
                                else 0f
                                val previewSize = 100.dp
                                val offsetXPx = (fraction * sliderWidthPx) - with(LocalDensity.current) { previewSize.toPx() / 2 }
                                Image(
                                    bitmap = currentPreviewBitmap,
                                    contentDescription = "Preview frame",
                                    modifier = Modifier
                                        .offset {
                                            IntOffset(x = offsetXPx.toInt(), y = -130)
                                            // y = -130 px approx above slider; adjust as needed
                                        }
                                        .size(previewSize)
                                        .background(
                                            MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                )
                            }
                            Text(
                                text = formatTime((videoPlayerState.duration * 1000).toLong()),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Bottom icon controls row (Volume, CC, Fullscreen)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Volume/Mute control
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = onToggleMute,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .semantics {
                                            contentDescription =
                                                if (videoPlayerState.isMuted) "Unmute audio" else "Mute audio"
                                        }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_sound_effects), 
                                        contentDescription = null, 
                                        tint = Color.White, 
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Slider(
                                    value = if (videoPlayerState.isMuted) 0f else videoPlayerState.volume,
                                    onValueChange = { vol -> 
                                        onVolumeChange(vol)
                                        onShowControls(true) // Keep controls visible during volume adjustment
                                    },
                                    onValueChangeFinished = {
                                        // Volume change finished - interaction tracking is handled by the callback
                                    },
                                    valueRange = 0f..1f,
                                    modifier = Modifier
                                        .width(70.dp)
                                        .semantics {
                                            contentDescription =
                                                "Volume control, ${(videoPlayerState.volume * 100).toInt()}%"
                                        },
                                    colors = SliderDefaults.colors(
                                        thumbColor = MaterialTheme.colorScheme.primary,
                                        activeTrackColor = MaterialTheme.colorScheme.primary,
                                        inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    )
                                )
                            }
                            // Captions (CC) toggle with language selection
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                CaptionsLanguageButton(
                                    videoPlayerState = videoPlayerState,
                                    onToggleCaptions = onToggleCaptions,
                                    onCaptionLanguageChange = onCaptionLanguageChange,
                                    onShowControls = onShowControls
                                )
                            }
                            // Fullscreen toggle button
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                                IconButton(
                                    onClick = onToggleFullscreen,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .semantics {
                                            contentDescription =
                                                if (videoPlayerState.isFullscreen) videoPlayerExitFullscreen else videoPlayerEnterFullscreen
                                        }
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            if (videoPlayerState.isFullscreen) R.drawable.ic_fullscreen_exit else R.drawable.ic_fullscreen
                                        ),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ** Fullscreen Mode UI with proper fullscreen support **
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .systemBarsPadding()  // safe-area padding for controls
                    // Capture all touch events to prevent interaction with elements underneath
                    .pointerInput(Unit) {
                        detectTapGestures {
                            // Tapping anywhere on the screen shows controls and toggles play/pause
                            onShowControls(true)
                            onPlayPause()
                        }
                    }
                    // Ensure this component captures all focus and prevents interaction with background
                    .focusable(
                        enabled = true,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            ) {
                // Video content fills screen entirely
                VideoContentArea(
                    video = video,
                    videoPlayerState = videoPlayerState,
                    onPlayPause = onPlayPause,
                    onSeekChanged = {},
                    onSeekFinished = onSeek,
                    onVolumeChange = onVolumeChange,
                    onToggleMute = onToggleMute,
                    onUpdatePosition = onUpdatePosition,
                    onUpdateDuration = onUpdateDuration,
                    onShowControls = onShowControls
                )

                // Controls overlay (top & bottom), use Column to space controls correctly
                AnimatedVisibility(
                    visible = videoPlayerState.showControls,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        TopControls(
                            video = video,
                            onDismiss = onDismiss,
                            isFullscreen = true,
                            onFullscreenToggle = onToggleFullscreen
                        )

                        BottomControls(
                            videoPlayerState = videoPlayerState,
                            onPlayPause = onPlayPause,
                            onSeek = onSeek,
                            onVolumeChange = onVolumeChange,
                            onToggleMute = onToggleMute,
                            onToggleCaptions = onToggleCaptions,
                            onCaptionLanguageChange = onCaptionLanguageChange,
                            onShowControls = onShowControls
                        )
                    }
                }

                // Subtitle overlay
                videoPlayerState.currentSubtitleText?.let { subtitle ->
                    if (videoPlayerState.isCaptionsEnabled) {
                        SubtitleOverlay(text = subtitle, alpha = 1.0f)
                    }
                }

                // Play/Pause overlay icon
                if (showOverlayIcon) {
                    Icon(
                        painter = painterResource(id = overlayIconRes),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun VideoContentArea(
    video: VideoItem,
    videoPlayerState: VideoPlayerState,
    onPlayPause: () -> Unit,
    onSeekChanged: (Float) -> Unit,
    onSeekFinished: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onUpdatePosition: (Float) -> Unit,
    onUpdateDuration: (Float) -> Unit,
    onShowControls: (Boolean) -> Unit
) {
    val context = LocalContext.current
    // Remember ExoPlayer instance
    val exoPlayer = remember(video.videoUrl) { 
        ExoPlayer.Builder(context).build().also { player ->
            // Build and prepare media item
            val mediaItem = if (video.videoUrl.startsWith("android.resource://")) {
                // Handle Android resource URLs
                val resourceName = video.videoUrl.substringAfterLast("/")
                val resourceId = context.resources.getIdentifier(
                    resourceName, "raw", context.packageName
                )
                if (resourceId != 0) {
                    val uri = Uri.parse("android.resource://${context.packageName}/raw/$resourceName")
                    MediaItem.fromUri(uri)
                } else {
                    MediaItem.fromUri(Uri.parse(video.videoUrl))
                }
            } else {
                // Handle regular URLs
                MediaItem.fromUri(Uri.parse(video.videoUrl))
            }
            player.setMediaItem(mediaItem)
            player.prepare()
        } 
    }
    var playerView by remember { mutableStateOf<PlayerView?>(null) }
    var isBuffering by remember { mutableStateOf(false) }
    var playerError by remember { mutableStateOf<String?>(null) }
    
    // Attach ExoPlayer listeners
    LaunchedEffect(exoPlayer) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_READY -> {
                        isBuffering = false
                        // Set duration when ready
                        val durationSec = exoPlayer.duration.coerceAtLeast(0) / 1000f
                        onUpdateDuration(durationSec)
                    }
                    Player.STATE_BUFFERING -> isBuffering = true
                    Player.STATE_ENDED -> isBuffering = false
                    Player.STATE_IDLE -> Unit
                }
            }
            override fun onPlayerError(error: PlaybackException) {
                playerError = "Playback error: ${error.message}"
                isBuffering = false
            }
        })
    }
    // Sync ExoPlayer play/pause with state
    LaunchedEffect(videoPlayerState.isPlaying) {
        if (videoPlayerState.isPlaying) exoPlayer.play() else exoPlayer.pause()
    }
    // Sync ExoPlayer volume & mute with state
    LaunchedEffect(videoPlayerState.volume, videoPlayerState.isMuted) {
        exoPlayer.volume = if (videoPlayerState.isMuted) 0f else videoPlayerState.volume
    }
    // Sync ExoPlayer seek position with state (when user finished seeking)
    LaunchedEffect(videoPlayerState.currentPosition) {
        // Only seek if the difference is significant (to avoid looping updates)
        val targetMs = (videoPlayerState.currentPosition * 1000).toLong()
        if (kotlin.math.abs(exoPlayer.currentPosition - targetMs) > 500) {
            exoPlayer.seekTo(targetMs)
        }
    }
    // Continuously update current position in state while playing
    LaunchedEffect(Unit) {
        while (true) {
            if (!videoPlayerState.isScrubbing) {
                val currentPosSec = exoPlayer.currentPosition / 1000f
                onUpdatePosition(currentPosSec)
            }
            delay(200)  // update every 0.2s
        }
    }
    // Release ExoPlayer when this composable disposes
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }
    
    // UI: PlayerView inside AndroidView
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
            // Remove the pointerInput here since it's handled by the parent container
            // to prevent conflicts and ensure proper full-screen capture
            ,
        contentAlignment = Alignment.Center
    ) {
        if (playerError != null) {
            ErrorMessage(error = playerError.orEmpty())
        } else {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).also { pv ->
                        pv.player = exoPlayer
                        pv.useController = false  // we use custom controls
                        pv.setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                        pv.layoutParams = android.widget.FrameLayout.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        playerView = pv
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            if (isBuffering) {
                // Buffering indicator
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(48.dp))
            }
        }
    }
}

// TopControls and BottomControls for fullscreen
@Composable
private fun TopControls(
    video: VideoItem, 
    onDismiss: () -> Unit, 
    isFullscreen: Boolean,
    onFullscreenToggle: () -> Unit
) {
    // Back button (Close), Title text, Fullscreen toggle
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        Color.Transparent
                    )
                )
            )
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onDismiss, 
            modifier = Modifier
                .size(48.dp)
                .semantics {
                    contentDescription = "Close video player"
                }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back), 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.onSurface, 
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = video.title.ifBlank { "Video" }, 
            color = MaterialTheme.colorScheme.onSurface, 
            maxLines = 1, 
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        )
        IconButton(
            onClick = onFullscreenToggle,
            modifier = Modifier
                .size(48.dp)
                .semantics {
                    contentDescription = if (isFullscreen) "Exit fullscreen" else "Enter fullscreen"
                }
        ) {
            Icon(
                painter = painterResource(
                    if (isFullscreen) R.drawable.ic_fullscreen_exit else R.drawable.ic_fullscreen
                ), 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.onSurface, 
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun BottomControls(
    videoPlayerState: VideoPlayerState,
    onPlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onToggleCaptions: () -> Unit,
    onCaptionLanguageChange: (String) -> Unit,
    onShowControls: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(
                top = Constants.Spacing.MD.dp,
                start = Constants.Spacing.MD.dp,
                end = Constants.Spacing.MD.dp,
                bottom = Constants.Spacing.MD.dp
            )
    ) {
        Column {
            // Timeline slider
            TimelineSlider(
                currentPosition = videoPlayerState.currentPosition,
                duration = videoPlayerState.duration,
                onSeek = { position ->
                    onSeek(position)
                },
                onShowControls = onShowControls
            )

            Spacer(modifier = Modifier.height(Constants.Spacing.SM.dp))

            // Control buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.MD.dp)
                ) {
                    // Play/Pause button
                    IconButton(
                        onClick = onPlayPause,
                        modifier = Modifier
                            .size(48.dp)
                            .focusable()
                            .semantics {
                                contentDescription =
                                    if (videoPlayerState.isPlaying) "Pause video" else "Play video"
                            }
                    ) {
                        Icon(
                            painter = painterResource(id = if (videoPlayerState.isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Time display
                    Text(
                        text = "${formatTime((videoPlayerState.currentPosition * 1000).toLong())} / ${formatTime((videoPlayerState.duration * 1000).toLong())}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.semantics {
                            contentDescription = "Video progress, ${formatTime((videoPlayerState.currentPosition * 1000).toLong())} of ${formatTime((videoPlayerState.duration * 1000).toLong())}"
                        }
                    )
                }

                // Right side controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.MD.dp)
                ) {
                    // Volume control
                    VolumeControl(
                        volume = videoPlayerState.volume,
                        onVolumeChange = onVolumeChange,
                        onToggleMute = onToggleMute,
                        onShowControls = onShowControls
                    )

                    // Captions toggle with language selection
                    CaptionsLanguageButton(
                        videoPlayerState = videoPlayerState,
                        onToggleCaptions = onToggleCaptions,
                        onCaptionLanguageChange = onCaptionLanguageChange,
                        onShowControls = onShowControls
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineSlider(
    currentPosition: Float,
    duration: Float,
    onSeek: (Float) -> Unit,
    onShowControls: (Boolean) -> Unit = {}
) {
    var isDragging by remember { mutableStateOf(false) }
    
    Slider(
        value = currentPosition,
        onValueChange = { position ->
            isDragging = true
            onShowControls(true) // Keep controls visible during seeking
            onSeek(position)
        },
        onValueChangeFinished = {
            isDragging = false
        },
        valueRange = 0f..duration,
        modifier = Modifier
            .fillMaxWidth()
            .focusable()
            .semantics {
                contentDescription =
                    "Video timeline, ${formatTime((currentPosition * 1000).toLong())} of ${
                        formatTime((duration * 1000).toLong())
                    }"
                stateDescription = "${((currentPosition / duration) * 100).toInt()}% complete"
            },
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
        )
    )
}

@Composable
private fun VolumeControl(
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onShowControls: (Boolean) -> Unit = {}
) {
    val isMuted = volume == 0f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.SM.dp)
    ) {
        IconButton(
            onClick = onToggleMute,
            modifier = Modifier
                .size(40.dp)
                .focusable()
                .semantics {
                    contentDescription = if (isMuted) "Unmute audio" else "Mute audio"
                }
        ) {
                    Icon(
            painter = painterResource(id = R.drawable.ic_sound_effects),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        }

        Slider(
            value = volume,
            onValueChange = { newVolume ->
                onVolumeChange(newVolume)
            },
            onValueChangeFinished = {
                // Volume change finished - interaction tracking is handled by the callback
            },
            valueRange = 0f..1f,
            modifier = Modifier
                .width(80.dp)
                .semantics {
                    contentDescription = "Volume control, ${(volume * 100).toInt()}%"
                },
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun CaptionsLanguageButton(
    videoPlayerState: VideoPlayerState,
    onToggleCaptions: () -> Unit,
    onCaptionLanguageChange: (String) -> Unit,
    onShowControls: (Boolean) -> Unit = {}
) {
    var showLanguageMenu by remember { mutableStateOf(false) }
    var longPressTimer by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Get string resources
    val cdCaptionLanguageMenu = stringResource(R.string.cd_video_player_caption_language_menu)
    val cdEnglishCaptions = stringResource(R.string.cd_video_player_caption_language_english)
    val cdGermanCaptions = stringResource(R.string.cd_video_player_caption_language_german)
    val cdOffCaptions = stringResource(R.string.cd_video_player_caption_language_off)
    val cdSelectLanguage = stringResource(R.string.cd_video_player_caption_language_select)
    val cdEnableCaptions = stringResource(R.string.cd_video_player_caption_language_enable)
    val cdDisableCaptions = stringResource(R.string.cd_video_player_caption_language_disable)
    val cdNoSubtitles = stringResource(R.string.cd_video_player_caption_language_no_subtitles)
    val cdSubtitlesNotAvailable = stringResource(R.string.cd_video_player_caption_language_subtitles_not_available)
    val cdSubtitlesAvailable = stringResource(R.string.cd_video_player_caption_language_subtitles_available)
    val cdCaptionsEnabled = stringResource(R.string.cd_video_player_caption_language_captions_enabled)
    val cdCaptionsDisabled = stringResource(R.string.cd_video_player_caption_language_captions_disabled)
    val offText = stringResource(R.string.video_player_caption_language_off)
    
    // Handle long press
    LaunchedEffect(isPressed) {
        if (isPressed && videoPlayerState.availableCaptionLanguages.size > 1) {
            longPressTimer = launch {
                delay(500) // 500ms for long press
                showLanguageMenu = true
            }
        } else {
            longPressTimer?.cancel()
            longPressTimer = null
        }
    }
    
    Box {
        IconButton(
            onClick = {
                // Single tap toggles captions on/off
                longPressTimer?.cancel() // Cancel long press if it's a tap
                onToggleCaptions()
            },
            enabled = videoPlayerState.hasSubtitles,
            interactionSource = interactionSource,
            modifier = Modifier
                .size(40.dp)
                .focusable()
                .semantics {
                    contentDescription = when {
                        !videoPlayerState.hasSubtitles -> cdNoSubtitles
                        videoPlayerState.availableCaptionLanguages.size > 1 -> cdSelectLanguage
                        videoPlayerState.isCaptionsEnabled -> cdDisableCaptions
                        else -> cdEnableCaptions
                    }
                    stateDescription = when {
                        !videoPlayerState.hasSubtitles -> cdSubtitlesNotAvailable
                        videoPlayerState.isCaptionsEnabled -> cdCaptionsEnabled
                        else -> cdCaptionsDisabled
                    }
                }
        ) {
            Surface(
                color = when {
                    !videoPlayerState.hasSubtitles -> Color.White.copy(alpha = 0.3f)
                    videoPlayerState.isCaptionsEnabled -> MaterialTheme.colorScheme.primary
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(Constants.CornerRadius.SM.dp),
                modifier = Modifier.size(32.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.video_player_cc_button),
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            !videoPlayerState.hasSubtitles -> Color.White.copy(alpha = 0.5f)
                            videoPlayerState.isCaptionsEnabled -> MaterialTheme.colorScheme.onPrimary
                            else -> Color.White
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        // Language selection dropdown menu
        DropdownMenu(
            expanded = showLanguageMenu,
            onDismissRequest = { 
                showLanguageMenu = false
                // Keep controls visible when menu is dismissed
                onShowControls(true)
            },
            modifier = Modifier.semantics {
                contentDescription = cdCaptionLanguageMenu
            }
        ) {
            // Off option
            DropdownMenuItem(
                text = {
                    Text(
                        text = offText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (!videoPlayerState.isCaptionsEnabled) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                },
                onClick = {
                    onToggleCaptions()
                    showLanguageMenu = false
                    // Keep controls visible when selecting an option
                    onShowControls(true)
                },
                modifier = Modifier.semantics {
                    contentDescription = cdOffCaptions
                }
            )
            
            // Language options
            videoPlayerState.availableCaptionLanguages.forEach { captionLanguage ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = captionLanguage.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (videoPlayerState.selectedCaptionLanguage == captionLanguage.languageCode && videoPlayerState.isCaptionsEnabled) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    },
                    onClick = {
                        onCaptionLanguageChange(captionLanguage.languageCode)
                        if (!videoPlayerState.isCaptionsEnabled) {
                            onToggleCaptions() // Enable captions when selecting a language
                        }
                        showLanguageMenu = false
                        // Keep controls visible when selecting a language
                        onShowControls(true)
                    },
                    modifier = Modifier.semantics {
                        contentDescription = when (captionLanguage.languageCode) {
                            "en" -> cdEnglishCaptions
                            "de" -> cdGermanCaptions
                            else -> "Select ${captionLanguage.displayName} captions"
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ErrorMessage(error: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color.Red.copy(alpha = 0.9f),
            shape = RoundedCornerShape(Constants.CornerRadius.MD.dp),
            modifier = Modifier.padding(Constants.Spacing.LG.dp)
        ) {
            Text(
                text = stringResource(R.string.video_player_error_message, error),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.padding(Constants.Spacing.MD.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Subtitle overlay that displays synchronized captions
 */
@Composable
private fun SubtitleOverlay(
    text: String,
    alpha: Float
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            color = Color.Black.copy(alpha = 0.8f * alpha),
            shape = RoundedCornerShape(Constants.CornerRadius.SM.dp),
            modifier = Modifier
                .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
                .animateContentSize()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    horizontal = Constants.Spacing.MD.dp,
                    vertical = Constants.Spacing.SM.dp
                ),
                maxLines = 3,
                softWrap = true
            )
        }
    }
}
