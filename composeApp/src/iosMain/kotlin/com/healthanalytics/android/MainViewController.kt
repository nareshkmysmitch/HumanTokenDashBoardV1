package com.healthanalytics.android

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import createDataStore

fun MainViewController() = ComposeUIViewController {
    App(
        remember { createDataStore() }
    )
}