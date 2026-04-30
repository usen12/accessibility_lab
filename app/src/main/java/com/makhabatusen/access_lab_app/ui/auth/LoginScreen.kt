package com.makhabatusen.access_lab_app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.password
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.ui.util.Constants
import com.makhabatusen.access_lab_app.ui.util.ResponsiveSpacing
import com.makhabatusen.access_lab_app.ui.util.SpacingContext
import com.makhabatusen.access_lab_app.ui.components.UnifiedButton
import com.makhabatusen.access_lab_app.ui.components.UnifiedButtonType
import com.makhabatusen.access_lab_app.ui.components.UnifiedOutlinedButton
import com.makhabatusen.access_lab_app.ui.components.UnifiedTextButton
import com.makhabatusen.access_lab_app.ui.util.isLandscape
import com.makhabatusen.access_lab_app.ui.util.isTablet

/**
 * LoginScreen - Authentication screen with accessibility compliance
 *
 * Accessibility Features (EN 301 549 / WCAG 2.1 AA):
 * - Proper content descriptions for screen readers
 * - Semantic error announcements with live regions
 * - Keyboard navigation support
 * - High contrast color scheme compliance
 * - Touch target size compliance (48dp minimum)
 * - Clear visual and programmatic labels
 * - Error state announcements
 * - Focus management
 */


