package com.healthanalytics.android

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.healthanalytics.android.di.initKoin
import org.jetbrains.compose.ui.tooling.preview.Preview

// Initialize Koin when the app starts
private val koin = initKoin()

@Composable
@Preview
fun App() {
    MaterialTheme {
//        MarketPlaceScreen()
    }
}