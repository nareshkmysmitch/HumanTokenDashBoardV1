package com.healthanalytics.android.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Repository for managing user preferences using DataStore.
 */
class PreferencesRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("accessToken")
    }

    /**
     * Flow of access token value.
     * Emits null if no token is stored.
     */
    val accessToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }
        .catch { e ->
            // Log error or handle it appropriately
            throw e
        }

    /**
     * Save the access token to DataStore.
     * @param token The token to save
     */
    suspend fun saveAccessToken(token: String) {
        try {
            dataStore.edit { preferences ->
                preferences[ACCESS_TOKEN_KEY] = token
            }
        } catch (e: Exception) {
            // Log error or handle it appropriately
            throw e
        }
    }

    /**
     * Clear the stored access token.
     */
    suspend fun clearAccessToken() {
        try {
            dataStore.edit { preferences ->
                preferences.remove(ACCESS_TOKEN_KEY)
            }
        } catch (e: Exception) {
            // Log error or handle it appropriately
            throw e
        }
    }
} 