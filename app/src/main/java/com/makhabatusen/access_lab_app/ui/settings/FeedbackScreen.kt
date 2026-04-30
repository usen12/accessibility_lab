package com.makhabatusen.access_lab_app.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.ui.util.isLandscape
import com.makhabatusen.access_lab_app.ui.util.isLargeTablet
import com.makhabatusen.access_lab_app.ui.util.isTablet
import com.makhabatusen.access_lab_app.ui.components.AccessibleFocusIndicator
import com.makhabatusen.access_lab_app.ui.components.FocusIndicatorStyle
import com.makhabatusen.access_lab_app.ui.components.UnifiedButton
import com.makhabatusen.access_lab_app.ui.components.UnifiedButtonType

/**
 * Feedback Page for users to share thoughts and report issues
 * Implements comprehensive accessibility features following BITV 2.0 standards
 */
@Composable
fun FeedbackScreen(onBackPressed: () -> Unit) {
    val feedbackViewModel = viewModel<FeedbackViewModel>()
    val context = LocalContext.current
    val isLandscape = isLandscape()
    val isTablet = isTablet()
    
    // Handle back navigation
    BackHandler {
        onBackPressed()
    }
    
    // Load device info on startup
    LaunchedEffect(Unit) {
        feedbackViewModel.loadDeviceInfo(context)
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
    
    val feedbackTitle = stringResource(R.string.feedback_title)
    val backButtonDescription = stringResource(R.string.cd_feedback_back_button)
    
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
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = feedbackTitle,
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
                        contentDescription = feedbackTitle
                        heading()
                    }
            )
        }
        
        // Feedback form content
        Column(
            modifier = Modifier.widthIn(max = maxContentWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Feedback Type Selection
            FeedbackTypeSection(
                selectedType = feedbackViewModel.selectedFeedbackType,
                onTypeSelected = { feedbackViewModel.updateFeedbackType(it) },
                isTablet = isTablet
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Feedback Content
            FeedbackContentSection(
                feedbackText = feedbackViewModel.feedbackText,
                onFeedbackTextChange = { feedbackViewModel.updateFeedbackText(it) },
                onSubmit = { feedbackViewModel.submitFeedback(context) },
                isTablet = isTablet
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Device Information
            DeviceInfoSection(
                deviceInfo = feedbackViewModel.deviceInfo,
                isTablet = isTablet
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Submit Button
            SubmitFeedbackButton(
                onSubmit = { feedbackViewModel.submitFeedback(context) },
                isEnabled = feedbackViewModel.isFormValid,
                isLoading = feedbackViewModel.isSubmitting,
                isTablet = isTablet
            )
        }
    }
    
    // Success Dialog
    if (feedbackViewModel.showSuccessDialog) {
        FeedbackSuccessDialog(
            onDismiss = { feedbackViewModel.hideSuccessDialog() },
            onBackToSettings = {
                feedbackViewModel.hideSuccessDialog()
                onBackPressed()
            }
        )
    }
    

}

@Composable
private fun FeedbackTypeSection(
    selectedType: FeedbackType,
    onTypeSelected: (FeedbackType) -> Unit,
    isTablet: Boolean
) {
    val sectionTitle = stringResource(R.string.feedback_type_title)
    val sectionDescription = stringResource(R.string.feedback_type_description)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { 
                contentDescription = sectionTitle
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
            Text(
                text = sectionTitle,
                style = if (isTablet) {
                    MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .semantics { heading() }
            )
            
            Text(
                text = sectionDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            // Feedback Type Options
            FeedbackType.values().forEach { feedbackType ->
                FeedbackTypeOption(
                    feedbackType = feedbackType,
                    isSelected = selectedType == feedbackType,
                    onTypeSelected = onTypeSelected,
                    isTablet = isTablet
                )
                
                if (feedbackType != FeedbackType.values().last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun FeedbackTypeOption(
    feedbackType: FeedbackType,
    isSelected: Boolean,
    onTypeSelected: (FeedbackType) -> Unit,
    isTablet: Boolean
) {
    val radioDescription = stringResource(
        R.string.cd_feedback_type_option,
        stringResource(feedbackType.getDisplayNameRes()),
        if (isSelected) stringResource(R.string.accessibility_on) else stringResource(R.string.accessibility_off)
    )
    
    AccessibleFocusIndicator(
        style = if (isSelected) FocusIndicatorStyle.HIGH_CONTRAST else FocusIndicatorStyle.DEFAULT
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = Color.Transparent,
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .semantics {
                        contentDescription = radioDescription
                        stateDescription = if (isSelected) "selected" else "not selected"
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { onTypeSelected(feedbackType) },
                    modifier = Modifier.size(if (isTablet) 28.dp else 24.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = stringResource(feedbackType.getDisplayNameRes()),
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
                    
                    Text(
                        text = stringResource(feedbackType.getDescriptionRes()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = if (isTablet) 14.sp else 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedbackContentSection(
    feedbackText: String,
    onFeedbackTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isTablet: Boolean
) {
    val sectionTitle = stringResource(R.string.feedback_content_title)
    val feedbackHint = stringResource(R.string.feedback_content_hint)
    val feedbackLabel = stringResource(R.string.feedback_content_label)
    val feedbackDescription = stringResource(R.string.feedback_content_description)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { 
                contentDescription = sectionTitle
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
            Text(
                text = sectionTitle,
                style = if (isTablet) {
                    MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .semantics { heading() }
            )
            
            Text(
                text = feedbackDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            // Feedback Text Input
            val feedbackTextFieldDescription = stringResource(R.string.cd_feedback_text_field)
            val focusManager = LocalFocusManager.current
            
            OutlinedTextField(
                value = feedbackText,
                onValueChange = onFeedbackTextChange,
                label = { Text(feedbackLabel) },
                placeholder = { Text(feedbackHint) },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = feedbackTextFieldDescription
                    },
                minLines = 5,
                maxLines = 10,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onDone = {
                        // Just close the keyboard, don't submit
                        focusManager.clearFocus()
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = if (isTablet) 16.sp else 14.sp
                )
            )
        }
    }
}

@Composable
private fun DeviceInfoSection(
    deviceInfo: DeviceInfo,
    isTablet: Boolean
) {
    val sectionTitle = stringResource(R.string.feedback_device_info_title)
    val sectionDescription = stringResource(R.string.feedback_device_info_description)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { 
                contentDescription = sectionTitle
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
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = sectionTitle,
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
            
            Text(
                text = sectionDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            // Device Info Items
            DeviceInfoItem(
                label = stringResource(R.string.feedback_device_info_app_version),
                value = deviceInfo.appVersion,
                isTablet = isTablet
            )
            
            DeviceInfoItem(
                label = stringResource(R.string.feedback_device_info_android_version),
                value = deviceInfo.androidVersion,
                isTablet = isTablet
            )
            
            DeviceInfoItem(
                label = stringResource(R.string.feedback_device_info_device_model),
                value = deviceInfo.deviceModel,
                isTablet = isTablet
            )
            
            DeviceInfoItem(
                label = stringResource(R.string.feedback_device_info_screen_resolution),
                value = deviceInfo.screenResolution,
                isTablet = isTablet
            )
        }
    }
}

@Composable
private fun DeviceInfoItem(
    label: String,
    value: String,
    isTablet: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = if (isTablet) 16.sp else 14.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = if (isTablet) 16.sp else 14.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SubmitFeedbackButton(
    onSubmit: () -> Unit,
    isEnabled: Boolean,
    isLoading: Boolean,
    isTablet: Boolean
) {
    val submitButtonText = stringResource(R.string.feedback_submit_button)
    val submitButtonDescription = stringResource(R.string.cd_feedback_submit_button)
    
    UnifiedButton(
        onClick = onSubmit,
        text = submitButtonText,
        icon = Icons.Default.Send,
        contentDescription = submitButtonDescription,
        buttonType = UnifiedButtonType.PRIMARY,
        maxWidth = true,
        enabled = isEnabled && !isLoading,
        loading = isLoading
    )
}

@Composable
private fun FeedbackSuccessDialog(
    onDismiss: () -> Unit,
    onBackToSettings: () -> Unit
) {
    val dialogTitle = stringResource(R.string.feedback_success_title)
    val dialogMessage = stringResource(R.string.feedback_success_message)
    val dismissButton = stringResource(R.string.feedback_success_dismiss)
    val backButton = stringResource(R.string.feedback_success_back_to_settings)
    val dialogDescription = stringResource(R.string.cd_feedback_success_dialog)
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = dialogTitle,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.semantics { heading() }
            )
        },
        text = {
            Text(
                text = dialogMessage,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            val backButtonDescription = stringResource(R.string.cd_feedback_success_back_button)
            TextButton(
                onClick = onBackToSettings,
                modifier = Modifier.semantics {
                    contentDescription = backButtonDescription
                }
            ) {
                Text(backButton)
            }
        },
        dismissButton = {
            val dismissButtonDescription = stringResource(R.string.cd_feedback_success_dismiss_button)
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.semantics {
                    contentDescription = dismissButtonDescription
                }
            ) {
                Text(dismissButton)
            }
        },
        modifier = Modifier.semantics {
            contentDescription = dialogDescription
        }
    )
}

 