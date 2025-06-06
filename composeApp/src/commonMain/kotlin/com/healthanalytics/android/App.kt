package com.healthanalytics.android

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.healthanalytics.android.presentation.HealthAnalyticsApp
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    MaterialTheme {
        HealthAnalyticsApp()
    }
}