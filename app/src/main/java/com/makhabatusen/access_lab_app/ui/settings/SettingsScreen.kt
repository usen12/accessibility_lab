package com.makhabatusen.access_lab_app.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.makhabatusen.access_lab_app.R
import com.makhabatusen.access_lab_app.core.util.ImageStorageUtil
import com.makhabatusen.access_lab_app.ui.components.ProfileTopBar
import com.makhabatusen.access_lab_app.ui.components.UnifiedButton
import com.makhabatusen.access_lab_app.ui.components.UnifiedButtonType
import com.makhabatusen.access_lab_app.core.util.UserDataStorageUtil
import com.makhabatusen.access_lab_app.ui.util.isLandscape
import com.makhabatusen.access_lab_app.ui.util.isLargeTablet
import com.makhabatusen.access_lab_app.ui.util.isTablet

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onAccessibilitySettings: () -> Unit,
    onLanguageSettings: () -> Unit,
    onFeedback: () -> Unit
) {
    val profileViewModel = viewModel<ProfileViewModel>()
    val isLandscape = isLandscape()
    val isTablet = isTablet()

    val padding = when {
        isLargeTablet() -> 56.dp
        isTablet -> 48.dp
        isLandscape -> 40.dp
        else -> 24.dp
    }

    val maxContentWidth = when {
        isLargeTablet() -> 1000.dp
        isTablet -> 800.dp
        isLandscape -> 600.dp
        else -> 400.dp
    }


    Scaffold(
        topBar = {
            ProfileTopBar()
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = maxContentWidth)
                    .padding(padding)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Settings Section - Emphasized
                ProfileSettingsSection(profileViewModel, isTablet)

                Spacer(modifier = Modifier.height(32.dp))

                // App Settings Section
                AppSettingsSection(
                    isTablet = isTablet,
                    onAccessibilitySettings = onAccessibilitySettings,
                    onLanguageSettings = onLanguageSettings,
                    onFeedback = onFeedback
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Logout Button
                val logoutButtonDescription = stringResource(R.string.cd_profile_logout_button)
                UnifiedButton(
                    onClick = onLogout,
                    text = stringResource(R.string.profile_logout_button),
                    icon = Icons.Default.ExitToApp,
                    contentDescription = logoutButtonDescription,
                    buttonType = UnifiedButtonType.ERROR,
                    maxWidth = true
                )
            }
        }
    }
}

