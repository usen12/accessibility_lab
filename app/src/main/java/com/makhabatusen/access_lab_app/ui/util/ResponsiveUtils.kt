package com.makhabatusen.access_lab_app.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

enum class WindowSizeClass {
    Compact,    // Phone
    Medium,     // Large phone / Small tablet
    Expanded    // Tablet
}

@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    return when {
        screenWidth < Constants.Breakpoints.PHONE_MAX_WIDTH.dp -> WindowSizeClass.Compact
        screenWidth < Constants.Breakpoints.DESKTOP_MIN_WIDTH.dp -> WindowSizeClass.Medium
        else -> WindowSizeClass.Expanded
    }
}

@Composable
fun isLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    // Debug logging (remove in production)
    android.util.Log.d("ResponsiveUtils", "isLandscape: orientation=${configuration.orientation}, isLandscape=$isLandscape")
    
    return isLandscape
}

@Composable
fun isTablet(): Boolean {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val smallestWidth = minOf(screenWidth, screenHeight)
    val largestWidth = maxOf(screenWidth, screenHeight)
    val aspectRatio = largestWidth.toFloat() / smallestWidth.toFloat()
    
    // A device is considered a tablet if:
    // 1. Smallest width is >= 720dp (typical tablet minimum)
    // 2. Aspect ratio is reasonable (not too extreme like phones)
    // 3. Largest width is also substantial
    val isTablet = smallestWidth >= Constants.Breakpoints.TABLET_MIN_WIDTH &&
           aspectRatio <= 2.0f && // Most tablets have aspect ratio <= 2.0
           largestWidth >= 1000 // Ensure it's actually a large screen
    
    // Debug logging (remove in production)
    android.util.Log.d("ResponsiveUtils", "isTablet: screenWidth=$screenWidth, screenHeight=$screenHeight, smallestWidth=$smallestWidth, largestWidth=$largestWidth, aspectRatio=$aspectRatio, isTablet=$isTablet")
    
    return isTablet
}

@Composable
fun isLargeTablet(): Boolean {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val smallestWidth = minOf(screenWidth, screenHeight)
    val largestWidth = maxOf(screenWidth, screenHeight)
    val aspectRatio = largestWidth.toFloat() / smallestWidth.toFloat()
    
    // A device is considered a large tablet if:
    // 1. Smallest width is >= 840dp (10" tablet minimum)
    // 2. Aspect ratio is reasonable
    // 3. Largest width is substantial
    return smallestWidth >= Constants.Breakpoints.LARGE_TABLET_MIN_WIDTH &&
           aspectRatio <= 2.0f &&
           largestWidth >= 1200
} 