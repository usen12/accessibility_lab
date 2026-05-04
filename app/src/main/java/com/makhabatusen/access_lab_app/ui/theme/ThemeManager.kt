package com.makhabatusen.access_lab_app.ui.theme

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ThemeManager handles the app's theme state with accessibility compliance.
 * Supports both user preference and system theme detection.
 * Implemented as a singleton to ensure consistent state across the app.
 */
class ThemeManager private constructor(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "theme_preferences"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_FOLLOW_SYSTEM = "follow_system"
        
        // Theme mode constants
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"
        
        @Volatile
        private var INSTANCE: ThemeManager? = null
        
        fun getInstance(context: Context): ThemeManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemeManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val _themeMode = MutableStateFlow(getStoredThemeMode())
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()
    
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()
    
    private val _followSystem = MutableStateFlow(getStoredFollowSystem())
    val followSystem: StateFlow<Boolean> = _followSystem.asStateFlow()
    
    init {
        updateDarkModeState()
    }
    
    /**
     * Set the theme mode (light, dark, or system)
     */
    fun setThemeMode(mode: String) {
        android.util.Log.d("ThemeManager", "Setting theme mode to: $mode")
        _themeMode.value = mode
        saveThemeMode(mode)
        updateDarkModeState()
    }
    
    /**
     * Toggle between light and dark mode
     */
    fun toggleDarkMode() {
        val newMode = when (_themeMode.value) {
            THEME_LIGHT -> THEME_DARK
            THEME_DARK -> THEME_LIGHT
            THEME_SYSTEM -> THEME_DARK // If following system, switch to dark
            else -> THEME_DARK
        }
        setThemeMode(newMode)
    }

    
    /**
     * Get the effective dark mode state
     */
    fun getEffectiveDarkMode(): Boolean {
        return when (_themeMode.value) {
            THEME_LIGHT -> false
            THEME_DARK -> true
            THEME_SYSTEM -> isSystemDarkMode()
            else -> false
        }
    }
    
    /**
     * Check if system is in dark mode
     */
    private fun isSystemDarkMode(): Boolean {
        return context.resources.configuration.uiMode and 
               android.content.res.Configuration.UI_MODE_NIGHT_MASK == 
               android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
    
    /**
     * Update the dark mode state based on current settings
     */
    private fun updateDarkModeState() {
        val newDarkMode = getEffectiveDarkMode()
        android.util.Log.d("ThemeManager", "Updating dark mode state to: $newDarkMode")
        _isDarkMode.value = newDarkMode
    }
    
    /**
     * Get stored theme mode from preferences
     */
    private fun getStoredThemeMode(): String {
        return prefs.getString(KEY_THEME_MODE, THEME_SYSTEM) ?: THEME_SYSTEM
    }
    
    /**
     * Save theme mode to preferences
     */
    private fun saveThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
    }
    
    /**
     * Get stored follow system preference
     */
    private fun getStoredFollowSystem(): Boolean {
        return prefs.getBoolean(KEY_FOLLOW_SYSTEM, true)
    }
    
    /**
     * Save follow system preference
     */
    private fun saveFollowSystem(follow: Boolean) {
        prefs.edit().putBoolean(KEY_FOLLOW_SYSTEM, follow).apply()
    }
}

/**
 * Composable function to get the current theme manager
 */
@Composable
fun rememberThemeManager(): ThemeManager {
    val context = LocalContext.current
    return remember { ThemeManager.getInstance(context) }
}

/**
 * Composable function to observe theme changes
 */
@Composable
fun observeThemeMode(): State<String> {
    val themeManager = rememberThemeManager()
    return themeManager.themeMode.collectAsState()
}

/**
 * Composable function to observe dark mode state
 */
@Composable
fun observeDarkMode(): State<Boolean> {
    val themeManager = rememberThemeManager()
    return themeManager.isDarkMode.collectAsState()
} 