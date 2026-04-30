package com.makhabatusen.access_lab_app.ui.notes

import android.app.Application
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import android.widget.Toast
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.core.sensors.MotionSensorManager
import com.makhabatusen.access_lab_app.ui.settings.AccessibilitySettingsViewModel
import com.makhabatusen.access_lab_app.ui.theme.AccessibleLightBackground
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: Int?,
    onBack: () -> Unit
) {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory(context))
    val accessibilityViewModel = viewModel<AccessibilitySettingsViewModel>()
    val note = noteId?.let { viewModel.getNoteById(it) }
    var textState by remember(noteId) {
        mutableStateOf(TextFieldValue(note?.content ?: ""))
    }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    
    val isProtected = note?.isProtected ?: false
    
    // Get string resources
    val backButtonText = stringResource(R.string.cd_note_back_button)
    val screenTitle = when {
        note == null -> stringResource(R.string.note_add_title)
        isProtected -> stringResource(R.string.note_protected_title)
        else -> stringResource(R.string.note_edit_title)
    }

    fun saveNoteIfNeeded(): Boolean {
        val content = textState.text.trim()

        if (isProtected) return true
        if (content.isEmpty()) return false

        if (note == null) {
            return viewModel.saveNote(content)
        }

        if (content != note.content) {
            viewModel.updateNote(note.copy(content = content))
        }

        return true
    }


    // Motion sensor for shake-to-save functionality (second stage)
    val motionSensor = remember {
        MotionSensorManager(context) {
            if (isProtected) return@MotionSensorManager

            val saved = saveNoteIfNeeded()
            if (saved) {
                Toast.makeText(
                    context,
                    context.getString(R.string.note_saved_via_shake),
                    Toast.LENGTH_SHORT
                ).show()
                onBack()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.note_empty_cannot_save),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    // Lifecycle-aware motion sensor management - only when motion actuation is enabled
    DisposableEffect(lifecycleOwner, accessibilityViewModel.motionActuationEnabled) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (accessibilityViewModel.motionActuationEnabled) {
                        motionSensor.start()
                    }
                }
                Lifecycle.Event.ON_PAUSE -> motionSensor.stop()
                else -> {}
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            motionSensor.stop()
        }
    }
    
    // Handle motion actuation setting changes while screen is active
    LaunchedEffect(accessibilityViewModel.motionActuationEnabled) {
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            if (accessibilityViewModel.motionActuationEnabled) {
                motionSensor.start()
            } else {
                motionSensor.stop()
            }
        }
    }




    fun saveAndBack() {
        if (!isProtected) {
            saveNoteIfNeeded()
        }
        onBack()
    }

    BackHandler(enabled = true) {
        saveAndBack()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Custom top bar with Done button - responsive height for better text visibility
        val configuration = LocalConfiguration.current
        val isTablet = configuration.screenWidthDp >= 600
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        
        val topBarHeight = when {
            isTablet -> 64.dp
            isLandscape -> 60.dp
            else -> 56.dp
        }
        
        val buttonHeight = when {
            isTablet -> 56.dp
            isLandscape -> 52.dp
            else -> 48.dp
        }
        
        val textStyle = when {
            isTablet -> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            else -> MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(topBarHeight)
                .padding(start = 4.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { saveAndBack() },
                modifier = Modifier.semantics { contentDescription = backButtonText }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = backButtonText)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = screenTitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            if (!isProtected) {
                TextButton(
                    onClick = { saveAndBack() },
                    modifier = Modifier
                        .height(buttonHeight)
                        .padding(vertical = 2.dp)
                        .semantics { contentDescription = context.getString(R.string.cd_note_done_button) }
                ) {
                    Text(
                        text = stringResource(R.string.note_done_button),
                        style = textStyle
                    )
                }
            }
        }
        
        // Show protected note warning if applicable
        if (isProtected) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (MaterialTheme.colorScheme.background == AccessibleLightBackground) {
                        Color(0xFFE3F2FD) // Light blue background for light theme
                    } else {
                        Color(0xFF0D47A1) // Dark blue background for dark theme
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = context.getString(R.string.cd_note_protected_icon),
                        tint = if (MaterialTheme.colorScheme.background == AccessibleLightBackground) {
                            Color(0xFF0D47A1) // Dark blue for light theme
                        } else {
                            Color(0xFF90CAF9) // Light blue for dark theme
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.note_protected_info_message),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = if (MaterialTheme.colorScheme.background == AccessibleLightBackground) {
                            Color(0xFF0D47A1) // Dark blue for light theme
                        } else {
                            Color(0xFF90CAF9) // Light blue for dark theme
                        }
                    )
                }
            }
        }
        
        // Note input area takes the rest of the screen
        if (isProtected) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (MaterialTheme.colorScheme.background == AccessibleLightBackground) {
                        Color(0xFFFFF9C4) // Light yellow for light mode
                    } else {
                        Color(0xFF232323) // Very dark gray for dark mode
                    }
                ),
                border = BorderStroke(2.dp, if (MaterialTheme.colorScheme.background == AccessibleLightBackground) Color(0xFFBDB76B) else Color(0xFFFFF176)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                OutlinedTextField(
                    value = textState,
                    onValueChange = {}, // No editing allowed
                    label = { Text(stringResource(R.string.note_input_label)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .semantics { contentDescription = context.getString(R.string.cd_note_protected_content) },
                    singleLine = false,
                    minLines = 10,
                    maxLines = 40,
                    enabled = false,
                    readOnly = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = if (MaterialTheme.colorScheme.background == AccessibleLightBackground) Color(0xFF1C1B1F) else Color.White
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = if (MaterialTheme.colorScheme.background == AccessibleLightBackground) Color(0xFF1C1B1F) else Color.White,
                        disabledBorderColor = Color.Transparent,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContainerColor = Color.Transparent
                    )
                )
            }
        } else {
            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it },
                label = { Text(stringResource(R.string.note_input_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .semantics { contentDescription = context.getString(R.string.cd_note_input) },
                singleLine = false,
                minLines = 10,
                maxLines = 40,
                enabled = true,
                readOnly = false
            )
        }
    }
} 