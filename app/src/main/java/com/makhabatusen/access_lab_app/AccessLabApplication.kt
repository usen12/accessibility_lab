package com.makhabatusen.access_lab_app

import android.app.Application
import android.content.Context
import android.util.Log
import android.webkit.WebView
import com.google.firebase.FirebaseApp
import com.makhabatusen.access_lab_app.data.notes.local.NoteDatabase
import com.makhabatusen.access_lab_app.data.notes.repository.NoteRepository
import com.makhabatusen.access_lab_app.data.notes.seed.PredefinedNotesData
import com.makhabatusen.access_lab_app.core.language.LanguageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AccessLabApplication : Application() {

    private lateinit var languageManager: LanguageManager
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        initializeLanguageManager()
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        initializePredefinedNotes()
    }

    override fun attachBaseContext(base: Context) {
        languageManager = LanguageManager(base)
        val context = languageManager.getLocaleAwareContext(base)
        super.attachBaseContext(context)
    }

    private fun initializeLanguageManager() {
        languageManager = LanguageManager(this)
        languageManager.initialize()
    }

    private fun initializePredefinedNotes() {
        applicationScope.launch {
            try {
                val database = NoteDatabase.getDatabase(this@AccessLabApplication)
                val repository = NoteRepository(database.noteDao())
                if (repository.getProtectedNotesCount() == 0) {
                    repository.insertNotes(PredefinedNotesData.predefinedNotes)
                }
            } catch (e: Exception) {
                Log.e("AccessLabApplication", "Failed to initialize predefined notes", e)
            }
        }
    }
}