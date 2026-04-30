package com.makhabatusen.access_lab_app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import android.content.Context
import com.makhabatusen.access_lab_app.R

/**
 * Font size scale options for accessibility
 * Follows WCAG 2.1 and BITV 2.0 guidelines
 * Maximum scale of 2.0x aligns with Android system font scale limits
 */
enum class FontSizeScale {
    SMALL,      // 0.85x - for users who prefer smaller text
    MEDIUM,     // 1.0x - default size
    LARGE,      // 1.25x - enhanced readability
    EXTRA_LARGE, // 1.5x - high accessibility
    MAXIMUM     // 2.0x - maximum accessibility (Android system limit)
}

/**
 * Typography manager that handles font scaling for accessibility
 */
object TypographyManager {
    private var currentScale: FontSizeScale = FontSizeScale.MEDIUM
    private val _scaleState = mutableStateOf(currentScale)
    
    fun setFontScale(scale: FontSizeScale) {
        android.util.Log.d("TypographyManager", "Setting font scale to: $scale")
        currentScale = scale
        _scaleState.value = scale
    }
    
    fun getFontScale(): FontSizeScale = currentScale
    
    fun getScaleState() = _scaleState
    
    private fun getScaleFactor(): Float = when (currentScale) {
        FontSizeScale.SMALL -> 0.85f
        FontSizeScale.MEDIUM -> 1.0f
        FontSizeScale.LARGE -> 1.25f
        FontSizeScale.EXTRA_LARGE -> 1.5f
        FontSizeScale.MAXIMUM -> 2.0f
    }
    
    private fun scaleFontSize(baseSize: Float): Float = baseSize * getScaleFactor()
    
    /**
     * Get typography with current font scale applied
     */
    fun getTypography(): Typography {
        val scaleFactor = getScaleFactor()
        android.util.Log.d("TypographyManager", "Generating typography with scale factor: $scaleFactor")
        
        return Typography(
            // Display styles
            displayLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = scaleFontSize(57f).sp,
                lineHeight = scaleFontSize(64f).sp,
                letterSpacing = (-0.25).sp
            ),
            displayMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = scaleFontSize(45f).sp,
                lineHeight = scaleFontSize(52f).sp,
                letterSpacing = 0.sp
            ),
            displaySmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = scaleFontSize(36f).sp,
                lineHeight = scaleFontSize(44f).sp,
                letterSpacing = 0.sp
            ),
            
            // Headline styles
            headlineLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = scaleFontSize(32f).sp,
                lineHeight = scaleFontSize(40f).sp,
                letterSpacing = 0.sp
            ),
            headlineMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = scaleFontSize(28f).sp,
                lineHeight = scaleFontSize(36f).sp,
                letterSpacing = 0.sp
            ),
            headlineSmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = scaleFontSize(24f).sp,
                lineHeight = scaleFontSize(32f).sp,
                letterSpacing = 0.sp
            ),
            
            // Title styles
            titleLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = scaleFontSize(22f).sp,
                lineHeight = scaleFontSize(28f).sp,
                letterSpacing = 0.sp
            ),
            titleMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = scaleFontSize(16f).sp,
                lineHeight = scaleFontSize(24f).sp,
                letterSpacing = 0.15.sp
            ),
            titleSmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = scaleFontSize(14f).sp,
                lineHeight = scaleFontSize(20f).sp,
                letterSpacing = 0.1.sp
            ),
            
            // Body styles
            bodyLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = scaleFontSize(16f).sp,
                lineHeight = scaleFontSize(24f).sp,
                letterSpacing = 0.5.sp
            ),
            bodyMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = scaleFontSize(14f).sp,
                lineHeight = scaleFontSize(20f).sp,
                letterSpacing = 0.25.sp
            ),
            bodySmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = scaleFontSize(12f).sp,
                lineHeight = scaleFontSize(16f).sp,
                letterSpacing = 0.4.sp
            ),
            
            // Label styles
            labelLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = scaleFontSize(14f).sp,
                lineHeight = scaleFontSize(20f).sp,
                letterSpacing = 0.1.sp
            ),
            labelMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = scaleFontSize(12f).sp,
                lineHeight = scaleFontSize(16f).sp,
                letterSpacing = 0.5.sp
            ),
            labelSmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = scaleFontSize(11f).sp,
                lineHeight = scaleFontSize(16f).sp,
                letterSpacing = 0.5.sp
            )
        )
    }
    
    /**
     * Get the current scale factor as a percentage
     */
    fun getScalePercentage(): Int = (getScaleFactor() * 100).toInt()
    
    /**
     * Get a readable description of the current font scale
     */
    fun getScaleDescription(context: Context): String = when (currentScale) {
        FontSizeScale.SMALL -> context.getString(R.string.accessibility_font_scale_small_desc)
        FontSizeScale.MEDIUM -> context.getString(R.string.accessibility_font_scale_medium_desc)
        FontSizeScale.LARGE -> context.getString(R.string.accessibility_font_scale_large_desc)
        FontSizeScale.EXTRA_LARGE -> context.getString(R.string.accessibility_font_scale_extra_large_desc)
        FontSizeScale.MAXIMUM -> context.getString(R.string.accessibility_font_scale_maximum_desc)
    }
}

// Default typography for backward compatibility
val Typography = TypographyManager.getTypography()