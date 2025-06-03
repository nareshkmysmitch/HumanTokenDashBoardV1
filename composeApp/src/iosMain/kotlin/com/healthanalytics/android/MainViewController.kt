package com.healthanalytics.android

import androidx.compose.ui.window.ComposeUIViewController
import com.healthanalytics.android.presentation.HealthAnalyticsApp
import com.healthanalytics.android.presentation.dashboard.DashboardScreen

fun MainViewController() = ComposeUIViewController { HealthAnalyticsApp() }