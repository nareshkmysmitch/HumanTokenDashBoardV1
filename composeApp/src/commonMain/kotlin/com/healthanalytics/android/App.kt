package com.healthanalytics.android

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.healthanalytics.android.di.initKoin
import com.healthanalytics.android.presentation.dashboard.DashboardScreen
import com.healthanalytics.android.presentation.health.HealthDataScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

// Initialize Koin when the app starts
private val koin = initKoin()

@Composable
@Preview
fun App(prefs: DataStore<Preferences>) {
    MaterialTheme {
        HealthDataScreen()
    }
}