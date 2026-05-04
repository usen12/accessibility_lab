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
    
}