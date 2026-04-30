package com.makhabatusen.access_lab_app.ui.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.makhabatusen.access_lab_app.ui.theme.AccessLabTheme

class NotesScreenTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccessLabTheme {
                NotesScreenTestWrapper()
            }
        }
    }
} 