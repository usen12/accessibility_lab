package com.makhabatusen.access_lab_app.core.language

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.compose.runtime.*
import com.makhabatusen.access_lab_app.R
import java.util.*

/**
 * Language Manager for handling app language preferences
 * Supports system default, English, and German languages
 * Follows Android best practices for locale management
 */
class LanguageManager(private val context: Context) {
    
    companion object {
        const val LANGUAGE_SYSTEM = "system"
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_GERMAN = "de"
        
        private const val PREFS_NAME = "language_preferences"
        private const val KEY_LANGUAGE = "selected_language"
        
        // Supported languages list
        private val SUPPORTED_LANGUAGES = setOf(LANGUAGE_SYSTEM, LANGUAGE_ENGLISH, LANGUAGE_GERMAN)
    }
    
    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // Current language state
    private val _currentLanguage = mutableStateOf(getStoredLanguage())
    val currentLanguage: State<String> = _currentLanguage
    
    /**
     * Get the currently stored language preference
     */
    private fun getStoredLanguage(): String {
        val storedLanguage = sharedPreferences.getString(KEY_LANGUAGE, LANGUAGE_SYSTEM) ?: LANGUAGE_SYSTEM
        // Validate that the stored language is still supported
        return if (SUPPORTED_LANGUAGES.contains(storedLanguage)) {
            storedLanguage
        } else {
            // If stored language is not supported, reset to system default
            android.util.Log.d("LanguageManager", "Stored language '$storedLanguage' not supported, resetting to system default")
            LANGUAGE_SYSTEM
        }
    }
    
    /**
     * Set the app language and update the locale
     */
    fun setLanguage(languageCode: String) {
        // Validate the language code
        if (!SUPPORTED_LANGUAGES.contains(languageCode)) {
            android.util.Log.w("LanguageManager", "Unsupported language code: $languageCode, falling back to system default")
            return
        }
        
        val previousLanguage = _currentLanguage.value
        _currentLanguage.value = languageCode
        
        // Save to preferences
        sharedPreferences.edit().putString(KEY_LANGUAGE, languageCode).apply()
        
        android.util.Log.d("LanguageManager", "Language changed from '$previousLanguage' to '$languageCode'")
        
        // Apply locale if different from previous
        if (previousLanguage != languageCode) {
            applyLocale(languageCode)
        }
    }
    
    /**
     * Apply the locale to the app
     */
    private fun applyLocale(languageCode: String) {
        val locale = when (languageCode) {
            LANGUAGE_ENGLISH -> Locale.ENGLISH
            LANGUAGE_GERMAN -> Locale.GERMAN
            else -> getBestAvailableLocale()
        }
        
        updateLocale(locale)
    }
    
