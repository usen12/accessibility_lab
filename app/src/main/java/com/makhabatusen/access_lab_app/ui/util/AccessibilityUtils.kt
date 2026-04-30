package com.makhabatusen.access_lab_app.ui.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Utility class for accessibility compliance checks and helpers.
 * Implements BITV 2.0 and WCAG 2.1 AA guidelines.
 */
object AccessibilityUtils {
    
    /**
     * Calculate the relative luminance of a color (WCAG 2.1)
     */
    fun calculateRelativeLuminance(color: Color): Double {
        val red = color.red
        val green = color.green
        val blue = color.blue
        
        val rsRGB = if (red <= 0.03928) red / 12.92 else ((red + 0.055) / 1.055).pow(2.4)
        val gsRGB = if (green <= 0.03928) green / 12.92 else ((green + 0.055) / 1.055).pow(2.4)
        val bsRGB = if (blue <= 0.03928) blue / 12.92 else ((blue + 0.055) / 1.055).pow(2.4)
        
        return 0.2126 * rsRGB + 0.7152 * gsRGB + 0.0722 * bsRGB
    }
    
    /**
     * Calculate contrast ratio between two colors (WCAG 2.1)
     */
    fun calculateContrastRatio(color1: Color, color2: Color): Double {
        val luminance1 = calculateRelativeLuminance(color1)
        val luminance2 = calculateRelativeLuminance(color2)
        
        val lighter = max(luminance1, luminance2)
        val darker = min(luminance1, luminance2)
        
        return (lighter + 0.05) / (darker + 0.05)
    }
    
    /**
     * Check if contrast ratio meets WCAG AA standards
     * - Normal text: 4.5:1
     * - Large text: 3:1
     */
    fun meetsWCAGAA(color1: Color, color2: Color, isLargeText: Boolean = false): Boolean {
        val contrastRatio = calculateContrastRatio(color1, color2)
        return if (isLargeText) {
            contrastRatio >= 3.0
        } else {
            contrastRatio >= 4.5
        }
    }
    
    /**
     * Check if contrast ratio meets WCAG AAA standards
     * - Normal text: 7:1
     * - Large text: 4.5:1
     */
    fun meetsWCAGAAA(color1: Color, color2: Color, isLargeText: Boolean = false): Boolean {
        val contrastRatio = calculateContrastRatio(color1, color2)
        return if (isLargeText) {
            contrastRatio >= 4.5
        } else {
            contrastRatio >= 7.0
        }
    }
    
    /**
     * Check if contrast ratio meets BITV 2.0 standards
     * - Normal text: 4.5:1 (same as WCAG AA)
     * - Large text: 3:1 (same as WCAG AA)
     */
    fun meetsBITV20(color1: Color, color2: Color, isLargeText: Boolean = false): Boolean {
        return meetsWCAGAA(color1, color2, isLargeText)
    }
    
    /**
     * Get the minimum touch target size for accessibility
     * - WCAG: 44x44 points minimum
     * - Material Design: 48x48dp recommended
     */
    fun getMinimumTouchTargetSize(): Int = 48
    
    /**
     * Get the recommended touch target size for accessibility
     * - Material Design: 48x48dp
     * - Enhanced accessibility: 56x56dp
     */
    fun getRecommendedTouchTargetSize(): Int = 56
    
    /**
     * Check if a color is considered "light" for accessibility purposes
     */
    fun isLightColor(color: Color): Boolean {
        return color.luminance() > 0.5
    }
    
    /**
     * Get appropriate text color for a given background color
     * Ensures sufficient contrast for accessibility
     */
    fun getAccessibleTextColor(backgroundColor: Color): Color {
        return if (isLightColor(backgroundColor)) {
            Color.Black
        } else {
            Color.White
        }
    }
    
    /**
     * Validate color combination for accessibility
     * Returns a list of issues found
     */
    fun validateColorAccessibility(
        foregroundColor: Color,
        backgroundColor: Color,
        isLargeText: Boolean = false
    ): List<String> {
        val issues = mutableListOf<String>()
        
        val contrastRatio = calculateContrastRatio(foregroundColor, backgroundColor)
        
        if (!meetsWCAGAA(foregroundColor, backgroundColor, isLargeText)) {
            issues.add("Contrast ratio ${String.format("%.2f", contrastRatio)}:1 does not meet WCAG AA standards")
        }
        
        if (!meetsBITV20(foregroundColor, backgroundColor, isLargeText)) {
            issues.add("Contrast ratio ${String.format("%.2f", contrastRatio)}:1 does not meet BITV 2.0 standards")
        }
        
        return issues
    }
    
    /**
     * Get accessibility recommendations for color combinations
     */
    fun getAccessibilityRecommendations(
        foregroundColor: Color,
        backgroundColor: Color,
        isLargeText: Boolean = false
    ): List<String> {
        val recommendations = mutableListOf<String>()
        
        val contrastRatio = calculateContrastRatio(foregroundColor, backgroundColor)
        
        if (contrastRatio < 3.0) {
            recommendations.add("Consider using colors with higher contrast for better accessibility")
        } else if (contrastRatio < 4.5) {
            recommendations.add("For normal text, aim for at least 4.5:1 contrast ratio")
        } else if (contrastRatio < 7.0) {
            recommendations.add("For enhanced accessibility, consider 7:1 contrast ratio")
        }
        
        return recommendations
    }
    
    /**
     * Get system font scale factor
     * Returns the system's font scale setting (0.85f to 2.0f)
     */
    fun getSystemFontScale(context: android.content.Context): Float {
        return context.resources.configuration.fontScale
    }
    
    /**
     * Check if system font scale is enabled
     */
    fun isSystemFontScaleEnabled(context: android.content.Context): Boolean {
        return getSystemFontScale(context) != 1.0f
    }
    
    /**
     * Get recommended font scale based on system settings
     * Maps Android system font scale (0.85f to 2.0f) to our scale options
     */
    fun getRecommendedFontScale(context: android.content.Context): com.makhabatusen.access_lab_app.ui.theme.FontSizeScale {
        val systemScale = getSystemFontScale(context)
        return when {
            systemScale <= 0.9f -> com.makhabatusen.access_lab_app.ui.theme.FontSizeScale.SMALL
            systemScale <= 1.1f -> com.makhabatusen.access_lab_app.ui.theme.FontSizeScale.MEDIUM
            systemScale <= 1.4f -> com.makhabatusen.access_lab_app.ui.theme.FontSizeScale.LARGE
            systemScale <= 1.7f -> com.makhabatusen.access_lab_app.ui.theme.FontSizeScale.EXTRA_LARGE
            else -> com.makhabatusen.access_lab_app.ui.theme.FontSizeScale.MAXIMUM
        }
    }
    
    /**
     * Check if accessibility services are enabled on the device
     * Returns true if any accessibility service is currently running
     */
    fun isAccessibilityEnabled(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        )
        return enabledServices.isNotEmpty()
    }
    
    /**
     * Check if TalkBack or similar screen reader is enabled
     */
    fun isScreenReaderEnabled(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_SPOKEN
        )
        return enabledServices.isNotEmpty()
    }
} 