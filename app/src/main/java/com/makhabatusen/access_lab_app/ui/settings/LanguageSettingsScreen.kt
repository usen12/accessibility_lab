package com.makhabatusen.access_lab_app.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.ui.util.isLandscape
import com.makhabatusen.access_lab_app.ui.util.isLargeTablet
import com.makhabatusen.access_lab_app.ui.util.isTablet
import com.makhabatusen.access_lab_app.ui.components.AccessibleFocusIndicator
import com.makhabatusen.access_lab_app.ui.components.FocusIndicatorStyle


/**
 * Language Settings Page for selecting app language
 */
@Composable
fun LanguageSettingsScreen(
    onBackPressed: () -> Unit,
    onRestartRequested: () -> Unit = {}
) {
    val languageViewModel = viewModel<LanguageSettingsViewModel>()
    val context = LocalContext.current
    val isLandscape = isLandscape()
    val isTablet = isTablet()
    
    // Handle back navigation
    BackHandler {
        onBackPressed()
    }
    
    // Initialize ViewModel on startup
    LaunchedEffect(Unit) {
        languageViewModel.initialize(context)
        languageViewModel.setRestartCallback(onRestartRequested)
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
    
    val settingsTitle = stringResource(R.string.language_settings_title)
    val backButtonDescription = stringResource(R.string.cd_accessibility_back_button)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar with Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AccessibleFocusIndicator(
                style = FocusIndicatorStyle.HIGH_CONTRAST
            ) {
                IconButton(
                    onClick = onBackPressed,
                    modifier = Modifier
                        .size(48.dp)
                        .semantics {
                            contentDescription = backButtonDescription
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = backButtonDescription,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = settingsTitle,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isTablet) 28.sp else 24.sp
                ),
                modifier = Modifier.semantics { heading() }
            )
        }
        
        // Main Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = padding)
                .widthIn(max = maxContentWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Language Selection Section
            LanguageSelectionSection(
                languageViewModel = languageViewModel,
                isTablet = isTablet
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Information Section
            LanguageInfoSection(isTablet = isTablet)
        }
    }
    
    // Restart Dialog
    if (languageViewModel.showRestartDialog) {
        RestartDialog(
            onConfirm = { 
                languageViewModel.onRestartConfirmed()
                // Recreate the activity to apply language changes
                // This will be handled by the activity
            },
            onDismiss = { languageViewModel.onRestartCancelled() }
        )
    }
}

/**
 * Language Selection Section
 */
@Composable
private fun LanguageSelectionSection(
    languageViewModel: LanguageSettingsViewModel,
    isTablet: Boolean
) {
    val selectTitle = stringResource(R.string.language_select_title)
    val selectDescription = stringResource(R.string.language_select_description)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Section Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_language),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = selectTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = if (isTablet) 20.sp else 18.sp
                    ),
                    modifier = Modifier.semantics { heading() }
                )
            }
            
            Text(
                text = selectDescription,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = if (isTablet) 16.sp else 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Language Options
            languageViewModel.availableLanguages.forEach { languageOption ->
                LanguageOptionItem(
                    languageOption = languageOption,
                    isSelected = languageViewModel.currentLanguage == languageOption.code,
                    onLanguageSelected = { languageViewModel.setLanguage(languageOption.code) },
                    isTablet = isTablet
                )
                
                if (languageOption.code != languageViewModel.availableLanguages.last().code) {
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

/**
 * Language Option Item
 */
@Composable
private fun LanguageOptionItem(
    languageOption: com.makhabatusen.access_lab_app.core.language.LanguageOption,
    isSelected: Boolean,
    onLanguageSelected: () -> Unit,
    isTablet: Boolean
) {
    val selectedState = stringResource(R.string.accessibility_state_selected)
    val notSelectedState = stringResource(R.string.accessibility_state_not_selected)
    val radioDescription = stringResource(
        R.string.cd_accessibility_toggle,
        languageOption.displayName,
        if (isSelected) stringResource(R.string.accessibility_on) else stringResource(R.string.accessibility_off)
    )
    
    AccessibleFocusIndicator(
        style = if (isSelected) FocusIndicatorStyle.HIGH_CONTRAST else FocusIndicatorStyle.DEFAULT
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
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .semantics {
                        contentDescription = radioDescription
                        stateDescription = if (isSelected) selectedState else notSelectedState
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = onLanguageSelected,
                    modifier = Modifier.size(if (isTablet) 28.dp else 24.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = languageOption.displayName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = if (isTablet) 18.sp else 16.sp
                    ),
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}

/**
 * Language Information Section
 */
@Composable
private fun LanguageInfoSection(isTablet: Boolean) {
    val infoTitle = stringResource(R.string.language_info_title)
    val bullet1 = stringResource(R.string.language_info_bullet_1)
    val bullet2 = stringResource(R.string.language_info_bullet_2)
    val bullet3 = stringResource(R.string.language_info_bullet_3)
    val bullet4 = stringResource(R.string.language_info_bullet_4)
    val bullet5 = stringResource(R.string.language_info_bullet_5)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = infoTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = if (isTablet) 18.sp else 16.sp
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "$bullet1\n$bullet2\n$bullet3\n$bullet4\n$bullet5",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = if (isTablet) 16.sp else 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = if (isTablet) 24.sp else 20.sp
            )
        }
    }
}

/**
 * Restart Dialog
 */
@Composable
private fun RestartDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val restartTitle = stringResource(R.string.language_restart_required)
    val restartMessage = stringResource(R.string.language_restart_message)
    val restartNow = stringResource(R.string.language_restart_now)
    val restartLater = stringResource(R.string.language_restart_later)
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = restartTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = restartMessage,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
            ) {
                Text(
                    text = restartNow,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(text = restartLater)
            }
        }
    )
} 