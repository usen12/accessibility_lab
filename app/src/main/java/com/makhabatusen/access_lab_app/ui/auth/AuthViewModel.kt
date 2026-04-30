package com.makhabatusen.access_lab_app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _isAuthChecked = MutableStateFlow(false)
    val isAuthChecked: StateFlow<Boolean> = _isAuthChecked

    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = _currentUser

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _isLoggedIn.value = user != null
        _currentUser.value = user
        _isAuthChecked.value = true
    }

    init {
        auth.addAuthStateListener(authListener)
        if (auth.currentUser != null) {
            _isAuthChecked.value = true
        } else {
            // Fallback: mark auth as checked after a timeout in case the listener is slow
            viewModelScope.launch {
                delay(3000)
                _isAuthChecked.value = true
            }
        }
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        super.onCleared()
    }

    fun signOut() {
        auth.signOut()
        _isAuthChecked.value = false
    }

    fun signInWithEmailAndPassword(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(mapAuthError(task.exception))
                }
            }
    }

    fun signInAnonymously(onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(mapAuthError(task.exception))
                }
            }
    }

    fun createUserWithEmailAndPassword(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(mapAuthError(task.exception))
                }
            }
    }

    fun isCurrentUserAnonymous(): Boolean = auth.currentUser?.isAnonymous == true

    fun getCurrentUserEmail(): String? = auth.currentUser?.email

    fun getCurrentUserUid(): String? = auth.currentUser?.uid

    private fun mapAuthError(exception: Exception?): String = when (exception) {
        is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
        is FirebaseAuthInvalidUserException -> "No account found with this email"
        is FirebaseAuthWeakPasswordException -> "Password is too weak. Please choose a stronger password"
        is FirebaseAuthUserCollisionException -> "An account with this email already exists"
        is FirebaseNetworkException -> "Network error. Please check your connection and try again"
        else -> exception?.localizedMessage ?: "Authentication failed. Please try again"
    }
}