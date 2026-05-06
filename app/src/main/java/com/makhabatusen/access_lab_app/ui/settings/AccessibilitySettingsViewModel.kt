package com.makhabatusen.access_lab_app.ui.settings

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makhabatusen.access_lab_app.ui.theme.FontSizeScale
import com.makhabatusen.access_lab_app.ui.theme.ThemeManager
import com.makhabatusen.access_lab_app.ui.theme.TypographyManager
import com.makhabatusen.access_lab_app.ui.util.AccessibilityUtils
import kotlinx.coroutines.launch

class AccessibilitySettingsViewModel : ViewModel() {
    private var context: Context? = null
    private var themeManager: ThemeManager? = null

    var followSystem by mutableStateOf(true)
        private set

    var darkMode by mutableStateOf(false)
        private set

    var highContrast by mutableStateOf(false)
        private set

    var largeText by mutableStateOf(false)
        private set

    var fontScale by mutableStateOf(FontSizeScale.MEDIUM)
        private set

    var motionActuationEnabled by mutableStateOf(false)
        private set

    fun updateFollowSystem(enabled: Boolean) {
        followSystem = enabled
        val mode = when {
            enabled -> ThemeManager.THEME_SYSTEM
            darkMode -> ThemeManager.THEME_DARK
            else -> ThemeManager.THEME_LIGHT
        }
        themeManager?.setThemeMode(mode)
        saveSetting("follow_system", enabled)
    }

    fun updateDarkMode(enabled: Boolean) {
        darkMode = enabled
        if (!followSystem) {
            themeManager?.setThemeMode(
                if (enabled) ThemeManager.THEME_DARK else ThemeManager.THEME_LIGHT
            )
        }
        saveSetting("dark_mode", enabled)
    }

    fun updateHighContrast(enabled: Boolean) {
        highContrast = enabled
        saveSetting("high_contrast", enabled)
    }

    fun updateFontScale(scale: FontSizeScale) {
        Log.d("AccessibilitySettingsViewModel", "updateFontScale called with: $scale")
        fontScale = scale
        TypographyManager.setFontScale(scale)
        saveFontScale(scale)
    }

    fun updateMotionActuation(enabled: Boolean) {
        motionActuationEnabled = enabled
        saveSetting("motion_actuation_enabled", enabled)
    }

    fun loadSettings(context: Context, themeManager: ThemeManager) {
        this.context = context.applicationContext
        this.themeManager = themeManager

        viewModelScope.launch {
            val sharedPrefs =
                context.getSharedPreferences("accessibility_settings", Context.MODE_PRIVATE)

            val storedMode = themeManager.themeMode.value
            followSystem = storedMode == ThemeManager.THEME_SYSTEM
            darkMode = when (storedMode) {
                ThemeManager.THEME_DARK -> true
                ThemeManager.THEME_SYSTEM -> themeManager.getEffectiveDarkMode()
                else -> false
            }

            highContrast = sharedPrefs.getBoolean("high_contrast", false)
            largeText = sharedPrefs.getBoolean("large_text", false)

            val fontScaleOrdinal = sharedPrefs.getInt("font_scale", -1)
            fontScale = if (fontScaleOrdinal == -1) {
                AccessibilityUtils.getRecommendedFontScale(context)
            } else {
                FontSizeScale.entries[fontScaleOrdinal]
            }

            TypographyManager.setFontScale(fontScale)
            motionActuationEnabled = sharedPrefs.getBoolean("motion_actuation_enabled", false)
        }
    }

    private fun saveSetting(key: String, value: Boolean) {
        context?.let { ctx ->
            viewModelScope.launch {
                val sharedPrefs =
                    ctx.getSharedPreferences("accessibility_settings", Context.MODE_PRIVATE)
                sharedPrefs.edit { putBoolean(key, value) }
            }
        }
    }

    private fun saveFontScale(scale: FontSizeScale, isSystemDefault: Boolean = false) {
        context?.let { ctx ->
            viewModelScope.launch {
                val sharedPrefs =
                    ctx.getSharedPreferences("accessibility_settings", Context.MODE_PRIVATE)
                val valueToSave = if (isSystemDefault) -1 else scale.ordinal
                sharedPrefs.edit { putInt("font_scale", valueToSave) }
            }
        }
    }
}