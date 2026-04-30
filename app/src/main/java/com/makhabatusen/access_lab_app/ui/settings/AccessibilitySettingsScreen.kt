package com.makhabatusen.access_lab_app.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.ui.util.isLandscape
import com.makhabatusen.access_lab_app.ui.util.isLargeTablet
import com.makhabatusen.access_lab_app.ui.util.isTablet
import com.makhabatusen.access_lab_app.ui.components.AccessibleFocusIndicator
import com.makhabatusen.access_lab_app.ui.components.FocusIndicatorStyle
import com.makhabatusen.access_lab_app.ui.theme.rememberThemeManager
import com.makhabatusen.access_lab_app.ui.settings.AccessibilitySettingsViewModel


@Composable
fun AccessibilitySettingsScreen(onBackPressed: () -> Unit) {
    val accessibilityViewModel = viewModel<AccessibilitySettingsViewModel>()
    val themeManager = rememberThemeManager()
    val context = LocalContext.current
    val isLandscape = isLandscape()
    val isTablet = isTablet()
    
    // Handle back navigation
    BackHandler {
        onBackPressed()
    }
    
    // Load settings on startup
    LaunchedEffect(Unit) {
        accessibilityViewModel.loadSettings(context, themeManager)
    }
    
    val padding = when {
        isLargeTablet() -> 56.dp
        isTablet -> 48.dp
        isLandscape -> 40.dp
        else -> 24.dp
    }
    
    val maxContentWidth = when {
        isLargeTablet() -> 1000.dp
        isTablet -> 800.dp
        isLandscape -> 600.dp
        else -> 400.dp
    }
    
    val settingsTitle = stringResource(R.string.accessibility_settings_title)
    val backButtonDescription = stringResource(R.string.cd_accessibility_back_button)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackPressed,
                modifier = Modifier.semantics { 
                    contentDescription = backButtonDescription
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = settingsTitle,
                style = if (isTablet) {
                    MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .semantics { 
                        contentDescription = settingsTitle
                        heading()
                    }
            )
        }
        
        // Settings content
        Column(
            modifier = Modifier.widthIn(max = maxContentWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Visual Accessibility Section
            AccessibilitySection(
                title = stringResource(R.string.accessibility_visual_title),
                iconRes = R.drawable.ic_accessibility_visual,
                isTablet = isTablet
            ) {
                AccessibilityToggleItem(
                    title = stringResource(R.string.accessibility_dark_mode),
                    description = stringResource(R.string.accessibility_dark_mode_desc),
                    iconRes = R.drawable.ic_dark_mode,
                    isEnabled = accessibilityViewModel.darkMode,
                    onToggle = { 
                        accessibilityViewModel.updateDarkMode(it)
                    },
                    isTablet = isTablet,
                    isHighPriority = true // Dark mode is a high priority setting
                )
                
                AccessibilityToggleItem(
                    title = stringResource(R.string.accessibility_high_contrast),
                    description = stringResource(R.string.accessibility_high_contrast_desc),
                    iconRes = R.drawable.ic_high_contrast,
                    isEnabled = accessibilityViewModel.highContrast,
                    onToggle = { accessibilityViewModel.updateHighContrast(it) },
                    isTablet = isTablet
                )
                
                // Font Scale Selector (replaces simple large text toggle)
                FontScaleSelector(
                    currentScale = accessibilityViewModel.fontScale,
                    onScaleSelected = { scale ->
                        accessibilityViewModel.updateFontScale(scale)
                    },
                    isTablet = isTablet
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Interaction Accessibility Section
            AccessibilitySection(
                title = stringResource(R.string.accessibility_interaction_title),
                iconRes = R.drawable.ic_accessibility_interaction,
                isTablet = isTablet
            ) {
                AccessibilityToggleItem(
                    title = stringResource(R.string.accessibility_motion_actuation),
                    description = stringResource(R.string.accessibility_motion_actuation_desc),
                    iconRes = R.drawable.ic_gesture_navigation,
                    isEnabled = accessibilityViewModel.motionActuationEnabled,
                    onToggle = { accessibilityViewModel.updateMotionActuation(it) },
                    isTablet = isTablet
                )
            }
        }
    }
}

@Composable
private fun AccessibilitySection(
    title: String,
    iconRes: Int,
    isTablet: Boolean,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { 
                contentDescription = title
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isTablet) 8.dp else 4.dp
        ),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(
                if (isTablet) 32.dp else 24.dp
            )
        ) {
            // Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = title,
                    style = if (isTablet) {
                        MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    modifier = Modifier.semantics { heading() }
                )
            }
            
            // Section Content
            content()
        }
    }
}

@Composable
private fun AccessibilityToggleItem(
    title: String,
    description: String,
    iconRes: Int,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    isTablet: Boolean,
    isHighPriority: Boolean = false
) {
    val toggleDescription = stringResource(
        R.string.cd_accessibility_toggle,
        title,
        if (isEnabled) stringResource(R.string.accessibility_on) else stringResource(R.string.accessibility_off)
    )
    
    val focusIndicatorStyle = if (isHighPriority) {
        FocusIndicatorStyle.HIGH_CONTRAST
    } else {
        FocusIndicatorStyle.DEFAULT
    }
    
    AccessibleFocusIndicator(
        style = focusIndicatorStyle
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = Color.Transparent,
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(20.dp))
                
                // Text Content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isHighPriority) FontWeight.SemiBold else FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Toggle Switch
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle,
                    modifier = Modifier
                        .semantics { 
                            contentDescription = toggleDescription
                            role = Role.Switch
                            if (isHighPriority) {
                                heading()
                            }
                        },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }
    }
} 