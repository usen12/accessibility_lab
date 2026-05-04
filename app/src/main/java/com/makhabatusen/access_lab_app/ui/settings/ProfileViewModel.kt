package com.makhabatusen.access_lab_app.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set
    
    var hasProfilePicture by mutableStateOf(false)
        private set
    
    var userName by mutableStateOf("")
        private set
    
    var userBio by mutableStateOf("")
        private set
    
    var isEditingName by mutableStateOf(false)
        private set
    
    var isEditingBio by mutableStateOf(false)
        private set
    
    var isSaving by mutableStateOf(false)
        private set
    
    var showSaveSuccess by mutableStateOf(false)
        private set
    
    var showOptionsDialog by mutableStateOf(false)
        private set
    
    fun updateProfilePicture(uri: Uri?) {
        selectedImageUri = uri
        hasProfilePicture = uri != null
    }
    
    fun clearProfilePicture() {
        selectedImageUri = null
        hasProfilePicture = false
    }
    
    fun updateUserName(name: String) {
        userName = name
    }
    
    fun updateUserBio(bio: String) {
        userBio = bio
    }
    
    fun setEditingNameState(editing: Boolean) {
        isEditingName = editing
    }
    
    fun setEditingBioState(editing: Boolean) {
        isEditingBio = editing
    }
    
    fun setSavingState(saving: Boolean) {
        isSaving = saving
    }
    
    fun showSaveSuccessMessage() {
        showSaveSuccess = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            showSaveSuccess = false
        }
    }
    
    fun setShowOptionsDialogState(show: Boolean) {
        showOptionsDialog = show
    }
    
    fun getProfilePictureContentDescription(userName: String): String {
        return if (hasProfilePicture) {
            "Profile picture of $userName"
        } else {
            "Add profile picture"
        }
    }
}