package com.makhabatusen.access_lab_app.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.makhabatusen.access_lab_app.ui.settings.AccessibilitySettingsScreen
import com.makhabatusen.access_lab_app.ui.settings.FeedbackScreen
import com.makhabatusen.access_lab_app.ui.settings.LanguageSettingsScreen
import com.makhabatusen.access_lab_app.ui.settings.SettingsScreen
import com.makhabatusen.access_lab_app.ui.notes.NotesScreen
import com.makhabatusen.access_lab_app.ui.home.HomeScreen
import com.makhabatusen.access_lab_app.ui.quiz.QuizScreen
import com.makhabatusen.access_lab_app.ui.media.pages.VideoLibraryScreen
import com.makhabatusen.access_lab_app.ui.util.isLandscape
import com.makhabatusen.access_lab_app.ui.util.isTablet
import com.makhabatusen.access_lab_app.ui.notes.NoteEditScreen
import com.makhabatusen.access_lab_app.ui.theme.rememberThemeManager
import com.makhabatusen.access_lab_app.R

sealed class Screen(val route: String, val title: String, val icon: @Composable (() -> Unit)? = null) {
    object Home : Screen("home", "Home", { Icon(Icons.Default.Home, contentDescription = "Home") })
    object Quiz : Screen("quiz", "Quiz", { Icon(painter = painterResource(id = R.drawable.ic_quiz), contentDescription = "Quiz") })
    object Settings : Screen("settings", "Settings", { Icon(Icons.Default.Settings, contentDescription = "Settings") })
    object Notes : Screen("notes", "Notes", { Icon(painter = painterResource(id = R.drawable.ic_notes), contentDescription = "Notes") })
    object Video : Screen("video", "Library", { Icon(painter = painterResource(id = R.drawable.ic_media), contentDescription = "Library") })
    object AccessibilitySettings : Screen("accessibility_settings", "Accessibility Settings", null)
    object LanguageSettings : Screen("language_settings", "Language Settings", null)
    object Feedback : Screen("feedback", "Feedback", null)
    data class NoteEdit(val noteId: Int? = null) : Screen(
        route = if (noteId != null) "noteEdit/$noteId" else "noteEdit",
        title = if (noteId != null) "Edit Note" else "Add Note",
        icon = null
    )
}

