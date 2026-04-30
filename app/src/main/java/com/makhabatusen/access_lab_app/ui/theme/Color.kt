package com.makhabatusen.access_lab_app.ui.theme

import androidx.compose.ui.graphics.Color


// Accessible Color Scheme for Light Mode (WCAG AA compliant)
val AccessibleLightPrimary = Color(0xFF1976D2) // Blue 700 - 4.5:1 contrast
val AccessibleLightOnPrimary = Color(0xFFFFFFFF) // White
val AccessibleLightSecondary = Color(0xFF424242) // Grey 800 - 4.5:1 contrast
val AccessibleLightOnSecondary = Color(0xFFFFFFFF) // White
val AccessibleLightTertiary = Color(0xFF2E7D32) // Green 800 - 4.5:1 contrast
val AccessibleLightOnTertiary = Color(0xFFFFFFFF) // White
val AccessibleLightBackground = Color(0xFFFAFAFA) // Grey 50
val AccessibleLightOnBackground = Color(0xFF1C1B1F) // Grey 900 - 4.5:1 contrast
val AccessibleLightSurface = Color(0xFFFFFFFF) // White
val AccessibleLightOnSurface = Color(0xFF1C1B1F) // Grey 900 - 4.5:1 contrast
val AccessibleLightSurfaceVariant = Color(0xFFF3F3F3) // Grey 100
val AccessibleLightOnSurfaceVariant = Color(0xFF424242) // Grey 800 - 4.5:1 contrast
val AccessibleLightOutline = Color(0xFF757575) // Grey 600 - 3:1 contrast
val AccessibleLightOutlineVariant = Color(0xFFBDBDBD) // Grey 400

// Accessible Color Scheme for Dark Mode (WCAG AA compliant)
val AccessibleDarkPrimary = Color(0xFF90CAF9) // Blue 200 - 4.5:1 contrast
val AccessibleDarkOnPrimary = Color(0xFF0D47A1) // Blue 900
val AccessibleDarkSecondary = Color(0xFFBDBDBD) // Grey 400 - 4.5:1 contrast
val AccessibleDarkOnSecondary = Color(0xFF212121) // Grey 900
// Fixed dark theme tertiary colors (WCAG AA compliant - 4.5:1+ contrast)
val AccessibleDarkTertiary = Color(0xFF4CAF50) // Green 500 - better contrast
val AccessibleDarkOnTertiary = Color(0xFF000000) // Black - maximum contrast
val AccessibleDarkBackground = Color(0xFF121212) // Grey 900
val AccessibleDarkOnBackground = Color(0xFFE0E0E0) // Grey 300 - 4.5:1 contrast
val AccessibleDarkSurface = Color(0xFF1E1E1E) // Grey 850
val AccessibleDarkOnSurface = Color(0xFFE0E0E0) // Grey 300 - 4.5:1 contrast
val AccessibleDarkSurfaceVariant = Color(0xFF2D2D2D) // Grey 800
val AccessibleDarkOnSurfaceVariant = Color(0xFFBDBDBD) // Grey 400 - 4.5:1 contrast
val AccessibleDarkOutline = Color(0xFF9E9E9E) // Grey 500 - 3:1 contrast
val AccessibleDarkOutlineVariant = Color(0xFF616161) // Grey 700

// High Contrast Colors (Enhanced accessibility)
val HighContrastLightPrimary = Color(0xFF0D47A1) // Blue 900 - 7:1 contrast
val HighContrastLightOnPrimary = Color(0xFFFFFFFF) // White
val HighContrastLightBackground = Color(0xFFFFFFFF) // White
val HighContrastLightOnBackground = Color(0xFF000000) // Black - 21:1 contrast
val HighContrastLightSurface = Color(0xFFFFFFFF) // White
val HighContrastLightOnSurface = Color(0xFF000000) // Black - 21:1 contrast

val HighContrastDarkPrimary = Color(0xFF64B5F6) // Blue 300 - 7:1 contrast
val HighContrastDarkOnPrimary = Color(0xFF000000) // Black
val HighContrastDarkBackground = Color(0xFF000000) // Black
val HighContrastDarkOnBackground = Color(0xFFFFFFFF) // White - 21:1 contrast
val HighContrastDarkSurface = Color(0xFF000000) // Black
val HighContrastDarkOnSurface = Color(0xFFFFFFFF) // White - 21:1 contrast

// Semantic Colors for Quiz App (Accessible versions)
val QuizContainerGreen = Color(0xFF2E7D32) // Green 800 - 4.5:1 contrast
val QuizContainerGreenDark = Color(0xFF1B5E20) // Green 900 - 4.5:1 contrast

// Status Colors (Accessible versions)
val SuccessGreen = Color(0xFF2E7D32) // Green 800 - 4.5:1 contrast
// Fixed warning orange color (WCAG AA compliant - 4.5:1+ contrast)
val WarningOrange = Color(0xFFBF360C) // Orange 900 - better contrast (4.5:1+)
val ErrorRed = Color(0xFFC62828) // Red 800 - 4.5:1 contrast
val InfoBlue = Color(0xFF1565C0) // Blue 800 - 4.5:1 contrast

// Focus and Selection Colors
val FocusIndicatorLight = Color(0xFF1976D2) // Blue 700
val FocusIndicatorDark = Color(0xFF90CAF9) // Blue 200
val SelectionLight = Color(0xFFE3F2FD) // Blue 50
val SelectionDark = Color(0xFF0D47A1) // Blue 900

// Disabled State Colors
val DisabledLight = Color(0xFFBDBDBD) // Grey 400
val DisabledDark = Color(0xFF616161) // Grey 700
val OnDisabledLight = Color(0xFF757575) // Grey 600
val OnDisabledDark = Color(0xFF9E9E9E) // Grey 500