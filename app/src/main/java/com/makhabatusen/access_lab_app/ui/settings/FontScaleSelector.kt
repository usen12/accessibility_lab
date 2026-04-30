package com.makhabatusen.access_lab_app.ui.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.ui.theme.FontSizeScale
import com.makhabatusen.access_lab_app.ui.theme.TypographyManager
import androidx.compose.ui.text.font.FontFamily


@Composable
fun FontScaleSelector(
    currentScale: FontSizeScale,
    onScaleSelected: (FontSizeScale) -> Unit,
    isTablet: Boolean = false
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    val fontScaleDescription = stringResource(R.string.accessibility_font_scale_desc)
    val currentScaleDescription = TypographyManager.getScaleDescription(context)
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { showDialog = true }
            .semantics { 
                contentDescription = "$fontScaleDescription, current: $currentScaleDescription"
                role = Role.Button
            },
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Icon(
                painter = painterResource(id = R.drawable.ic_format_size),
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
                    text = stringResource(R.string.accessibility_font_scale),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = fontScaleDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Current selection indicator
                Surface(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = currentScaleDescription,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Arrow indicator
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    
    // Font Scale Selection Dialog
    if (showDialog) {
        FontScaleDialog(
            currentScale = currentScale,
            onScaleSelected = { scale ->
                onScaleSelected(scale)
                showDialog = false
            },
            onDismiss = { showDialog = false },
            isTablet = isTablet
        )
    }
}

@Composable
private fun FontScaleDialog(
    currentScale: FontSizeScale,
    onScaleSelected: (FontSizeScale) -> Unit,
    onDismiss: () -> Unit,
    isTablet: Boolean
) {
    var selectedScale by remember { mutableStateOf(currentScale) }
    
    val dialogTitle = stringResource(R.string.accessibility_font_scale_dialog_title)
    val dialogDescription = stringResource(R.string.accessibility_font_scale_dialog_desc)
    val previewTitle = stringResource(R.string.accessibility_font_scale_preview)
    val previewSample = stringResource(R.string.accessibility_font_scale_preview_sample)
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = if (isTablet) 600.dp else 400.dp)
                .heightIn(max = if (isTablet) 700.dp else 600.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(if (isTablet) 32.dp else 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Dialog Header
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .semantics { heading() }
                )
                
                Text(
                    text = dialogDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )
                
                // Font Scale Options
                FontScaleOptions(
                    selectedScale = selectedScale,
                    onScaleSelected = { selectedScale = it },
                    isTablet = isTablet
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Preview Section
                Text(
                    text = previewTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .semantics { heading() }
                )
                
                // Preview Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Create preview typography with the selected scale
                        val previewTypography = createPreviewTypography(selectedScale)
                        
                        // Sample heading
                        Text(
                            text = stringResource(R.string.accessibility_font_scale_preview_heading),
                            style = previewTypography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Sample body text
                        Text(
                            text = stringResource(R.string.accessibility_font_scale_preview_body),
                            style = previewTypography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Sample smaller text
                        Text(
                            text = stringResource(R.string.accessibility_font_scale_preview_sample, getScaleDescription(selectedScale)),
                            style = previewTypography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Sample label text
                        Text(
                            text = stringResource(R.string.accessibility_font_scale_preview_label),
                            style = previewTypography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.profile_cancel_editing),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    
                    Button(
                        onClick = { onScaleSelected(selectedScale) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.profile_save_name),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FontScaleOptions(
    selectedScale: FontSizeScale,
    onScaleSelected: (FontSizeScale) -> Unit,
    isTablet: Boolean
) {
    val options = FontSizeScale.values()
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { scale ->
            FontScaleOption(
                scale = scale,
                isSelected = scale == selectedScale,
                onSelect = { onScaleSelected(scale) },
                isTablet = isTablet
            )
        }
    }
}

@Composable
private fun FontScaleOption(
    scale: FontSizeScale,
    isSelected: Boolean,
    onSelect: () -> Unit,
    isTablet: Boolean
) {
    val scaleDescription = when (scale) {
        FontSizeScale.SMALL -> stringResource(R.string.accessibility_font_scale_small)
        FontSizeScale.MEDIUM -> stringResource(R.string.accessibility_font_scale_medium)
        FontSizeScale.LARGE -> stringResource(R.string.accessibility_font_scale_large)
        FontSizeScale.EXTRA_LARGE -> stringResource(R.string.accessibility_font_scale_extra_large)
        FontSizeScale.MAXIMUM -> stringResource(R.string.accessibility_font_scale_maximum)
    }
    
    val optionDescription = stringResource(
        R.string.cd_accessibility_toggle,
        scaleDescription,
        if (isSelected) stringResource(R.string.accessibility_on) else stringResource(R.string.accessibility_off)
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .semantics { 
                contentDescription = optionDescription
                role = Role.RadioButton
                selected = isSelected
            },
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio button indicator
            Surface(
                modifier = Modifier.size(20.dp),
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                }
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(2.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Scale description
            Text(
                text = scaleDescription,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                ),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Create typography with a specific font scale for preview
 */
private fun createPreviewTypography(scale: FontSizeScale): androidx.compose.material3.Typography {
    val scaleFactor = when (scale) {
        FontSizeScale.SMALL -> 0.85f
        FontSizeScale.MEDIUM -> 1.0f
        FontSizeScale.LARGE -> 1.25f
        FontSizeScale.EXTRA_LARGE -> 1.5f
        FontSizeScale.MAXIMUM -> 2.0f
    }
    
    val scaleFontSize = { baseSize: Float -> baseSize * scaleFactor }
    
    return androidx.compose.material3.Typography(
        headlineSmall = androidx.compose.ui.text.TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = scaleFontSize(24f).sp,
            lineHeight = scaleFontSize(32f).sp,
            letterSpacing = 0.sp
        ),
        bodyLarge = androidx.compose.ui.text.TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = scaleFontSize(16f).sp,
            lineHeight = scaleFontSize(24f).sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = androidx.compose.ui.text.TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = scaleFontSize(14f).sp,
            lineHeight = scaleFontSize(20f).sp,
            letterSpacing = 0.25.sp
        ),
        labelLarge = androidx.compose.ui.text.TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = scaleFontSize(14f).sp,
            lineHeight = scaleFontSize(20f).sp,
            letterSpacing = 0.1.sp
        )
    )
}

/**
 * Get scale description for a specific font scale
 */
@Composable
private fun getScaleDescription(scale: FontSizeScale): String = when (scale) {
    FontSizeScale.SMALL -> stringResource(R.string.accessibility_font_scale_small_desc)
    FontSizeScale.MEDIUM -> stringResource(R.string.accessibility_font_scale_medium_desc)
    FontSizeScale.LARGE -> stringResource(R.string.accessibility_font_scale_large_desc)
    FontSizeScale.EXTRA_LARGE -> stringResource(R.string.accessibility_font_scale_extra_large_desc)
    FontSizeScale.MAXIMUM -> stringResource(R.string.accessibility_font_scale_maximum_desc)
}