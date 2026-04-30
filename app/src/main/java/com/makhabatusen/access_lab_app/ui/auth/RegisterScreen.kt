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
 * RegisterScreen - Registration screen with accessibility compliance
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

@Composable
fun RegisterScreen(
    onRegisterBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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

    // Accessibility: Track validation states for screen reader announcements
    val isEmailValid = email.isEmpty() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val emailErrorState = if (email.isNotEmpty() && !isEmailValid) {
        if (!email.contains("@")) {
            "Please enter a valid email address (include @)"
        } else {
            stringResource(R.string.register_email_invalid_error)
        }
    } else null
    
    val isPasswordValid = password.length >= 6
    val passwordErrorState = if (password.isNotEmpty() && !isPasswordValid) {
        stringResource(R.string.register_password_too_short_error)
    } else null
    
    val isConfirmPasswordValid = confirmPassword.isEmpty() || password == confirmPassword
    val confirmPasswordErrorState = if (confirmPassword.isNotEmpty() && !isConfirmPasswordValid) {
        stringResource(R.string.register_passwords_mismatch_error)
    } else null
    
    // Ensure error states are cleared when fields are empty (for test reliability)
    val finalEmailErrorState = if (email.isEmpty()) null else emailErrorState
    val finalPasswordErrorState = if (password.isEmpty()) null else passwordErrorState
    val finalConfirmPasswordErrorState = if (confirmPassword.isEmpty()) null else confirmPasswordErrorState
    
    // Clear general error state only when user is actively typing (not when errors are being set)
    val finalGeneralError = error

    // Pre-load string resources to avoid calling @Composable functions in non-@Composable functions
    val missingFieldsError = stringResource(R.string.register_missing_fields_error)
    val emailInvalidError = stringResource(R.string.register_email_invalid_error)
    val passwordTooShortError = stringResource(R.string.register_password_too_short_error)
    val passwordsMismatchError = stringResource(R.string.register_passwords_mismatch_error)
    val noInternetError = stringResource(R.string.register_no_internet_error)

    // Pre-load all string resources used in semantics blocks
    val cdRegisterEmailField = stringResource(R.string.cd_register_email_field)
    val cdRegisterPasswordField = stringResource(R.string.cd_register_password_field)
    val cdRegisterPasswordFieldState = stringResource(R.string.cd_register_password_field_state)
    val cdRegisterConfirmPasswordField = stringResource(R.string.cd_register_confirm_password_field)
    val cdRegisterConfirmPasswordFieldState = stringResource(R.string.cd_register_confirm_password_field_state)
    val cdRegisterButton = stringResource(R.string.cd_register_button)
    val cdRegisterBackButton = stringResource(R.string.cd_register_back_button)
    val cdRegisterErrorMessage = stringResource(R.string.cd_register_error_message)
    val cdRegisterWelcomeTitle = stringResource(R.string.cd_register_welcome_title)
    val cdRegisterWelcomeSubtitle = stringResource(R.string.cd_register_welcome_subtitle)
    val cdLoginErrorSuggestion = stringResource(R.string.cd_login_error_suggestion)
    val cdRegisterEmailEmptyState = stringResource(R.string.cd_register_email_empty_state)

    fun register() {
        error = null

        // Validate inputs
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            error = missingFieldsError
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error = emailInvalidError
            return
        }

        if (password.length < 6) {
            error = passwordTooShortError
            return
        }

        if (password != confirmPassword) {
            error = passwordsMismatchError
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

        authViewModel.createUserWithEmailAndPassword(
            email = email,
            password = password,
            onSuccess = {
                loading = false
                onRegisterSuccess()
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
                        text = stringResource(R.string.register_welcome_title),
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                            .semantics {
                                heading()
                                contentDescription = cdRegisterWelcomeTitle
                            }
                    )
                    Text(
                        text = stringResource(R.string.register_welcome_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = Constants.Spacing.Screen.horizontal)
                            .semantics {
                                contentDescription = cdRegisterWelcomeSubtitle
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
                                    contentDescription = "$cdRegisterErrorMessage $finalGeneralError"
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
                        label = { Text(stringResource(R.string.register_email_label)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.FORM))
                            .testTag("registerEmailField")
                            .semantics {
                                contentDescription = cdRegisterEmailField
                                // Announce error state to screen readers
                                if (finalEmailErrorState != null) {
                                    error(finalEmailErrorState)
                                    stateDescription = finalEmailErrorState
                                } else if (email.isEmpty()) {
                                    stateDescription = "Email field is empty"
                                }
                            },
                        placeholder = { Text(stringResource(R.string.register_email_placeholder)) },
                        isError = finalEmailErrorState != null,
                        enabled = !loading
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
                        label = { Text(stringResource(R.string.register_password_label)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.FORM))
                            .testTag("registerPasswordField")
                            .semantics {
                                contentDescription = cdRegisterPasswordField
                                stateDescription = cdRegisterPasswordFieldState
                                // Add password semantics for accessibility
                                password()
                            },
                        placeholder = { Text(stringResource(R.string.register_password_placeholder)) },
                        isError = finalPasswordErrorState != null,
                        enabled = !loading
                    )

                    // Separate error message for password validation (for test accessibility)
                    if (finalPasswordErrorState != null) {
                        Text(
                            text = finalPasswordErrorState,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.FORM))
                                .semantics {
                                    contentDescription = "Error suggestion message: $finalPasswordErrorState"
                                }
                        )
                    }

                    // Confirm Password TextField with comprehensive accessibility support
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            error = null
                        },
                        label = { Text(stringResource(R.string.register_confirm_password_label)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION))
                            .testTag("registerConfirmPasswordField")
                            .semantics {
                                contentDescription = cdRegisterConfirmPasswordField
                                stateDescription = cdRegisterConfirmPasswordFieldState
                                // Add password semantics for accessibility
                                password()
                            },
                        placeholder = { Text(stringResource(R.string.register_confirm_password_placeholder)) },
                        isError = finalConfirmPasswordErrorState != null,
                        enabled = !loading
                    )

                    // Separate error message for confirm password validation (for test accessibility)
                    if (finalConfirmPasswordErrorState != null) {
                        Text(
                            text = finalConfirmPasswordErrorState,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION))
                                .semantics {
                                    contentDescription = "Error suggestion message: $finalConfirmPasswordErrorState"
                                }
                        )
                    }

                    // Register and Back buttons in a single row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(ResponsiveSpacing.getElementSpacing(SpacingContext.BUTTON))
                    ) {
                        UnifiedButton(
                            onClick = { register() },
                            text = stringResource(R.string.register_button_text),
                            enabled = !loading,
                            loading = loading,
                            contentDescription = cdRegisterButton,
                            buttonType = UnifiedButtonType.PRIMARY,
                            modifier = Modifier.weight(1f)
                        )

                        UnifiedOutlinedButton(
                            onClick = onRegisterBack,
                            text = stringResource(R.string.register_back_button_text),
                            enabled = !loading,
                            contentDescription = cdRegisterBackButton,
                            modifier = Modifier.weight(1f)
                        )
                    }
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
                    text = stringResource(R.string.register_welcome_title),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION))
                        .semantics { 
                            heading()
                            contentDescription = "Create Account"
                        }
                )

                Text(
                    text = stringResource(R.string.register_welcome_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION))
                        .semantics { 
                            contentDescription = "Join us to start your accessibility journey"
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
                                contentDescription = "$cdRegisterErrorMessage $finalGeneralError"
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
                    label = { Text(stringResource(R.string.register_email_label)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                        .testTag("registerEmailField")
                        .semantics {
                            contentDescription = cdRegisterEmailField
                            // Announce error state to screen readers
                            if (finalEmailErrorState != null) {
                                error(finalEmailErrorState)
                                stateDescription = finalEmailErrorState
                            } else if (email.isEmpty()) {
                                stateDescription = cdRegisterEmailEmptyState
                            }
                        },
                    placeholder = { Text(stringResource(R.string.register_email_placeholder)) },
                    isError = finalEmailErrorState != null,
                    supportingText = {
                        if (finalEmailErrorState != null) {
                            Text(finalEmailErrorState, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    enabled = !loading
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
                    label = { Text(stringResource(R.string.register_password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                        .testTag("registerPasswordField")
                        .semantics {
                            contentDescription = cdRegisterPasswordField
                            stateDescription = cdRegisterPasswordFieldState
                            // Add password semantics for accessibility
                            password()
                        },
                    placeholder = { Text(stringResource(R.string.register_password_placeholder)) },
                    isError = finalPasswordErrorState != null,
                    supportingText = {
                        if (finalPasswordErrorState != null) {
                            Text(finalPasswordErrorState, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    enabled = !loading
                )

                // Separate error message for password validation (for test accessibility)
                if (finalPasswordErrorState != null) {
                    Text(
                        text = finalPasswordErrorState,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                            .semantics {
                                contentDescription = "Error suggestion message: $finalPasswordErrorState"
                            }
                    )
                }

                // Confirm Password TextField with comprehensive accessibility support
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        error = null
                    },
                    label = { Text(stringResource(R.string.register_confirm_password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION))
                        .testTag("registerConfirmPasswordField")
                        .semantics {
                            contentDescription = cdRegisterConfirmPasswordField
                            stateDescription = cdRegisterConfirmPasswordFieldState
                            // Add password semantics for accessibility
                            password()
                        },
                    placeholder = { Text(stringResource(R.string.register_confirm_password_placeholder)) },
                    isError = finalConfirmPasswordErrorState != null,
                    supportingText = {
                        if (finalConfirmPasswordErrorState != null) {
                            Text(finalConfirmPasswordErrorState, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    enabled = !loading
                )

                // Separate error message for confirm password validation (for test accessibility)
                if (finalConfirmPasswordErrorState != null) {
                    Text(
                        text = finalConfirmPasswordErrorState,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION))
                            .semantics {
                                contentDescription = "Error suggestion message: $finalConfirmPasswordErrorState"
                            }
                    )
                }

                UnifiedButton(
                    onClick = { register() },
                    text = stringResource(R.string.register_button_text),
                    enabled = !loading,
                    loading = loading,
                    contentDescription = cdRegisterButton,
                    buttonType = UnifiedButtonType.PRIMARY,
                    maxWidth = true
                )

                Spacer(modifier = Modifier.height(ResponsiveSpacing.getElementSpacing(SpacingContext.BUTTON)))

                UnifiedTextButton(
                    onClick = onRegisterBack,
                    text = stringResource(R.string.register_back_button_text),
                    enabled = !loading,
                    contentDescription = cdRegisterBackButton,
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
                    text = stringResource(R.string.register_welcome_title),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                        .semantics { 
                            heading()
                            contentDescription = "Create Account"
                        }
                )

                Text(
                    text = stringResource(R.string.register_welcome_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION))
                        .semantics { 
                            contentDescription = "Join us to start your accessibility journey"
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
                                contentDescription = "$cdRegisterErrorMessage $finalGeneralError"
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
                    label = { Text(stringResource(R.string.register_email_label)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                        .testTag("registerEmailField")
                        .semantics {
                            contentDescription = cdRegisterEmailField
                            // Announce error state to screen readers
                            if (finalEmailErrorState != null) {
                                error(finalEmailErrorState)
                                stateDescription = finalEmailErrorState
                            } else if (email.isEmpty()) {
                                stateDescription = cdRegisterEmailEmptyState
                            }
                        },
                    placeholder = { Text(stringResource(R.string.register_email_placeholder)) },
                    isError = finalEmailErrorState != null,
                    enabled = !loading
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
                    label = { Text(stringResource(R.string.register_password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                        .testTag("registerPasswordField")
                        .semantics {
                            contentDescription = cdRegisterPasswordField
                            stateDescription = cdRegisterPasswordFieldState
                            // Add password semantics for accessibility
                            password()
                        },
                    placeholder = { Text(stringResource(R.string.register_password_placeholder)) },
                    isError = finalPasswordErrorState != null,
                    enabled = !loading
                )

                // Separate error message for password validation (for test accessibility)
                if (finalPasswordErrorState != null) {
                    Text(
                        text = finalPasswordErrorState,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.CONTENT))
                            .semantics {
                                contentDescription = "Error suggestion message: $finalPasswordErrorState"
                            }
                    )
                }

                // Confirm Password TextField with comprehensive accessibility support
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        error = null
                    },
                    label = { Text(stringResource(R.string.register_confirm_password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION))
                        .testTag("registerConfirmPasswordField")
                        .semantics {
                            contentDescription = cdRegisterConfirmPasswordField
                            stateDescription = cdRegisterConfirmPasswordFieldState
                            // Add password semantics for accessibility
                            password()
                        },
                    placeholder = { Text(stringResource(R.string.register_confirm_password_placeholder)) },
                    isError = finalConfirmPasswordErrorState != null,
                    enabled = !loading
                )

                // Separate error message for confirm password validation (for test accessibility)
                if (finalConfirmPasswordErrorState != null) {
                    Text(
                        text = finalConfirmPasswordErrorState,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = ResponsiveSpacing.getElementSpacing(SpacingContext.SECTION))
                            .semantics {
                                contentDescription = "Error suggestion message: $finalConfirmPasswordErrorState"
                            }
                    )
                }

                UnifiedButton(
                    onClick = { register() },
                    text = stringResource(R.string.register_button_text),
                    enabled = !loading,
                    loading = loading,
                    contentDescription = cdRegisterButton,
                    buttonType = UnifiedButtonType.PRIMARY,
                    maxWidth = true
                )

                Spacer(modifier = Modifier.height(ResponsiveSpacing.getElementSpacing(SpacingContext.BUTTON)))

                UnifiedTextButton(
                    onClick = onRegisterBack,
                    text = stringResource(R.string.register_back_button_text),
                    enabled = !loading,
                    contentDescription = cdRegisterBackButton,
                    maxWidth = true
                )

                Spacer(modifier = Modifier.weight(0.2f))
            }
        }
    }
}