//// Correct Version
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onShowRegister: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val authViewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.makhabatusen.access_lab_app.ui.auth.AuthViewModel>()

    val isLandscape = isLandscape()
    val isTablet = isTablet()

    // Use responsive spacing system
    val screenPadding = ResponsiveSpacing.getScreenPadding()
    val maxContentWidth = ResponsiveSpacing.getMaxContentWidth()
    val buttonHeight = ResponsiveSpacing.getButtonHeight()
    val elementSpacing = ResponsiveSpacing.getElementSpacing(SpacingContext.FORM)

    // Accessibility: Track email validation state for screen reader announcements
    val isEmailValid = email.isEmpty() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val emailErrorState = if (email.isNotEmpty() && !isEmailValid) {
        if (!email.contains("@")) {
            "Please enter a valid email address (include @)"
        } else {
            stringResource(R.string.login_email_invalid_error)
        }
    } else null

    // Ensure error state is cleared when email is empty (for test reliability)
    val finalEmailErrorState = if (email.isEmpty()) null else emailErrorState

    // Clear general error state only when user is actively typing (not when errors are being set)
    // This allows errors to be displayed when login is attempted with empty fields
    val finalGeneralError = error

    // Pre-load string resources to avoid calling @Composable functions in non-@Composable functions
    val missingFieldsError = stringResource(R.string.login_missing_fields_error)
    val emailInvalidError = stringResource(R.string.login_email_invalid_error)
    val noInternetError = stringResource(R.string.login_no_internet_error)

    // Pre-load all string resources used in semantics blocks
    val loginEmailLabel = stringResource(R.string.login_email_label)
    val loginPasswordLabel = stringResource(R.string.login_password_label)
    val cdLoginPasswordFieldState = stringResource(R.string.cd_login_password_field_state)
    val cdLoginErrorMessage = stringResource(R.string.cd_login_error_message)
    
    // Pre-load accessibility content descriptions
    val cdLoginWelcomeTitle = stringResource(R.string.cd_login_welcome_title)
    val cdLoginWelcomeSubtitle = stringResource(R.string.cd_login_welcome_subtitle)
    val cdLoginEmailEmptyState = stringResource(R.string.cd_login_email_empty_state)
    val cdLoginErrorSuggestion = stringResource(R.string.cd_login_error_suggestion)

    fun login() {
        error = null

        // Validate inputs
        if (email.isBlank() || password.isBlank()) {
            error = missingFieldsError
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error = emailInvalidError
            return
        }

        // Check network connectivity first
        val connectivityManager =
            context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        val isConnected =
            networkCapabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        if (!isConnected) {
            error = noInternetError
            return
        }

        loading = true

        authViewModel.signInWithEmailAndPassword(
            email = email,
            password = password,
            onSuccess = {
                loading = false
                onLoginSuccess()
            },
            onError = { errorMessage ->
                loading = false
                error = errorMessage
            }
        )
    }

    fun loginAnonymously() {
        error = null

        val connectivityManager =
            context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        val isConnected =
            networkCapabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        if (!isConnected) {
            error = noInternetError
            return
        }

        loading = true

        authViewModel.signInAnonymously(
            onSuccess = {
                loading = false
                onLoginSuccess()
            },
            onError = { errorMessage ->
                loading = false
                error = errorMessage
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        if (isLandscape && !isTablet) {
            // Landscape layout for phones - optimized space distribution
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = screenPadding.horizontal,
                        vertical = screenPadding.vertical
                    ),
                horizontalArrangement = Arrangement.spacedBy(ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side - Compact welcome text (1/3 of space)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.login_welcome_title),
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                            .semantics {
                                heading()
                                contentDescription = cdLoginWelcomeTitle
                            }
                    )
                    Text(
                        text = stringResource(R.string.login_welcome_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = Constants.Spacing.Screen.horizontal)
                            .semantics {
                                contentDescription = cdLoginWelcomeSubtitle
                            }
                    )
                }

                // Right side - Form with more space (2/3 of space)
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .widthIn(max = 400.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Accessibility: Error message with live region for immediate announcement
                    if (finalGeneralError != null) {
                        Text(
                            text = finalGeneralError.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                                .semantics {
                                    liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Assertive
                                    contentDescription = "$cdLoginErrorMessage $finalGeneralError"
                                    stateDescription = finalGeneralError ?: ""
                                }
                        )
                    }

                    // Email TextField with comprehensive accessibility support
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            error = null
                        },
                        label = { Text(stringResource(R.string.login_email_label)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.FORM))
                            .testTag("emailField")
                            .semantics {
                                contentDescription = loginEmailLabel
                                // Announce error state to screen readers
                                if (finalEmailErrorState != null) {
                                    error(finalEmailErrorState)
                                    stateDescription = finalEmailErrorState
                                } else if (email.isEmpty()) {
                                    stateDescription = cdLoginEmailEmptyState
                                }
                            },
                        placeholder = { Text(stringResource(R.string.login_email_placeholder)) },
                        isError = finalEmailErrorState != null,
                        enabled = true
                    )

                                                        // Separate error message for email validation (for test accessibility)
                    if (finalEmailErrorState != null) {
                        Text(
                            text = finalEmailErrorState,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.FORM))
                                .semantics {
                                    contentDescription = cdLoginErrorSuggestion
                                }
                        )
                    }

                    // Password TextField with comprehensive accessibility support
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            error = null
                        },
                        label = { Text(stringResource(R.string.login_password_label)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                            .testTag("passwordField")
                            .semantics {
                                contentDescription = loginPasswordLabel
                                stateDescription = cdLoginPasswordFieldState
                                // Add password semantics for accessibility
                                password()
                            },
                        placeholder = { Text(stringResource(R.string.login_password_placeholder)) },
                        enabled = true
                    )

                    // Login buttons in a single row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(ResponsiveSpacing.getElementSpacing(SpacingContext.BUTTON))
                    ) {
                        UnifiedButton(
                            onClick = { login() },
                            text = stringResource(R.string.login_button_text),
                            enabled = !loading,
                            loading = loading,
                            contentDescription = stringResource(R.string.login_button_text),
                            buttonType = UnifiedButtonType.PRIMARY,
                            modifier = Modifier.weight(1f)
                        )

                        UnifiedOutlinedButton(
                            onClick = { loginAnonymously() },
                            text = stringResource(R.string.login_guest_button_text),
                            enabled = !loading,
                            loading = loading,
                            contentDescription = stringResource(R.string.login_guest_button_text),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT)))

                    UnifiedTextButton(
                        onClick = onShowRegister,
                        text = stringResource(R.string.login_register_prompt),
                        enabled = !loading,
                        contentDescription = stringResource(R.string.login_register_prompt)
                    )
                }
            }
        } else if (isTablet) {
            // Tablet layout - centered column with larger content
            Column(
                modifier = Modifier
                    .widthIn(max = maxContentWidth)
                    .padding(
                        horizontal = screenPadding.horizontal,
                        vertical = screenPadding.vertical
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.login_welcome_title),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION))
                        .semantics {
                            heading()
                            contentDescription = cdLoginWelcomeTitle
                        }
                )

                Text(
                    text = stringResource(R.string.login_welcome_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION))
                        .semantics {
                            contentDescription = cdLoginWelcomeSubtitle
                        }
                )

                // Accessibility: Error message with live region for immediate announcement
                if (finalGeneralError != null) {
                    Text(
                        text = finalGeneralError.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                            .semantics {
                                liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Assertive
                                contentDescription = "$cdLoginErrorMessage $finalGeneralError"
                                stateDescription = finalGeneralError ?: ""
                            }
                    )
                }

                // Email TextField with comprehensive accessibility support
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        error = null
                    },
                    label = { Text(stringResource(R.string.login_email_label)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                        .testTag("emailField")
                        .semantics {
                            contentDescription = loginEmailLabel
                            // Announce error state to screen readers
                            if (finalEmailErrorState != null) {
                                error(finalEmailErrorState)
                                stateDescription = finalEmailErrorState
                            } else if (email.isEmpty()) {
                                stateDescription = cdLoginEmailEmptyState
                            }
                        },
                    placeholder = { Text(stringResource(R.string.login_email_placeholder)) },
                    isError = finalEmailErrorState != null,
                    supportingText = {
                        if (finalEmailErrorState != null) {
                            Text(finalEmailErrorState, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    enabled = true
                )

                // Separate error message for email validation (for test accessibility)
                if (finalEmailErrorState != null) {
                    Text(
                        text = finalEmailErrorState,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                            .semantics {
                                contentDescription = cdLoginErrorSuggestion
                            }
                    )
                }

                // Password TextField with comprehensive accessibility support
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        error = null
                    },
                    label = { Text(stringResource(R.string.login_password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION))
                        .testTag("passwordField")
                        .semantics {
                            contentDescription = loginPasswordLabel
                            stateDescription = cdLoginPasswordFieldState
                            // Add password semantics for accessibility
                            password()
                        },
                    placeholder = { Text(stringResource(R.string.login_password_placeholder)) },
                    enabled = true
                )

                UnifiedButton(
                    onClick = { login() },
                    text = stringResource(R.string.login_button_text),
                    enabled = !loading,
                    loading = loading,
                    contentDescription = stringResource(R.string.login_button_text),
                    buttonType = UnifiedButtonType.PRIMARY,
                    maxWidth = true
                )

                Spacer(modifier = Modifier.height(ResponsiveSpacing.getElementSpacing(SpacingContext.BUTTON)))

                UnifiedButton(
                    onClick = { loginAnonymously() },
                    text = stringResource(R.string.login_guest_button_text),
                    enabled = !loading,
                    loading = loading,
                    contentDescription = stringResource(R.string.login_guest_button_text),
                    buttonType = UnifiedButtonType.SECONDARY,
                    maxWidth = true
                )

                Spacer(modifier = Modifier.height(ResponsiveSpacing.getElementSpacing(SpacingContext.BUTTON)))

                UnifiedTextButton(
                    onClick = onShowRegister,
                    text = stringResource(R.string.login_register_prompt),
                    enabled = !loading,
                    contentDescription = stringResource(R.string.login_register_prompt),
                    maxWidth = true
                )
            }
        } else {
            // Portrait layout for phones - optimized column layout
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = screenPadding.horizontal,
                        vertical = screenPadding.vertical
                    )
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(0.2f))

                Text(
                    text = stringResource(R.string.login_welcome_title),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                        .semantics {
                            heading()
                            contentDescription = cdLoginWelcomeTitle
                        }
                )

                Text(
                    text = stringResource(R.string.login_welcome_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION))
                        .semantics {
                            contentDescription = cdLoginWelcomeSubtitle
                        }
                )

                // Accessibility: Error message with live region for immediate announcement
                if (finalGeneralError != null) {
                    Text(
                        text = finalGeneralError.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                            .semantics {
                                liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Assertive
                                contentDescription = "$cdLoginErrorMessage $finalGeneralError"
                                stateDescription = finalGeneralError ?: ""
                            }
                    )
                }

                // Email TextField with comprehensive accessibility support
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        error = null
                    },
                    label = { Text(stringResource(R.string.login_email_label)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                        .testTag("emailField")
                        .semantics {
                            contentDescription = loginEmailLabel
                            // Announce error state to screen readers
                            if (finalEmailErrorState != null) {
                                error(finalEmailErrorState)
                                stateDescription = finalEmailErrorState
                            } else if (email.isEmpty()) {
                                stateDescription = cdLoginEmailEmptyState
                            }
                        },
                    placeholder = { Text(stringResource(R.string.login_email_placeholder)) },
                    isError = finalEmailErrorState != null,
                    enabled = true
                )

                // Separate error message for email validation (for test accessibility)
                if (finalEmailErrorState != null) {
                    Text(
                        text = finalEmailErrorState,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                            .semantics {
                                contentDescription = cdLoginErrorSuggestion
                            }
                    )
                }

                // Password TextField with comprehensive accessibility support
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        error = null
                    },
                    label = { Text(stringResource(R.string.login_password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION))
                        .testTag("passwordField")
                        .semantics {
                            contentDescription = loginPasswordLabel
                            stateDescription = cdLoginPasswordFieldState
                            // Add password semantics for accessibility
                            password()
                        },
                    placeholder = { Text(stringResource(R.string.login_password_placeholder)) },
                    enabled = true
                )

                UnifiedButton(
                    onClick = { login() },
                    text = stringResource(R.string.login_button_text),
                    enabled = !loading,
                    loading = loading,
                    contentDescription = stringResource(R.string.login_button_text),
                    buttonType = UnifiedButtonType.PRIMARY,
                    maxWidth = true
                )

                Spacer(modifier = Modifier.height(ResponsiveSpacing.getElementSpacing(SpacingContext.BUTTON)))

                UnifiedButton(
                    onClick = { loginAnonymously() },
                    text = stringResource(R.string.login_guest_button_text),
                    enabled = !loading,
                    loading = loading,
                    contentDescription = stringResource(R.string.login_guest_button_text),
                    buttonType = UnifiedButtonType.SECONDARY,
                    maxWidth = true
                )

                Spacer(modifier = Modifier.height(ResponsiveSpacing.getElementSpacing(SpacingContext.BUTTON)))

                UnifiedTextButton(
                    onClick = onShowRegister,
                    text = stringResource(R.string.login_register_prompt),
                    enabled = !loading,
                    contentDescription = stringResource(R.string.login_register_prompt),
                    maxWidth = true
                )

                Spacer(modifier = Modifier.weight(0.2f))
            }
        }
    }
}