package com.healthanalytics.android

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.healthanalytics.android.data.repositories.PreferencesRepository
import com.healthanalytics.android.di.DataStoreProvider
import com.healthanalytics.android.di.initKoin
import com.healthanalytics.android.presentation.HealthAnalyticsApp
import org.jetbrains.compose.ui.tooling.preview.Preview

private val koin = initKoin()

@Composable
@Preview
fun App(prefs: DataStore<Preferences>) {
    // Initialize DataStoreProvider only once
    LaunchedEffect(prefs) {
        if (!DataStoreProvider.isInitialized()) {
            DataStoreProvider.initialize(prefs)
        }
    }

    val repository = PreferencesRepository(prefs)
    MaterialTheme {
        HealthAnalyticsApp(repository)
    }
}