package com.healthanalytics.android.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * Provider for DataStore instance that can be accessed throughout the app.
 * Must be initialized before use.
 */
object DataStoreProvider {
    private var _dataStore: DataStore<Preferences>? = null
    private var isInitialized = false

    /**
     * Initialize the DataStore instance.
     * @param dataStore The DataStore instance to be used throughout the app
     * @throws IllegalStateException if already initialized
     */
    fun initialize(dataStore: DataStore<Preferences>) {
        if (isInitialized) {
            throw IllegalStateException("DataStoreProvider already initialized")
        }
        _dataStore = dataStore
        isInitialized = true
    }

    /**
     * Get the DataStore instance.
     * @return The initialized DataStore instance
     * @throws IllegalStateException if not initialized
     */
    fun getDataStore(): DataStore<Preferences> {
        return _dataStore ?: throw IllegalStateException("DataStore not initialized. Call initialize() first.")
    }

    /**
     * Check if DataStoreProvider is initialized.
     * @return true if initialized, false otherwise
     */
    fun isInitialized(): Boolean = isInitialized
} 