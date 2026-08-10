package com.example.data

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager(private val context: Context) {

    private val auth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseAuth.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w("FirebaseAuthManager", "FirebaseAuth unavailable: ${e.message}")
            null
        }
    }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth?.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _currentUser.value = firebaseAuth.currentUser
    }

    init {
        auth?.addAuthStateListener(authStateListener)
    }

    fun clearError() {
        _authError.value = null
    }

    suspend fun signInAnonymously(): FirebaseUser? {
        val authInstance = auth ?: run {
            _authError.value = "Firebase Auth unavailable"
            return null
        }
        _isLoading.value = true
        _authError.value = null
        return try {
            val result = authInstance.signInAnonymously().await()
            _currentUser.value = result.user
            result.user
        } catch (e: Exception) {
            _authError.value = e.localizedMessage ?: "Anonymous sign in failed"
            null
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun signInWithEmail(email: String, pass: String): FirebaseUser? {
        val authInstance = auth ?: run {
            _authError.value = "Firebase Auth unavailable"
            return null
        }
        if (email.isBlank() || pass.isBlank()) {
            _authError.value = "Email and password cannot be empty"
            return null
        }
        _isLoading.value = true
        _authError.value = null
        return try {
            val result = authInstance.signInWithEmailAndPassword(email, pass).await()
            _currentUser.value = result.user
            result.user
        } catch (e: Exception) {
            _authError.value = e.localizedMessage ?: "Sign in failed"
            null
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun signUpWithEmail(email: String, pass: String): FirebaseUser? {
        val authInstance = auth ?: run {
            _authError.value = "Firebase Auth unavailable"
            return null
        }
        if (email.isBlank() || pass.length < 6) {
            _authError.value = "Password must be at least 6 characters"
            return null
        }
        _isLoading.value = true
        _authError.value = null
        return try {
            val result = authInstance.createUserWithEmailAndPassword(email, pass).await()
            _currentUser.value = result.user
            result.user
        } catch (e: Exception) {
            _authError.value = e.localizedMessage ?: "Sign up failed"
            null
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun signInWithGoogle(webClientId: String): FirebaseUser? {
        val authInstance = auth ?: run {
            _authError.value = "Firebase Auth unavailable"
            return null
        }
        _isLoading.value = true
        _authError.value = null
        return try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialResult = credentialManager.getCredential(context, request)
            val credential = credentialResult.credential

            if (credential is GoogleIdTokenCredential) {
                val firebaseCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
                val authResult = authInstance.signInWithCredential(firebaseCredential).await()
                _currentUser.value = authResult.user
                authResult.user
            } else {
                _authError.value = "Invalid credential type received"
                null
            }
        } catch (e: GetCredentialException) {
            _authError.value = "Google Sign-In canceled or unavailable: ${e.localizedMessage}"
            null
        } catch (e: Exception) {
            _authError.value = e.localizedMessage ?: "Google Sign-In failed"
            null
        } finally {
            _isLoading.value = false
        }
    }

    fun signOut() {
        auth?.signOut()
        _currentUser.value = null
    }
}
