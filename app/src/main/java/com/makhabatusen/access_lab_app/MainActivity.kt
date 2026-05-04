package com.makhabatusen.access_lab_app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.makhabatusen.access_lab_app.ui.components.SplashScreen
import com.makhabatusen.access_lab_app.ui.auth.AuthViewModel
import com.makhabatusen.access_lab_app.ui.auth.LoginScreen
import com.makhabatusen.access_lab_app.ui.auth.RegisterScreen
import com.makhabatusen.access_lab_app.ui.navigation.MainNavigation
import com.makhabatusen.access_lab_app.ui.theme.AccessLabThemeWithManager
import com.makhabatusen.access_lab_app.ui.theme.rememberThemeManager
import com.makhabatusen.access_lab_app.core.language.LocaleAwareActivity
import com.makhabatusen.access_lab_app.ui.settings.AccessibilitySettingsViewModel

class MainActivity : LocaleAwareActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeManager = rememberThemeManager()
            val accessibilityViewModel = viewModel<AccessibilitySettingsViewModel>()
            val authViewModel = viewModel<AuthViewModel>()
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                accessibilityViewModel.loadSettings(context, themeManager)
            }

            AccessLabThemeWithManager(
                themeManager = themeManager,
                highContrast = accessibilityViewModel.highContrast
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showRegister by remember { mutableStateOf(false) }

                    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                    val isAuthChecked by authViewModel.isAuthChecked.collectAsState()

                    when {
                        !isAuthChecked -> SplashScreen()
                        isLoggedIn -> MainNavigation(
                            onLogout = { authViewModel.signOut() },
                            onRestartRequested = { recreate() }
                        )
                        showRegister -> RegisterScreen(
                            onRegisterBack = { showRegister = false },
                            onRegisterSuccess = { showRegister = false }
                        )
                        else -> LoginScreen(
                            onLoginSuccess = {},
                            onShowRegister = { showRegister = true }
                        )
                    }
                }
            }
        }
    }
}