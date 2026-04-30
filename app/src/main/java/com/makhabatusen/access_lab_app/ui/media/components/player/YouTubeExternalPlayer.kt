package com.makhabatusen.access_lab_app.ui.media.components.player

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.makhabatusen.access_lab_app.R

/**
 * Simple YouTube player that opens videos in external YouTube app or browser
 * This is the most reliable approach for playing YouTube videos
 */
@Composable
fun SimpleYouTubePlayer(
    videoId: String,
    videoTitle: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Initialize string resources for accessibility
    val cdYoutubeExternalOpen = stringResource(R.string.cd_youtube_external_open)
    val cdYoutubeExternalCancel = stringResource(R.string.cd_youtube_external_cancel)
    val cdYoutubeExternalClose = stringResource(R.string.cd_youtube_external_close)
    val youtubeExternalDescription = stringResource(R.string.youtube_external_description)
    val youtubeExternalOpenButton = stringResource(R.string.youtube_external_open_button)
    val youtubeExternalCancelButton = stringResource(R.string.youtube_external_cancel_button)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
            shape = RoundedCornerShape(16.dp),
            color = Color.Black
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Video thumbnail placeholder
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(
                                Color.Gray.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play video",
                            modifier = Modifier.size(64.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Video title
                    Text(
                        text = videoTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.semantics {
                            contentDescription = "Video title: $videoTitle"
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        text = youtubeExternalDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Play button
                    Button(
                        onClick = {
                            try {
                                // Try to open in YouTube app first
                                val intent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Fallback to browser
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.youtube.com/watch?v=$videoId")
                                )
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            }
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .semantics {
                                contentDescription = cdYoutubeExternalOpen
                            },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = youtubeExternalOpenButton,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cancel button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .semantics {
                                contentDescription = cdYoutubeExternalCancel
                            },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            brush = Brush.linearGradient(
                                colors = listOf(Color.White, Color.White.copy(alpha = 0.7f))
                            )
                        )
                    ) {
                        Text(youtubeExternalCancelButton)
                    }
                }
                // End of Column
                // Close button in top-right corner
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .semantics {
                            contentDescription = cdYoutubeExternalClose
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}