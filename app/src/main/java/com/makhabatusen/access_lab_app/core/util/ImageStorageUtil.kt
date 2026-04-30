package com.makhabatusen.access_lab_app.core.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ImageStorageUtil {
    private const val PROFILE_PICTURE_FILENAME = "profile_picture.jpg"
    
    /**
     * Save profile picture to internal storage
     */
    suspend fun saveProfilePicture(context: Context, imageUri: Uri): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(imageUri)
                    .build()
                
                val drawable = imageLoader.execute(request).image
                val bitmap = drawable?.toBitmap()
                
                if (bitmap != null) {
                    val file = File(context.filesDir, PROFILE_PICTURE_FILENAME)
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    }
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Load profile picture from internal storage
     */
    fun loadProfilePicture(context: Context): Uri? {
        return try {
            val file = File(context.filesDir, PROFILE_PICTURE_FILENAME)
            if (file.exists()) {
                Uri.fromFile(file)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Check if profile picture exists in storage
     */
    fun hasProfilePicture(context: Context): Boolean {
        val file = File(context.filesDir, PROFILE_PICTURE_FILENAME)
        return file.exists()
    }
    
    /**
     * Delete profile picture from storage
     */
    fun deleteProfilePicture(context: Context): Boolean {
        return try {
            val file = File(context.filesDir, PROFILE_PICTURE_FILENAME)
            if (file.exists()) {
                file.delete()
            } else {
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
} 