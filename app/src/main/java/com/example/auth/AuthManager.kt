package com.example.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthManager"

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    object Guest : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthManager(
    private val context: Context,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val _authState = MutableStateFlow<AuthState>(
        if (auth.currentUser != null) AuthState.Authenticated(auth.currentUser!!) else AuthState.Guest
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isGuest: Boolean
        get() = auth.currentUser == null

    private val authListener = FirebaseAuth.AuthStateListener { fbAuth ->
        val user = fbAuth.currentUser
        if (user != null) {
            _authState.value = AuthState.Authenticated(user)
        } else {
            _authState.value = AuthState.Guest
        }
    }

    init {
        auth.addAuthStateListener(authListener)
    }

    /**
     * Signs in interactively via Jetpack Credential Manager and Google Sign-In.
     */
    suspend fun signInWithGoogle(): Result<FirebaseUser> {
        _authState.value = AuthState.Loading
        return try {
            val serverClientId = try {
                val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                if (resId != 0) context.getString(resId) else null
            } catch (e: Exception) {
                null
            }

            if (serverClientId.isNullOrBlank()) {
                val msg = "Google Sign-In is not enabled yet in Firebase project (snake-game-a75f0). Please enable Google Sign-In in Firebase Console, or continue as Guest."
                _authState.value = AuthState.Error(msg)
                return Result.failure(IllegalStateException(msg))
            }

            val signInOption = GetSignInWithGoogleOption.Builder(serverClientId).build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()

            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential is androidx.credentials.CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val user = authResult.user ?: throw IllegalStateException("Firebase user was null after Google sign-in")
                _authState.value = AuthState.Authenticated(user)
                Result.success(user)
            } else {
                throw IllegalStateException("Unexpected credential type: ${credential.type}")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "Google Sign-In was cancelled by the user")
            _authState.value = if (auth.currentUser != null) AuthState.Authenticated(auth.currentUser!!) else AuthState.Guest
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In failed", e)
            _authState.value = AuthState.Error(e.localizedMessage ?: "Google sign-in failed")
            Result.failure(e)
        }
    }

    suspend fun registerWithEmail(email: String, pass: String): Result<FirebaseUser> {
        val friendlyMsg = "Email/Password registration is disabled in Firebase for this project. Please use 'Sign in with Google' for instant cloud sync."
        _authState.value = AuthState.Error(friendlyMsg)
        return Result.failure(Exception(friendlyMsg))
    }

    suspend fun loginWithEmail(email: String, pass: String): Result<FirebaseUser> {
        val friendlyMsg = "Email/Password sign-in is disabled in Firebase for this project. Please use 'Sign in with Google' for instant cloud sync."
        _authState.value = AuthState.Error(friendlyMsg)
        return Result.failure(Exception(friendlyMsg))
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Reset password error", e)
            Result.failure(e)
        }
    }

    suspend fun resendEmailVerification(): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw IllegalStateException("No user logged in")
            user.sendEmailVerification().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Resend verification error", e)
            Result.failure(e)
        }
    }

    suspend fun reloadUser(): Result<FirebaseUser?> {
        return try {
            val user = auth.currentUser
            user?.reload()?.await()
            val updated = auth.currentUser
            if (updated != null) {
                _authState.value = AuthState.Authenticated(updated)
            }
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun continueAsGuest() {
        auth.signOut()
        _authState.value = AuthState.Guest
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Guest
    }

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw IllegalStateException("No user logged in")
            user.delete().await()
            _authState.value = AuthState.Guest
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Delete account error", e)
            Result.failure(e)
        }
    }
}