@Composable
private fun ProfileSettingsSection(profileViewModel: ProfileViewModel, isTablet: Boolean) {
    val context = LocalContext.current
    val authViewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.makhabatusen.access_lab_app.ui.auth.AuthViewModel>()
    val currentUser by authViewModel.currentUser.collectAsState()

    val profilePictureLabel = stringResource(R.string.profile_picture_label)
    val addPictureText = stringResource(R.string.profile_add_picture)
    val changePictureText = stringResource(R.string.profile_change_picture)
    val placeholderText = stringResource(R.string.profile_picture_placeholder)
    val nameLabel = stringResource(R.string.profile_name_label)
    val namePlaceholder = stringResource(R.string.profile_name_placeholder)
    val bioLabel = stringResource(R.string.profile_bio_label)
    val bioPlaceholder = stringResource(R.string.profile_bio_placeholder)
    val tapToChangeText = stringResource(R.string.profile_tap_to_change)
    val tapToAddText = stringResource(R.string.profile_tap_to_add)
    val addNameText = stringResource(R.string.profile_add_name)
    val addBioText = stringResource(R.string.profile_add_bio)
    val editNameText = stringResource(R.string.profile_edit_name)
    val editBioText = stringResource(R.string.profile_edit_bio)
    val saveNameText = stringResource(R.string.profile_save_name)
    val cancelEditingText = stringResource(R.string.profile_cancel_editing)
    val saveSuccessText = stringResource(R.string.profile_save_success)
    val cdProfileSettingsSection = stringResource(R.string.cd_profile_settings_section)
    val profileUserFallback = stringResource(R.string.profile_user_fallback)

    // Load saved profile data on startup
    LaunchedEffect(Unit) {
        val savedUri = ImageStorageUtil.loadProfilePicture(context)
        if (savedUri != null) {
            profileViewModel.updateProfilePicture(savedUri)
        }

        val savedUserName = UserDataStorageUtil.loadUserName(context)
        if (!savedUserName.isNullOrBlank()) {
            profileViewModel.updateUserName(savedUserName)
        }

        val savedUserBio = UserDataStorageUtil.loadUserBio(context)
        if (!savedUserBio.isNullOrBlank()) {
            profileViewModel.updateUserBio(savedUserBio)
        }
    }

    // Save new profile picture when selected
    LaunchedEffect(profileViewModel.isSaving) {
        val pendingUri = profileViewModel.selectedImageUri
        if (profileViewModel.isSaving && pendingUri != null) {
            val success =
                ImageStorageUtil.saveProfilePicture(context, pendingUri)
            if (success) {
                // Update the URI to point to the saved file
                val savedUri = ImageStorageUtil.loadProfilePicture(context)
                if (savedUri != null) {
                    profileViewModel.updateProfilePicture(savedUri)
                }
            }
            profileViewModel.setSavingState(false)
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            profileViewModel.updateProfilePicture(uri)
            profileViewModel.setSavingState(true)
        }
    }

    Card(
                                modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = cdProfileSettingsSection
                            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isTablet) 12.dp else 8.dp
        ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(
                if (isTablet) 24.dp else 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Settings Heading - Prominent and Emphasized
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_title),
                    style = if (isTablet) {
                        MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    textAlign = TextAlign.Center,
                    softWrap = true,
                    overflow = TextOverflow.Visible,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 20.dp)
                        .semantics { heading() }
                )
            }
            
            // Profile Picture Section - Centered
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isTablet) 140.dp else 120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                        .clickable {
                            if (profileViewModel.hasProfilePicture) {
                                profileViewModel.setShowOptionsDialogState(true)
                            } else {
                                imagePicker.launch("image/*")
                            }
                        }
                        .semantics {
                            contentDescription =
                                profileViewModel.getProfilePictureContentDescription(
                                    if (profileViewModel.userName.isNotBlank()) profileViewModel.userName else profileUserFallback
                                )
                            role = Role.Button
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileViewModel.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(if (isTablet) 48.dp else 40.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp
                        )
                    } else if (profileViewModel.hasProfilePicture && profileViewModel.selectedImageUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(profileViewModel.selectedImageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = placeholderText,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = addPictureText,
                            modifier = Modifier.size(if (isTablet) 48.dp else 40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (profileViewModel.hasProfilePicture) tapToChangeText else tapToAddText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    softWrap = true,
                    overflow = TextOverflow.Visible
                )
            }

            // Name Field Section - Full Width
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = nameLabel,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    softWrap = true,
                    overflow = TextOverflow.Visible
                )

                if (profileViewModel.isEditingName) {
                    val nameFieldDescription = stringResource(R.string.cd_profile_name_field)
                    OutlinedTextField(
                        value = profileViewModel.userName,
                        onValueChange = { profileViewModel.updateUserName(it) },
                        placeholder = {
                            Text(
                                text = namePlaceholder,
                                style = MaterialTheme.typography.bodyMedium,
                                softWrap = true,
                                overflow = TextOverflow.Visible
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = nameFieldDescription
                            },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        UserDataStorageUtil.saveUserName(
                                            context,
                                            profileViewModel.userName
                                        )
                                        profileViewModel.setEditingNameState(false)
                                        profileViewModel.showSaveSuccessMessage()
                                    },
                                    modifier = Modifier.semantics {
                                        role = Role.Button
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = saveNameText,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        val savedName =
                                            UserDataStorageUtil.loadUserName(context)
                                        profileViewModel.updateUserName(savedName ?: "")
                                        profileViewModel.setEditingNameState(false)
                                    },
                                    modifier = Modifier.semantics {
                                        role = Role.Button
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = cancelEditingText,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    )
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { profileViewModel.setEditingNameState(true) }
                            .semantics {
                                contentDescription = "$nameLabel: ${if (profileViewModel.userName.isNotBlank()) profileViewModel.userName else addNameText}. Tap to edit."
                                role = Role.Button
                            },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (profileViewModel.userName.isNotBlank()) profileViewModel.userName else addNameText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (profileViewModel.userName.isNotBlank()) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                softWrap = true,
                                overflow = TextOverflow.Visible,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = editNameText,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Bio Field Section - Full Width
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = bioLabel,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    softWrap = true,
                    overflow = TextOverflow.Visible
                )

                if (profileViewModel.isEditingBio) {
                    val bioFieldDescription = stringResource(R.string.cd_profile_bio_field)
                    OutlinedTextField(
                        value = profileViewModel.userBio,
                        onValueChange = { profileViewModel.updateUserBio(it) },
                        placeholder = {
                            Text(
                                text = bioPlaceholder,
                                style = MaterialTheme.typography.bodyMedium,
                                softWrap = true,
                                overflow = TextOverflow.Visible
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp)
                            .semantics {
                                contentDescription = bioFieldDescription
                            },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                        ),
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        UserDataStorageUtil.saveUserBio(
                                            context,
                                            profileViewModel.userBio
                                        )
                                        profileViewModel.setEditingBioState(false)
                                        profileViewModel.showSaveSuccessMessage()
                                    },
                                    modifier = Modifier.semantics {
                                        role = Role.Button
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = saveNameText,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        val savedBio = UserDataStorageUtil.loadUserBio(context)
                                        profileViewModel.updateUserBio(savedBio ?: "")
                                        profileViewModel.setEditingBioState(false)
                                    },
                                    modifier = Modifier.semantics {
                                        role = Role.Button
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = cancelEditingText,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    )
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { profileViewModel.setEditingBioState(true) }
                            .semantics {
                                contentDescription = "$bioLabel: ${if (profileViewModel.userBio.isNotBlank()) profileViewModel.userBio else addBioText}. Tap to edit."
                                role = Role.Button
                            },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = if (profileViewModel.userBio.isNotBlank()) profileViewModel.userBio else addBioText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (profileViewModel.userBio.isNotBlank()) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                softWrap = true,
                                overflow = TextOverflow.Visible,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = editBioText,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Email Display Section - Full Width
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.profile_email_label),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    softWrap = true,
                    overflow = TextOverflow.Visible
                )

                val emailFieldDescription = stringResource(R.string.cd_profile_email_field)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = emailFieldDescription
                        },
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = when {
                                currentUser != null && currentUser?.email != null -> currentUser?.email.orEmpty()
                                currentUser != null && currentUser?.isAnonymous == true -> stringResource(R.string.profile_anonymous_user)
                                else -> stringResource(R.string.profile_not_signed_in)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            softWrap = true,
                            overflow = TextOverflow.Visible,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Save Success Message
            if (profileViewModel.showSaveSuccess) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = saveSuccessText,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            softWrap = true,
                            overflow = TextOverflow.Visible
                        )
                    }
                }
            }
        }
    }

    // Profile Picture Options Dialog
    if (profileViewModel.showOptionsDialog) {
        AlertDialog(
            onDismissRequest = { profileViewModel.setShowOptionsDialogState(false) },
            title = {
                Text(
                    text = profilePictureLabel,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    softWrap = true,
                    overflow = TextOverflow.Visible
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.profile_choose_option),
                    style = MaterialTheme.typography.bodyMedium,
                    softWrap = true,
                    overflow = TextOverflow.Visible
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        profileViewModel.setShowOptionsDialogState(false)
                        imagePicker.launch("image/*")
                    },
                    modifier = Modifier.semantics {
                        role = Role.Button
                    }
                ) {
                    Text(
                        text = changePictureText,
                        style = MaterialTheme.typography.bodyLarge,
                        softWrap = true,
                        overflow = TextOverflow.Visible
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        profileViewModel.setShowOptionsDialogState(false)
                        ImageStorageUtil.deleteProfilePicture(context)
                        profileViewModel.clearProfilePicture()
                    },
                    modifier = Modifier.semantics {
                        role = Role.Button
                    }
                ) {
                    Text(
                        text = stringResource(R.string.profile_remove_picture),
                        style = MaterialTheme.typography.bodyLarge,
                        softWrap = true,
                        overflow = TextOverflow.Visible
                    )
                }
            }
        )
    }
}

