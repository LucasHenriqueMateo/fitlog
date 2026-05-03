package com.fitlog.app.data.repository

import com.fitlog.app.data.remote.datasource.AuthRemoteDataSource
import com.fitlog.app.util.Result
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) {
    fun isLoggedIn(): Boolean = authRemoteDataSource.currentSession() != null

    fun getCurrentUserId(): String? = authRemoteDataSource.currentUser()?.id

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            authRemoteDataSource.signIn(email, password)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Sign in failed", e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            authRemoteDataSource.signUp(email, password)
            // signUp succeeds but email confirmation may be required — caller decides UX
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Sign up failed", e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return try {
            authRemoteDataSource.signInWithGoogle(idToken)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Google sign in failed", e)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            authRemoteDataSource.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Sign out failed", e)
        }
    }
}
