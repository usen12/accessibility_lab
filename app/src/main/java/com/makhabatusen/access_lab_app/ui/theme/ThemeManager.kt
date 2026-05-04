package com.makhabatusen.access_lab_app.ui.theme

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeManager private constructor(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "theme_preferences"
        private const val KEY_THEME_MODE = "theme_mode"

        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"

        @Volatile
        private var instance: ThemeManager? = null

        fun getInstance(context: Context): ThemeManager {
            return instance ?: synchronized(this) {
                instance ?: ThemeManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(getStoredThemeMode())
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    init {
        updateDarkModeState()
    }

    fun setThemeMode(mode: String) {
        android.util.Log.d("ThemeManager", "Setting theme mode to: $mode")
        _themeMode.value = mode
        saveThemeMode(mode)
        updateDarkModeState()
    }

    fun getEffectiveDarkMode(): Boolean {
        return when (_themeMode.value) {
            THEME_LIGHT -> false
            THEME_DARK -> true
            THEME_SYSTEM -> isSystemDarkMode()
            else -> false
        }
    }

    private fun isSystemDarkMode(): Boolean {
        return context.resources.configuration.uiMode and
               android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
               android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    private fun updateDarkModeState() {
        val newDarkMode = getEffectiveDarkMode()
        android.util.Log.d("ThemeManager", "Updating dark mode state to: $newDarkMode")
        _isDarkMode.value = newDarkMode
    }

    private fun getStoredThemeMode(): String {
        return prefs.getString(KEY_THEME_MODE, THEME_SYSTEM) ?: THEME_SYSTEM
    }

    private fun saveThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
    }
}

@Composable
fun rememberThemeManager(): ThemeManager {
    val context = LocalContext.current
    return remember { ThemeManager.getInstance(context) }
}