package com.healthanalytics.android

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.healthanalytics.android.presentation.navigation.MainScreen
import com.healthanalytics.android.presentation.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        Navigator(MainScreen())
    }
}