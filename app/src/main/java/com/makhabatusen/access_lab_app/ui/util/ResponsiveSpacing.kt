package com.makhabatusen.access_lab_app.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Responsive spacing utilities that adapt to different screen sizes and orientations
 * while maintaining Material Design 3 guidelines and accessibility standards.
 */
object ResponsiveSpacing {
    
    /**
     * Get responsive screen padding based on device type and orientation
     */
    @Composable
    fun getScreenPadding(): ScreenPadding {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        val screenHeight = configuration.screenHeightDp
        val isLandscape = screenWidth > screenHeight
        
        return when {
            screenWidth >= Constants.Breakpoints.LARGE_TABLET_MIN_WIDTH -> {
                // Large tablet (10" and up)
                ScreenPadding(
                    horizontal = if (isLandscape) 40.dp else 56.dp,
                    vertical = if (isLandscape) 24.dp else 32.dp
                )
            }
            screenWidth >= Constants.Breakpoints.TABLET_MIN_WIDTH -> {
                // Tablet (7" to 10")
                ScreenPadding(
                    horizontal = if (isLandscape) 32.dp else 48.dp,
                    vertical = if (isLandscape) 20.dp else 24.dp
                )
            }
            isLandscape -> {
                // Phone landscape
                ScreenPadding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
            }
            else -> {
                // Phone portrait - Material 3 default
                ScreenPadding(
                    horizontal = Constants.Spacing.Screen.horizontal,
                    vertical = Constants.Spacing.Screen.vertical
                )
            }
        }
    }
    
    /**
     * Get responsive content width based on device type
     */
    @Composable
    fun getMaxContentWidth(): Dp {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
        
        return when {
            screenWidth >= Constants.Breakpoints.LARGE_TABLET_MIN_WIDTH -> {
                Constants.ContentWidth.LARGE_TABLET_MAX.dp
            }
            screenWidth >= Constants.Breakpoints.TABLET_MIN_WIDTH -> {
                Constants.ContentWidth.TABLET_MAX.dp
            }
            isLandscape -> {
                Constants.ContentWidth.LANDSCAPE_MAX.dp
            }
            else -> {
                Constants.ContentWidth.PHONE_MAX.dp
            }
        }
    }
    
    /**
     * Get responsive button height based on device type
     */
    @Composable
    fun getButtonHeight(): Dp {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        
        return when {
            screenWidth >= Constants.Breakpoints.LARGE_TABLET_MIN_WIDTH -> {
                Constants.Heights.BUTTON_XLARGE.dp
            }
            screenWidth >= Constants.Breakpoints.TABLET_MIN_WIDTH -> {
                Constants.Heights.BUTTON_LARGE.dp
            }
            else -> {
                Constants.Heights.BUTTON_STANDARD.dp
            }
        }
    }
    
    /**
     * Get responsive spacing between elements based on context
     */
    @Composable
    fun getElementSpacing(context: SpacingContext = SpacingContext.CONTENT): Dp {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
        
        val baseSpacing = when (context) {
            SpacingContext.CONTENT -> Constants.Spacing.Content.betweenElements
            SpacingContext.SECTION -> Constants.Spacing.Content.section
            SpacingContext.FORM -> Constants.Spacing.Form.fieldSpacing
            SpacingContext.LIST -> Constants.Spacing.List.itemSpacing
            SpacingContext.BUTTON -> Constants.Spacing.Interactive.betweenButtons
            SpacingContext.MEDIA -> Constants.Spacing.Media.thumbnailSpacing
            SpacingContext.QUIZ -> Constants.Spacing.Quiz.questionSpacing
        }
        
        // Scale spacing for larger screens
        return when {
            screenWidth >= Constants.Breakpoints.LARGE_TABLET_MIN_WIDTH -> {
                baseSpacing * 1.5f
            }
            screenWidth >= Constants.Breakpoints.TABLET_MIN_WIDTH -> {
                baseSpacing * 1.25f
            }
            else -> {
                baseSpacing
            }
        }
    }
    
    /**
     * Get responsive card padding based on device type
     */
    @Composable
    fun getCardPadding(): Dp {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        
        return when {
            screenWidth >= Constants.Breakpoints.LARGE_TABLET_MIN_WIDTH -> {
                Constants.Spacing.XL.dp
            }
            screenWidth >= Constants.Breakpoints.TABLET_MIN_WIDTH -> {
                Constants.Spacing.LG.dp
            }
            else -> {
                Constants.Spacing.Content.card
            }
        }
    }
}

/**
 * Data class for screen padding values
 */
data class ScreenPadding(
    val horizontal: Dp,
    val vertical: Dp
)

/**
 * Enum for different spacing contexts
 */
enum class SpacingContext {
    CONTENT,    // General content spacing
    SECTION,    // Section spacing
    FORM,       // Form field spacing
    LIST,       // List item spacing
    BUTTON,     // Button spacing
    MEDIA,      // Media component spacing
    QUIZ        // Quiz component spacing
} 