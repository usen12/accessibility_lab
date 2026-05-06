package com.makhabatusen.access_lab_app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

// Accessible Light Color Scheme
private val AccessibleLightColorScheme = lightColorScheme(
    primary = AccessibleLightPrimary,
    onPrimary = AccessibleLightOnPrimary,
    secondary = AccessibleLightSecondary,
    onSecondary = AccessibleLightOnSecondary,
    tertiary = AccessibleLightTertiary,
    onTertiary = AccessibleLightOnTertiary,
    background = AccessibleLightBackground,
    onBackground = AccessibleLightOnBackground,
    surface = AccessibleLightSurface,
    onSurface = AccessibleLightOnSurface,
    surfaceVariant = AccessibleLightSurfaceVariant,
    onSurfaceVariant = AccessibleLightOnSurfaceVariant,
    outline = AccessibleLightOutline,
    outlineVariant = AccessibleLightOutlineVariant
)

// Accessible Dark Color Scheme
private val AccessibleDarkColorScheme = darkColorScheme(
    primary = AccessibleDarkPrimary,
    onPrimary = AccessibleDarkOnPrimary,
    secondary = AccessibleDarkSecondary,
    onSecondary = AccessibleDarkOnSecondary,
    tertiary = AccessibleDarkTertiary,
    onTertiary = AccessibleDarkOnTertiary,
    background = AccessibleDarkBackground,
    onBackground = AccessibleDarkOnBackground,
    surface = AccessibleDarkSurface,
    onSurface = AccessibleDarkOnSurface,
    surfaceVariant = AccessibleDarkSurfaceVariant,
    onSurfaceVariant = AccessibleDarkOnSurfaceVariant,
    outline = AccessibleDarkOutline,
    outlineVariant = AccessibleDarkOutlineVariant
)

// High Contrast Light Color Scheme
private val HighContrastLightColorScheme = lightColorScheme(
    primary = HighContrastLightPrimary,
    onPrimary = HighContrastLightOnPrimary,
    background = HighContrastLightBackground,
    onBackground = HighContrastLightOnBackground,
    surface = HighContrastLightSurface,
    onSurface = HighContrastLightOnSurface
)

// High Contrast Dark Color Scheme
private val HighContrastDarkColorScheme = darkColorScheme(
    primary = HighContrastDarkPrimary,
    onPrimary = HighContrastDarkOnPrimary,
    background = HighContrastDarkBackground,
    onBackground = HighContrastDarkOnBackground,
    surface = HighContrastDarkSurface,
    onSurface = HighContrastDarkOnSurface
)

@Composable
fun AccessLabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        highContrast && darkTheme -> HighContrastDarkColorScheme
        highContrast && !darkTheme -> HighContrastLightColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> AccessibleDarkColorScheme
        else -> AccessibleLightColorScheme
    }

    // Make typography reactive to font scale changes
    val currentFontScale by TypographyManager.getScaleState()
    val typography = remember(currentFontScale) {
        TypographyManager.getTypography()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

/**
 * Theme composable that integrates with ThemeManager
 */
@Composable
fun AccessLabThemeWithManager(
    themeManager: ThemeManager,
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val themeMode by themeManager.themeMode.collectAsState()
    val systemDark = isSystemInDarkTheme()

    val isDark = when (themeMode) {
        ThemeManager.THEME_DARK  -> true
        ThemeManager.THEME_LIGHT -> false
        else                     -> systemDark
    }

    val currentFontScale by TypographyManager.getScaleState()

    AccessLabTheme(
        darkTheme = isDark,
        highContrast = highContrast,
        content = content
    )
}

/**
 * Legacy theme function for backward compatibility
 */
@Composable
fun AccessLabTheme(
    content: @Composable () -> Unit
) {
    AccessLabTheme(
        darkTheme = isSystemInDarkTheme(),
        content = content
    )
}