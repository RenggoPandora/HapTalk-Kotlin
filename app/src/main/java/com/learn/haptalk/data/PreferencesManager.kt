package com.learn.haptalk.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Manages user session preferences using DataStore
 * Stores sessionId persistently
 */
class PreferencesManager(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "haptalk_preferences")
        private val SESSION_ID_KEY = stringPreferencesKey("session_id")
    }

    /**
     * Get or create sessionId
     */
    val sessionId: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SESSION_ID_KEY] ?: ""
    }

    /**
     * Generate and save new sessionId
     */
    suspend fun createSessionId(): String {
        val newSessionId = generateShortSessionId()
        context.dataStore.edit { preferences ->
            preferences[SESSION_ID_KEY] = newSessionId
        }
        return newSessionId
    }

    /**
     * Check if session exists
     */
    suspend fun hasSession(): Boolean {
        var exists = false
        context.dataStore.data.map { preferences ->
            exists = !preferences[SESSION_ID_KEY].isNullOrEmpty()
        }
        return exists
    }

    /**
     * Generate a short, readable sessionId
     * Format: first 8 chars of UUID without dashes
     */
    private fun generateShortSessionId(): String {
        return "u" + UUID.randomUUID().toString().replace("-", "").substring(0, 7)
    }
}

