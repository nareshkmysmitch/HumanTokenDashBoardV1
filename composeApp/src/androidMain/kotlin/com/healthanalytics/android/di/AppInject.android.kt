package com.healthanalytics.android.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.healthanalytics.android.localstorage.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModules = module {
    // Create and provide DataStore
    single<DataStore<Preferences>> {
        createDataStore(androidContext())
    }
}