package com.makhabatusen.access_lab_app.ui.notes

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.data.notes.local.Note
import com.makhabatusen.access_lab_app.ui.settings.AccessibilitySettingsViewModel
import com.makhabatusen.access_lab_app.ui.theme.AccessibleLightBackground
import com.makhabatusen.access_lab_app.core.sensors.MotionSensorManager
import com.makhabatusen.access_lab_app.ui.components.NotesTopBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onEditNote: (noteId: Int?) -> Unit
) {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory(context))
    val accessibilityViewModel = viewModel<AccessibilitySettingsViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val notes = uiState.notes

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    // Motion sensor for shake-to-open-note functionality (first stage)
    val motionSensor = remember {
        MotionSensorManager(context) {
            onEditNote(null) // Open new note screen
            Toast.makeText(
                context,
                context.getString(R.string.note_opened_via_shake),
                Toast.LENGTH_SHORT
            ).show()
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


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEditNote(null) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    // 48.dp touch target for accessibility compliance
                    .size(48.dp)
                    .semantics {
                        contentDescription = context.getString(R.string.cd_note_add_action)
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_note_add),
                    contentDescription = context.getString(R.string.cd_note_add_action)
                )
            }
        },
        topBar = {
            NotesTopBar()
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .semantics { contentDescription = context.getString(R.string.notes_list_container) }
        ) {
            if (notes.isEmpty()) {
                Text(
                    text = stringResource(R.string.note_empty_state_message),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .semantics {
                            contentDescription =
                                context.getString(R.string.cd_note_empty_state_message)
                        },
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .semantics {
                            contentDescription = context.getString(R.string.cd_notes_list)
                        },
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notes, key = { it.id }) { note ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Allow viewing all notes (both protected and non-protected)
                                    onEditNote(note.id)
                                }
                                .semantics {
                                    contentDescription = if (note.isProtected) {
                                        context.getString(
                                            R.string.note_view_protected,
                                            note.content
                                        )
                                    } else {
                                        context.getString(R.string.note_view_item)
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = note.content,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (!note.isProtected) {
                                        IconButton(
                                            onClick = {
                                                noteToDelete = note
                                                showDeleteDialog = true
                                            },
                                            modifier = Modifier
                                                .semantics {
                                                    contentDescription =
                                                        context.getString(R.string.cd_note_delete_action)
                                                }
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = context.getString(R.string.cd_note_delete_action),
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = stringResource(
                                        R.string.note_created_time_label,
                                        SimpleDateFormat(
                                            "MMM dd, yyyy HH:mm",
                                            Locale.getDefault()
                                        ).format(Date(note.timestamp))
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.semantics {
                                        contentDescription =
                                            context.getString(R.string.cd_note_created_time)
                                    }
                                )

                                if (note.isProtected) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(
                                                color = if (MaterialTheme.colorScheme.background == AccessibleLightBackground) {
                                                    Color(0xFFE3F2FD) // Light blue background for light theme
                                                } else {
                                                    Color(0xFF0D47A1) // Dark blue background for dark theme
                                                },
                                                shape = MaterialTheme.shapes.small
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                            .semantics {
                                                contentDescription =
                                                    context.getString(R.string.cd_note_protected_badge)
                                            }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_lock),
                                            contentDescription = context.getString(R.string.cd_note_protected_icon),
                                            tint = if (MaterialTheme.colorScheme.background == AccessibleLightBackground) {
                                                Color(0xFF0D47A1) // Dark blue for light theme
                                            } else {
                                                Color(0xFF90CAF9) // Light blue for dark theme
                                            },
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = stringResource(R.string.note_protected_badge),
                                            style = MaterialTheme.typography.bodySmall.copy(
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
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && noteToDelete != null) {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text(stringResource(R.string.note_delete_dialog_title))
            },
            text = {
                Text(stringResource(R.string.note_delete_dialog_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        noteToDelete?.let { viewModel.deleteNote(it) }
                        noteToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.note_delete_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                    }
                ) {
                    Text(stringResource(R.string.note_delete_dialog_cancel))
                }
            }
        )
    }
}