package com.healthanalytics.android

import androidx.compose.ui.window.ComposeUIViewController
import com.healthanalytics.android.di.initKoin
import com.healthanalytics.android.presentation.HealthAnalyticsApp
import com.healthanalytics.android.presentation.screens.onboard.HealthProfileScreen

fun MainViewController() = ComposeUIViewController(
    configure = {
//        initKoin()
    }
) { HealthAnalyticsApp() }