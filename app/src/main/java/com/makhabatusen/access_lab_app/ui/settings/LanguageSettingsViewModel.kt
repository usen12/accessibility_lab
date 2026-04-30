package com.makhabatusen.access_lab_app.ui.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.core.language.LanguageManager
import com.makhabatusen.access_lab_app.core.language.LanguageOption

/**
 * ViewModel for managing language settings
 */
class LanguageSettingsViewModel : ViewModel() {
    
    private var languageManager: LanguageManager? = null
    private var onRestartCallback: (() -> Unit)? = null
    private var context: Context? = null
    
    // UI State
    var currentLanguage by mutableStateOf(LanguageManager.LANGUAGE_SYSTEM)
        private set
    
    var availableLanguages by mutableStateOf<List<LanguageOption>>(emptyList())
        private set
    
    var showRestartDialog by mutableStateOf(false)
        private set
    
    var languageChanged by mutableStateOf(false)
        private set
    
    /**
     * Initialize the ViewModel with context
     */
    fun initialize(context: Context) {
        this.context = context
        val manager = LanguageManager(context)
        languageManager = manager
        currentLanguage = manager.currentLanguage.value
        availableLanguages = manager.getAvailableLanguages()
    }
    
    /**
     * Set the restart callback
     */
    fun setRestartCallback(callback: () -> Unit) {
        onRestartCallback = callback
    }
    
    /**
     * Set the selected language
     */
    fun setLanguage(languageCode: String) {
        if (currentLanguage != languageCode) {
            languageManager?.setLanguage(languageCode)
            currentLanguage = languageCode
            languageChanged = true
            
            // Show restart dialog for immediate language change
            showRestartDialog = true
        }
    }
    
    /**
     * Get the display name for the current language
     */
    fun getCurrentLanguageDisplayName(): String {
        return languageManager?.getLanguageDisplayName(currentLanguage) ?: context?.getString(R.string.language_system_default) ?: "System Default"
    }
    
    /**
     * Dismiss the restart dialog
     */
    fun dismissRestartDialog() {
        showRestartDialog = false
    }
    
    /**
     * Handle restart confirmation
     */
    fun onRestartConfirmed() {
        // The language is already saved in preferences
        // Call the restart callback to recreate the activity
        onRestartCallback?.invoke()
        showRestartDialog = false
        languageChanged = false
    }
    
    /**
     * Handle restart cancellation
     */
    fun onRestartCancelled() {
        showRestartDialog = false
        // Optionally revert the language change
        // languageManager?.setLanguage(previousLanguage)
    }
    
    /**
     * Check if the current language is the system default
     */
    fun isSystemDefault(): Boolean {
        return currentLanguage == LanguageManager.LANGUAGE_SYSTEM
    }
    
    /**
     * Get the language manager instance
     */
    fun getLanguageManager(): LanguageManager? = languageManager
} 