@Composable
private fun AppSettingsSection(
    isTablet: Boolean,
    onAccessibilitySettings: () -> Unit,
    onLanguageSettings: () -> Unit,
    onFeedback: () -> Unit
) {
    val settingsTitle = stringResource(R.string.app_settings_heading)
    val accessibilitySettings = stringResource(R.string.profile_accessibility_settings)
    val languageSettings = stringResource(R.string.language_settings_title)
    val feedback = stringResource(R.string.feedback_title)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { 
                contentDescription = settingsTitle
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isTablet) 8.dp else 4.dp
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(
                if (isTablet) 32.dp else 24.dp
            )
        ) {
            Text(
                text = settingsTitle,
                style = if (isTablet) {
                    MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                },
                textAlign = TextAlign.Center,
                softWrap = true,
                overflow = TextOverflow.Visible,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .semantics { heading() }
            )
            
            // Settings Items
            SettingsItem(
                icon = Icons.Default.Info,
                title = accessibilitySettings,
                onClick = onAccessibilitySettings
            )
            
            SettingsItem(
                icon = painterResource(R.drawable.ic_language),
                title = languageSettings,
                onClick = onLanguageSettings
            )
            
            SettingsItem(
                icon = painterResource(R.drawable.ic_feedback),
                title = feedback,
                onClick = onFeedback
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    SettingsItemContent(
        icon = { 
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = title,
        onClick = onClick
    )
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.painter.Painter,
    title: String,
    onClick: () -> Unit
) {
    SettingsItemContent(
        icon = { 
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = title,
        onClick = onClick
    )
}

@Composable
private fun SettingsItemContent(
    icon: @Composable () -> Unit,
    title: String,
    onClick: () -> Unit
) {
    val settingsItemDescription = stringResource(R.string.cd_profile_settings_item, title)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics { 
                contentDescription = settingsItemDescription
                role = Role.Button
            },
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                softWrap = true,
                overflow = TextOverflow.Visible,
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 