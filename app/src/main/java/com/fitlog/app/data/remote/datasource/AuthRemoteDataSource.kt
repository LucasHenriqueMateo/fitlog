package com.fitlog.app.data.remote.datasource

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val client: SupabaseClient
) {
    suspend fun signIn(email: String, password: String) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signUp(email: String, password: String) {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signInWithGoogle(idToken: String) {
        client.auth.signInWith(IDToken) {
            this.idToken = idToken
            provider = Google
        }
    }

    suspend fun signOut() {
        client.auth.signOut()
    }

    fun currentUser() = client.auth.currentUserOrNull()

    fun currentSession() = client.auth.currentSessionOrNull()

    val sessionStatus get() = client.auth.sessionStatus
}