@Composable
fun MainNavigation(
    onLogout: () -> Unit,
    onRestartRequested: () -> Unit = {}
) {
    val themeManager = rememberThemeManager()
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var noteEditId by remember { mutableStateOf<Int?>(null) }
    val isTablet = isTablet()
    val isLandscape = isLandscape()

    val showNoteEdit = selectedScreen is Screen.NoteEdit

    val showContent: @Composable () -> Unit = {
        when {
            showNoteEdit -> {
                NoteEditScreen(
                    noteId = noteEditId,
                    onBack = {
                        selectedScreen = Screen.Notes
                        noteEditId = null
                    }
                )
            }
            else -> when (selectedScreen) {
                Screen.Home -> HomeScreen(
                    onStartQuiz = { selectedScreen = Screen.Quiz },
                    onSeeResults = { selectedScreen = Screen.Settings },
                    onNoteClick = { noteId ->
                        noteEditId = noteId
                        selectedScreen = Screen.NoteEdit(noteId)
                    },
                    onLearnMoreClick = {
                        selectedScreen = Screen.Notes
                    }
                )
                Screen.Quiz -> QuizScreen(
                    onBackPressed = {
                        // If user is on first question, go back to previous screen
                        // Otherwise, let QuizScreen handle internal navigation
                        selectedScreen = Screen.Home
                    }
                )
                Screen.Settings -> SettingsScreen(
                    onLogout = onLogout,
                    onAccessibilitySettings = { selectedScreen = Screen.AccessibilitySettings },
                    onLanguageSettings = { selectedScreen = Screen.LanguageSettings },
                    onFeedback = { selectedScreen = Screen.Feedback }
                )
                Screen.AccessibilitySettings -> AccessibilitySettingsScreen(
                    onBackPressed = { selectedScreen = Screen.Settings }
                )
                Screen.LanguageSettings -> LanguageSettingsScreen(
                    onBackPressed = { selectedScreen = Screen.Settings },
                    onRestartRequested = onRestartRequested
                )
                Screen.Feedback -> FeedbackScreen(
                    onBackPressed = { selectedScreen = Screen.Settings }
                )
                Screen.Notes -> NotesScreen(
                    onEditNote = { id ->
                        noteEditId = id
                        selectedScreen = Screen.NoteEdit(id)
                    }
                )
                Screen.Video -> VideoLibraryScreen()
                else -> {}
            }
        }
    }

    if (isTablet && isLandscape) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Side navigation for tablets in landscape
            NavigationRail(
                modifier = Modifier
                    .width(80.dp)
                    .semantics { 
                        contentDescription = "Side navigation rail"
                    }
            ) {
                Screen.Home.icon?.let {
                    NavigationRailItem(
                        icon = it,
                        label = { Text(stringResource(R.string.nav_home)) },
                        selected = selectedScreen == Screen.Home,
                        onClick = { selectedScreen = Screen.Home },
                        modifier = Modifier.semantics {
                            contentDescription = "Navigate to Home screen"
                        }
                    )
                }
                Screen.Notes.icon?.let {
                    NavigationRailItem(
                        icon = it,
                        label = { Text(stringResource(R.string.nav_notes)) },
                        selected = selectedScreen == Screen.Notes,
                        onClick = { selectedScreen = Screen.Notes },
                        modifier = Modifier.semantics {
                            contentDescription = "Navigate to Notes screen"
                        }
                    )
                }
                Screen.Video.icon?.let {
                    NavigationRailItem(
                        icon = it,
                        label = { Text(stringResource(R.string.nav_library)) },
                        selected = selectedScreen == Screen.Video,
                        onClick = { selectedScreen = Screen.Video },
                        modifier = Modifier.semantics {
                            contentDescription = "Navigate to Library screen"
                        }
                    )
                }
                Screen.Quiz.icon?.let {
                    NavigationRailItem(
                        icon = it,
                        label = { Text(stringResource(R.string.nav_quiz)) },
                        selected = selectedScreen == Screen.Quiz,
                        onClick = { selectedScreen = Screen.Quiz },
                        modifier = Modifier.semantics {
                            contentDescription = "Navigate to Quiz screen"
                        }
                    )
                }
                Screen.Settings.icon?.let {
                    NavigationRailItem(
                        icon = it,
                        label = { Text(stringResource(R.string.nav_settings)) },
                        selected = selectedScreen == Screen.Settings,
                        onClick = { selectedScreen = Screen.Settings },
                        modifier = Modifier.semantics {
                            contentDescription = "Navigate to Settings screen"
                        }
                    )
                }
            }
            
            // Content area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                showContent()
            }
        }
    } else {
        // Standard bottom navigation for phones and tablets in portrait
        Scaffold(
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.semantics { 
                        contentDescription = "Bottom navigation bar"
                    }
                ) {
                    Screen.Home.icon?.let {
                        NavigationBarItem(
                            icon = it,
                            label = { Text(stringResource(R.string.nav_home)) },
                            selected = selectedScreen == Screen.Home,
                            onClick = { selectedScreen = Screen.Home },
                            modifier = Modifier.semantics {
                                contentDescription = "Navigate to Home screen"
                            }
                        )
                    }
                    Screen.Notes.icon?.let {
                        NavigationBarItem(
                            icon = it,
                            label = { Text(stringResource(R.string.nav_notes)) },
                            selected = selectedScreen == Screen.Notes,
                            onClick = { selectedScreen = Screen.Notes },
                            modifier = Modifier.semantics {
                                contentDescription = "Navigate to Notes screen"
                            }
                        )
                    }
                    Screen.Video.icon?.let {
                        NavigationBarItem(
                            icon = it,
                            label = { Text(stringResource(R.string.nav_library)) },
                            selected = selectedScreen == Screen.Video,
                            onClick = { selectedScreen = Screen.Video },
                            modifier = Modifier.semantics {
                                contentDescription = "Navigate to Library screen"
                            }
                        )
                    }
                    Screen.Quiz.icon?.let {
                        NavigationBarItem(
                            icon = it,
                            label = { Text(stringResource(R.string.nav_quiz)) },
                            selected = selectedScreen == Screen.Quiz,
                            onClick = { selectedScreen = Screen.Quiz },
                            modifier = Modifier.semantics {
                                contentDescription = "Navigate to Quiz screen"
                            }
                        )
                    }
                    Screen.Settings.icon?.let {
                        NavigationBarItem(
                            icon = it,
                            label = { Text(stringResource(R.string.nav_settings)) },
                            selected = selectedScreen == Screen.Settings,
                            onClick = { selectedScreen = Screen.Settings },
                            modifier = Modifier.semantics {
                                contentDescription = "Navigate to Settings screen"
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                showContent()
            }
        }
    }
} 