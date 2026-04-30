package com.makhabatusen.access_lab_app.ui.media.components.browse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.ui.media.data.models.YoutubeVideoItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCard(
    video: YoutubeVideoItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                enabled = true
            )
            .semantics {
                contentDescription = "Video: ${video.title}"
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail
            VideoThumbnail(
                thumbnailUrl = video.thumbnailUrl,
                duration = video.duration,
                modifier = Modifier
                    .width(120.dp)
                    .height(68.dp)
            )
            
            // Video Info
            VideoInfo(
                title = video.title,
                channelTitle = video.channelTitle,
                publishedAt = video.publishedAt,
                viewCount = video.viewCount,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun VideoThumbnail(
    thumbnailUrl: String?,
    duration: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        if (thumbnailUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_media)
            )
        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_media),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Duration badge
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = duration,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun VideoInfo(
    title: String,
    channelTitle: String,
    publishedAt: String,
    viewCount: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.semantics {
                contentDescription = "Video title: $title"
            }
        )
        
        // Channel name
        Text(
            text = channelTitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.semantics {
                contentDescription = "Channel: $channelTitle"
            }
        )
        
        // View count and publish date
        Text(
            text = "$viewCount • ${formatPublishedDate(publishedAt)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.semantics {
                contentDescription = "Views: $viewCount, Published: ${formatPublishedDate(publishedAt)}"
            }
        )
    }
}

private fun formatPublishedDate(publishedAt: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(publishedAt)
        date?.let { outputFormat.format(it) } ?: publishedAt
    } catch (e: Exception) {
        publishedAt
    }
} 