package com.healthanalytics.android.localstorage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

internal const val DATA_STORE_FILE_NAME="user_preferences.preferences_pb"

fun createDataStore(pathProvider: () -> String): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(produceFile = {
        pathProvider().toPath()
    })
}