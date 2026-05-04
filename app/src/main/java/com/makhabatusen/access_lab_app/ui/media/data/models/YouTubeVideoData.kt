package com.makhabatusen.access_lab_app.ui.media.data.models

import com.makhabatusen.access_lab_app.ui.media.data.services.YouTubeVideo

/**
 * Data class representing a video item in search results
 */
data class YoutubeVideoItem(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String?,
    val channelTitle: String,
    val publishedAt: String,
    val duration: String,
    val viewCount: String,
    val videoUrl: String? = null
) {
    
    companion object {
        /**
         * Create a VideoItem from YouTubeVideo (from the existing API service)
         */
        fun fromYouTubeVideo(video: YouTubeVideo): YoutubeVideoItem {
            return YoutubeVideoItem(
                id = video.id,
                title = video.title,
                description = video.description,
                thumbnailUrl = video.thumbnailUrl,
                channelTitle = video.channelTitle,
                publishedAt = video.publishedAt,
                duration = video.duration,
                viewCount = "Unknown views"
            )
        }
        
        /**
         * Create placeholder videos for the accessibility playlist
         * Based on the actual playlist: PLWz5rJ2EKKc8OENfLdh3zM5T6IRdlVYKj
         */
        fun getPlaceholderVideos(): List<YoutubeVideoItem> {
            return listOf(
                YoutubeVideoItem(
                    id = "GRV1kucMqIo",
                    title = "Accessibility Fundamentals - Understanding the Basics",
                    description = "Learn the fundamental principles of accessibility and why they matter for all users.",
                    thumbnailUrl = "https://img.youtube.com/vi/GRV1kucMqIo/hqdefault.jpg",
                    channelTitle = "Accessibility Lab",
                    publishedAt = "2024-01-15T10:00:00Z",
                    duration = "15:30",
                    viewCount = "12.5K views"
                ),
                YoutubeVideoItem(
                    id = "tLIUaZyTtX4",
                    title = "WCAG 2.1 Guidelines - Complete Overview",
                    description = "Comprehensive guide to WCAG 2.1 accessibility guidelines and implementation strategies.",
                    thumbnailUrl = "https://img.youtube.com/vi/tLIUaZyTtX4/hqdefault.jpg",
                    channelTitle = "Accessibility Lab",
                    publishedAt = "2024-01-20T14:30:00Z",
                    duration = "22:15",
                    viewCount = "18.7K views"
                ),
                YoutubeVideoItem(
                    id = "_1yRVwhEv5I",
                    title = "Mobile Accessibility - Best Practices for Apps",
                    description = "Essential accessibility practices for mobile app development across platforms.",
                    thumbnailUrl = "https://img.youtube.com/vi/_1yRVwhEv5I/hqdefault.jpg",
                    channelTitle = "Accessibility Lab",
                    publishedAt = "2024-02-01T09:15:00Z",
                    duration = "19:45",
                    viewCount = "14.2K views"
                ),
                YoutubeVideoItem(
                    id = "DLN2s16HwcE",
                    title = "Screen Reader Testing - Comprehensive Guide",
                    description = "How to effectively test your applications with screen readers and assistive technologies.",
                    thumbnailUrl = "https://img.youtube.com/vi/DLN2s16HwcE/hqdefault.jpg",
                    channelTitle = "Accessibility Lab",
                    publishedAt = "2024-02-10T16:45:00Z",
                    duration = "16:30",
                    viewCount = "8.9K views"
                ),
                YoutubeVideoItem(
                    id = "i1gMzQv0hWU",
                    title = "Color Contrast and Visual Accessibility Standards",
                    description = "Understanding color contrast requirements and visual accessibility standards for better design.",
                    thumbnailUrl = "https://img.youtube.com/vi/i1gMzQv0hWU/hqdefault.jpg",
                    channelTitle = "Accessibility Lab",
                    publishedAt = "2024-02-15T11:20:00Z",
                    duration = "13:45",
                    viewCount = "11.3K views"
                ),
                YoutubeVideoItem(
                    id = "X97P6Y8WHl0",
                    title = "Keyboard Navigation and Focus Management",
                    description = "Implementing proper keyboard navigation and focus indicators for better accessibility.",
                    thumbnailUrl = "https://img.youtube.com/vi/X97P6Y8WHl0/hqdefault.jpg",
                    channelTitle = "Accessibility Lab",
                    publishedAt = "2024-02-25T13:10:00Z",
                    duration = "20:15",
                    viewCount = "9.6K views"
                ),
                YoutubeVideoItem(
                    id = "JvWM2PjLJls",
                    title = "Accessible Forms and Input Validation",
                    description = "Creating accessible forms with proper labels, error handling, and validation.",
                    thumbnailUrl = "https://img.youtube.com/vi/JvWM2PjLJls/hqdefault.jpg",
                    channelTitle = "Accessibility Lab",
                    publishedAt = "2024-03-05T10:30:00Z",
                    duration = "17:20",
                    viewCount = "13.8K views"
                ),
                YoutubeVideoItem(
                    id = "wWDYIGk0Kdo",
                    title = "Inclusive Design Principles and Practices",
                    description = "Core principles of inclusive design for creating digital products that work for everyone.",
                    thumbnailUrl = "https://img.youtube.com/vi/wWDYIGk0Kdo/hqdefault.jpg",
                    channelTitle = "Accessibility Lab",
                    publishedAt = "2024-03-12T15:45:00Z",
                    duration = "24:30",
                    viewCount = "16.4K views"
                ),
                YoutubeVideoItem(
                    id = "Dqqbe8IFBA4",
                    title = "Accessibility Testing Tools and Techniques",
                    description = "Overview of essential accessibility testing tools and techniques for developers.",
                    thumbnailUrl = "https://img.youtube.com/vi/Dqqbe8IFBA4/hqdefault.jpg",
                    channelTitle = "Accessibility Lab",
                    publishedAt = "2024-03-20T12:00:00Z",
                    duration = "18:45",
                    viewCount = "10.7K views"
                ),
                YoutubeVideoItem(
                    id = "RHHpljSTDxA",
                    title = "Semantic HTML and Accessibility",
                    description = "How to use semantic HTML elements to improve accessibility and user experience.",
                    thumbnailUrl = "https://img.youtube.com/vi/RHHpljSTDxA/hqdefault.jpg",
                    channelTitle = "Accessibility Lab",
                    publishedAt = "2024-03-25T14:20:00Z",
                    duration = "14:55",
                    viewCount = "12.1K views"
                ),
                YoutubeVideoItem(
                    id = "Pjzjs3kB0JA",
                    title = "ARIA Labels and Roles - Advanced Accessibility",
                    description = "Advanced techniques using ARIA labels and roles for complex accessibility scenarios.",
                    thumbnailUrl = "https://img.youtube.com/vi/Pjzjs3kB0JA/hqdefault.jpg",
                    channelTitle = "Accessibility Lab",
                    publishedAt = "2024-04-01T09:30:00Z",
                    duration = "21:10",
                    viewCount = "15.3K views"
                ),
                YoutubeVideoItem(
                    id = "O2DeSITnzFk",
                    title = "Accessibility in React and Modern Frameworks",
                    description = "Implementing accessibility in React and other modern JavaScript frameworks.",
                    thumbnailUrl = "https://img.youtube.com/vi/O2DeSITnzFk/hqdefault.jpg",
                    channelTitle = "Accessibility Lab",
                    publishedAt = "2024-04-08T11:15:00Z",
                    duration = "25:40",
                    viewCount = "19.2K views"
                ),
                YoutubeVideoItem(
                    id = "LxKat_m7mHk",
                    title = "Voice User Interfaces and Accessibility",
                    description = "Designing voice user interfaces that are accessible to all users.",
                    thumbnailUrl = "https://img.youtube.com/vi/LxKat_m7mHk/hqdefault.jpg",
                    channelTitle = "Accessibility Lab",
                    publishedAt = "2024-04-15T16:45:00Z",
                    duration = "16:25",
                    viewCount = "8.4K views"
                ),
                YoutubeVideoItem(
                    id = "uG1v_7KA37E",
                    title = "Accessibility Compliance and Legal Requirements",
                    description = "Understanding accessibility compliance requirements and legal obligations.",
                    thumbnailUrl = "https://img.youtube.com/vi/uG1v_7KA37E/hqdefault.jpg",
                    channelTitle = "Accessibility Lab",
                    publishedAt = "2024-04-22T13:30:00Z",
                    duration = "23:15",
                    viewCount = "14.7K views"
                ),
                YoutubeVideoItem(
                    id = "rtyjbUxUmG8",
                    title = "Future of Accessibility - Emerging Technologies",
                    description = "Exploring emerging technologies and their impact on accessibility.",
                    thumbnailUrl = "https://img.youtube.com/vi/rtyjbUxUmG8/hqdefault.jpg",
                    channelTitle = "Accessibility Lab",
                    publishedAt = "2024-04-29T10:00:00Z",
                    duration = "28:30",
                    viewCount = "11.9K views"
                )
            )
        }
    }
} 