package com.fitlog.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SessionManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SessionManager {

    private companion object {
        val SESSION_KEY = stringPreferencesKey("supabase_session")
    }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override suspend fun loadSession(): UserSession? {
        return try {
            val prefs = context.sessionDataStore.data.first()
            val encoded = prefs[SESSION_KEY] ?: return null
            json.decodeFromString<UserSession>(encoded)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveSession(session: UserSession) {
        try {
            context.sessionDataStore.edit { prefs ->
                prefs[SESSION_KEY] = json.encodeToString(session)
            }
        } catch (e: Exception) {
            // ignore write errors — session will be in-memory only
        }
    }

    override suspend fun deleteSession() {
        try {
            context.sessionDataStore.edit { prefs ->
                prefs.remove(SESSION_KEY)
            }
        } catch (e: Exception) {
            // ignore
        }
    }
}
