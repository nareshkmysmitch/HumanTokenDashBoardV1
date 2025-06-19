package com.healthanalytics.android

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.healthanalytics.android.presentation.navigation.MainScreen
import com.healthanalytics.android.presentation.theme.AppTheme
import com.healthanalytics.android.presentation.screens.health.HealthDataViewModel
import org.koin.compose.koinInject

@Composable
fun App() {
    AppTheme {
        val healthDataViewModel: HealthDataViewModel = koinInject()
        Navigator(MainScreen())
    }
}