package com.makhabatusen.access_lab_app.core.util

import android.content.Context
import android.content.SharedPreferences

object UserDataStorageUtil {
    private const val PREF_NAME = "user_data_preferences"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_BIO = "user_bio"
    
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Save user name to SharedPreferences
     */
    fun saveUserName(context: Context, userName: String) {
        try {
            val prefs = getSharedPreferences(context)
            prefs.edit().putString(KEY_USER_NAME, userName).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Load user name from SharedPreferences
     */
    fun loadUserName(context: Context): String? {
        return try {
            val prefs = getSharedPreferences(context)
            prefs.getString(KEY_USER_NAME, null)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Save user bio to SharedPreferences
     */
    fun saveUserBio(context: Context, userBio: String) {
        try {
            val prefs = getSharedPreferences(context)
            prefs.edit().putString(KEY_USER_BIO, userBio).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Load user bio from SharedPreferences
     */
    fun loadUserBio(context: Context): String? {
        return try {
            val prefs = getSharedPreferences(context)
            prefs.getString(KEY_USER_BIO, null)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Clear user name from SharedPreferences
     */
    fun clearUserName(context: Context) {
        try {
            val prefs = getSharedPreferences(context)
            prefs.edit().remove(KEY_USER_NAME).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Clear user bio from SharedPreferences
     */
    fun clearUserBio(context: Context) {
        try {
            val prefs = getSharedPreferences(context)
            prefs.edit().remove(KEY_USER_BIO).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Clear all user data from SharedPreferences
     */
    fun clearAllUserData(context: Context) {
        try {
            val prefs = getSharedPreferences(context)
            prefs.edit().clear().apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Check if user name exists in SharedPreferences
     */
    fun hasUserName(context: Context): Boolean {
        return try {
            val prefs = getSharedPreferences(context)
            prefs.contains(KEY_USER_NAME)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Check if user bio exists in SharedPreferences
     */
    fun hasUserBio(context: Context): Boolean {
        return try {
            val prefs = getSharedPreferences(context)
            prefs.contains(KEY_USER_BIO)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
} 