package com.makhabatusen.access_lab_app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.luminance

/**
 * Enhanced focus indicator for accessibility compliance.
 * Provides multiple focus indicator styles that meet BITV 2.0 and WCAG guidelines.
 */
@Composable
fun AccessibleFocusIndicator(
    modifier: Modifier = Modifier,
    isFocused: Boolean = false,
    style: FocusIndicatorStyle = FocusIndicatorStyle.DEFAULT,
    content: @Composable () -> Unit
) {
    val focusColor = when (style) {
        FocusIndicatorStyle.DEFAULT -> MaterialTheme.colorScheme.primary
        FocusIndicatorStyle.HIGH_CONTRAST -> if (MaterialTheme.colorScheme.isLight) {
            Color.Black
        } else {
            Color.White
        }
        FocusIndicatorStyle.COLORED -> MaterialTheme.colorScheme.tertiary
    }
    
    val focusWidth = when (style) {
        FocusIndicatorStyle.DEFAULT -> 2.dp
        FocusIndicatorStyle.HIGH_CONTRAST -> 3.dp
        FocusIndicatorStyle.COLORED -> 2.dp
    }
    
    Box(
        modifier = modifier
            .then(
                if (isFocused) {
                    Modifier.border(
                        width = focusWidth,
                        color = focusColor,
                        shape = MaterialTheme.shapes.small
                    )
                } else {
                    Modifier
                }
            )
    ) {
        content()
    }
}

/**
 * Focus indicator styles for different accessibility needs
 */
enum class FocusIndicatorStyle {
    /**
     * Default focus indicator using theme primary color
     */
    DEFAULT,
    
    /**
     * High contrast focus indicator for enhanced visibility
     */
    HIGH_CONTRAST,
    
    /**
     * Colored focus indicator using tertiary color
     */
    COLORED
}

/**
 * Extension property to check if color scheme is light
 */
val androidx.compose.material3.ColorScheme.isLight: Boolean
    get() = this.background.luminance() > 0.5


