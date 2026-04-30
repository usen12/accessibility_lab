package com.makhabatusen.access_lab_app.core.language

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import java.util.*

/**
 * Base activity that handles locale changes properly
 * This ensures that language changes persist across app restarts
 */
abstract class LocaleAwareActivity : ComponentActivity() {
    
    private lateinit var languageManager: LanguageManager
    
    override fun attachBaseContext(newBase: Context) {
        // Initialize language manager
        languageManager = LanguageManager(newBase)
        
        // Apply the saved language preference using the consolidated logic
        val context = languageManager.getLocaleAwareContext(newBase)
        
        super.attachBaseContext(context)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize language manager with current context
        languageManager = LanguageManager(this)
        languageManager.initialize()
    }
    
    /**
     * Get the language manager instance
     */
    fun getLanguageManager(): LanguageManager = languageManager
} 