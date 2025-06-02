package com.healthanalytics.android

import androidx.compose.ui.window.ComposeUIViewController
import com.healthanalytics.android.presentation.HealthAnalyticsApp

fun MainViewController() = ComposeUIViewController { HealthAnalyticsApp() }