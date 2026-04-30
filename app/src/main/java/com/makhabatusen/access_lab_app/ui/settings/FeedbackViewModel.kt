package com.makhabatusen.access_lab_app.ui.settings

import android.content.Context
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makhabatusen.access_lab_app.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel for managing feedback form state and submission
 * Implements comprehensive validation and device info collection
 */
class FeedbackViewModel : ViewModel() {
    
    // Feedback form state
    var selectedFeedbackType by mutableStateOf(FeedbackType.GENERAL)
        private set
    
    var feedbackText by mutableStateOf("")
        private set
    
    var deviceInfo by mutableStateOf(DeviceInfo())
        private set
    
    // UI state
    var isSubmitting by mutableStateOf(false)
        private set
    
    var showSuccessDialog by mutableStateOf(false)
        private set
    
    // Validation state
    val isFormValid: Boolean
        get() = feedbackText.trim().isNotEmpty() && feedbackText.trim().length >= 10
    
    /**
     * Update the selected feedback type
     */
    fun updateFeedbackType(type: FeedbackType) {
        selectedFeedbackType = type
    }
    
    /**
     * Update the feedback text content
     */
    fun updateFeedbackText(text: String) {
        feedbackText = text
    }
    
    /**
     * Load device information for debugging purposes
     */
    fun loadDeviceInfo(context: Context) {
        viewModelScope.launch {
            deviceInfo = collectDeviceInfo(context)
        }
    }
    
    /**
     * Submit feedback with comprehensive error handling
     */
    fun submitFeedback(context: Context) {
        android.util.Log.d("FeedbackViewModel", "submitFeedback called")
        android.util.Log.d("FeedbackViewModel", "isFormValid: $isFormValid")
        android.util.Log.d("FeedbackViewModel", "feedbackText length: ${feedbackText.length}")
        android.util.Log.d("FeedbackViewModel", "feedbackText trimmed length: ${feedbackText.trim().length}")
        
        if (!isFormValid) {
            android.util.Log.d("FeedbackViewModel", "Form validation failed, returning early")
            return
        }
        
        android.util.Log.d("FeedbackViewModel", "Starting feedback submission")
        viewModelScope.launch {
            isSubmitting = true
            
            try {
                // Simulate network delay
                delay(1000)
                
                // Show success dialog
                showSuccessDialog = true
                
            } catch (e: Exception) {
                android.util.Log.e("FeedbackViewModel", "Error submitting feedback", e)
            } finally {
                isSubmitting = false
            }
        }
    }
    
    /**
     * Hide the success dialog
     */
    fun hideSuccessDialog() {
        showSuccessDialog = false
        // Reset form after dialog is dismissed
        resetForm()
    }
    
    /**
     * Reset the feedback form to initial state
     */
    fun resetForm() {
        selectedFeedbackType = FeedbackType.GENERAL
        feedbackText = ""
    }
    
    /**
     * Collect comprehensive device information for debugging
     */
    private suspend fun collectDeviceInfo(context: Context): DeviceInfo {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val displayMetrics = context.resources.displayMetrics
            
            DeviceInfo(
                appVersion = "${packageInfo.versionName} (${packageInfo.versionCode})",
                androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
                screenResolution = "${displayMetrics.widthPixels}x${displayMetrics.heightPixels}",
                timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            )
        } catch (e: Exception) {
            android.util.Log.e("FeedbackViewModel", "Error collecting device info", e)
            DeviceInfo() // Return empty device info on error
        }
    }
    

}

/**
 * Enum representing different types of feedback
 */
enum class FeedbackType {
    GENERAL,
    BUG_REPORT,
    FEATURE_REQUEST,
    ACCESSIBILITY_ISSUE,
    OTHER;
    
    fun getDisplayNameRes(): Int {
        return when (this) {
            GENERAL -> R.string.feedback_type_general
            BUG_REPORT -> R.string.feedback_type_bug
            FEATURE_REQUEST -> R.string.feedback_type_feature
            ACCESSIBILITY_ISSUE -> R.string.feedback_type_accessibility
            OTHER -> R.string.feedback_type_other
        }
    }
    
    fun getDescriptionRes(): Int {
        return when (this) {
            GENERAL -> R.string.feedback_type_general_desc
            BUG_REPORT -> R.string.feedback_type_bug_desc
            FEATURE_REQUEST -> R.string.feedback_type_feature_desc
            ACCESSIBILITY_ISSUE -> R.string.feedback_type_accessibility_desc
            OTHER -> R.string.feedback_type_other_desc
        }
    }
}

/**
 * Data class for device information
 */
data class DeviceInfo(
    val appVersion: String = "",
    val androidVersion: String = "",
    val deviceModel: String = "",
    val screenResolution: String = "",
    val timestamp: String = ""
)

 