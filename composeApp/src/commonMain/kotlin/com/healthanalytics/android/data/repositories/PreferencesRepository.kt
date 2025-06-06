package com.healthanalytics.android.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

object PreferencesKeys {
    val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
}

/**
 * Repository for managing user preferences using DataStore.
 */
class PreferencesRepository(private val dataStore: DataStore<Preferences>) {

    val accessToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN_KEY]
        }
        .catch { _ ->
            // Log the error or handle it as needed
            emit(null)
        }

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN_KEY] = token
        }
    }

    suspend fun clearAllData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

} 