    /**
     * Get the system locale
     */
    private fun getSystemLocale(): Locale {
        return try {
            val systemLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val locales = context.resources.configuration.locales
                if (locales.isEmpty) {
                    Locale.getDefault()
                } else {
                    locales[0]
                }
            } else {
                @Suppress("DEPRECATION")
                context.resources.configuration.locale ?: Locale.getDefault()
            }
            
            android.util.Log.d("LanguageManager", "Detected system locale: $systemLocale (language: ${systemLocale.language}, country: ${systemLocale.country})")
            systemLocale
        } catch (e: Exception) {
            android.util.Log.w("LanguageManager", "Failed to get system locale, using default", e)
            Locale.getDefault()
        }
    }
    
    /**
     * Check if the system locale is supported by the app
     */
    private fun isSystemLocaleSupported(): Boolean {
        val systemLocale = getSystemLocale()
        val languageCode = systemLocale.language.lowercase()
        val isSupported = languageCode == LANGUAGE_ENGLISH || languageCode == LANGUAGE_GERMAN
        android.util.Log.d("LanguageManager", "System locale '$languageCode' supported: $isSupported")
        return isSupported
    }
    
    /**
     * Get the best available locale for the system language
     */
    private fun getBestAvailableLocale(): Locale {
        val systemLocale = getSystemLocale()
        val languageCode = systemLocale.language.lowercase()
        
        val bestLocale = when (languageCode) {
            LANGUAGE_ENGLISH -> Locale.ENGLISH
            LANGUAGE_GERMAN -> Locale.GERMAN
            else -> {
                // If system language is not supported, check if it's a variant of English or German
                when {
                    languageCode.startsWith("en") -> {
                        android.util.Log.d("LanguageManager", "System language '$languageCode' maps to English")
                        Locale.ENGLISH
                    }
                    languageCode.startsWith("de") -> {
                        android.util.Log.d("LanguageManager", "System language '$languageCode' maps to German")
                        Locale.GERMAN
                    }
                    else -> {
                        android.util.Log.d("LanguageManager", "System language '$languageCode' not supported, falling back to English")
                        Locale.ENGLISH // Default fallback
                    }
                }
            }
        }
        
        android.util.Log.d("LanguageManager", "Best available locale for system language '$languageCode': $bestLocale")
        return bestLocale
    }
    
    /**
     * Update the app's locale using modern Android approach
     */
    fun getLocaleAwareContext(context: Context): Context {
        val locale = getEffectiveLocale()
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        
        return context.createConfigurationContext(config)
    }

    private fun updateLocale(locale: Locale) {
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        
        // Use modern approach to update configuration
        try {
            // For older versions, use the deprecated method but with proper error handling
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            
            // Update application context resources as well
            context.applicationContext?.let { appContext ->
                try {
                    @Suppress("DEPRECATION")
                    appContext.resources.updateConfiguration(config, appContext.resources.displayMetrics)
                } catch (e: Exception) {
                    // Handle any exceptions during configuration update
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("LanguageManager", "Failed to update locale configuration", e)
        }
    }
    
    /**
     * Get the effective locale (either selected or system default)
     */
    fun getEffectiveLocale(): Locale {
        return when (_currentLanguage.value) {
            LANGUAGE_ENGLISH -> Locale.ENGLISH
            LANGUAGE_GERMAN -> Locale.GERMAN
            else -> getBestAvailableLocale()
        }
    }
    
    /**
     * Get the display name for a language code
     */
    fun getLanguageDisplayName(languageCode: String): String {
        return when (languageCode) {
            LANGUAGE_SYSTEM -> context.getString(R.string.language_system_default)
            LANGUAGE_ENGLISH -> context.getString(R.string.language_english)
            LANGUAGE_GERMAN -> context.getString(R.string.language_german)
            else -> languageCode
        }
    }
    
    /**
     * Get all available languages
     */
    fun getAvailableLanguages(): List<LanguageOption> {
        return listOf(
            LanguageOption(LANGUAGE_SYSTEM, getLanguageDisplayName(LANGUAGE_SYSTEM)),
            LanguageOption(LANGUAGE_ENGLISH, getLanguageDisplayName(LANGUAGE_ENGLISH)),
            LanguageOption(LANGUAGE_GERMAN, getLanguageDisplayName(LANGUAGE_GERMAN))
        )
    }
    
    /**
     * Reset language to system default
     */
    fun resetToSystemDefault() {
        android.util.Log.d("LanguageManager", "Resetting language to system default")
        setLanguage(LANGUAGE_SYSTEM)
    }
    
    /**
     * Check if the current language is set to system default
     */
    fun isUsingSystemDefault(): Boolean {
        return _currentLanguage.value == LANGUAGE_SYSTEM
    }
    
    /**
     * Get the current system language code
     */
    fun getCurrentSystemLanguageCode(): String {
        return getSystemLocale().language.lowercase()
    }
    
    /**
     * Check if a restart is required after language change
     */
    fun isRestartRequired(): Boolean {
        // In a real implementation, you might want to track if the language
        // was changed during the current session
        return false
    }
    
    /**
     * Initialize the language manager and apply current language
     */
    fun initialize() {
        val currentLang = _currentLanguage.value
        val systemLocale = getSystemLocale()
        val effectiveLocale = getEffectiveLocale()
        
        android.util.Log.d("LanguageManager", "=== Language Manager Initialization ===")
        android.util.Log.d("LanguageManager", "Current language preference: $currentLang")
        android.util.Log.d("LanguageManager", "System locale: $systemLocale")
        android.util.Log.d("LanguageManager", "Effective locale: $effectiveLocale")
        android.util.Log.d("LanguageManager", "System locale supported: ${isSystemLocaleSupported()}")
        
        // Apply the locale
        applyLocale(currentLang)
        
        android.util.Log.d("LanguageManager", "Language manager initialization complete")
        android.util.Log.d("LanguageManager", "=====================================")
    }
}

/**
 * Data class representing a language option
 */
data class LanguageOption(
    val code: String,
    val displayName: String
)

/**
 * Composable function to remember the LanguageManager
 */
@Composable
fun rememberLanguageManager(context: Context): LanguageManager {
    return remember {
        LanguageManager(context).apply {
            initialize()
        }
    }
} 