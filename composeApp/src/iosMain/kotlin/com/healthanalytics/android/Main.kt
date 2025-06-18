package com.healthanalytics.android

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.healthanalytics.android.di.initKoin
import com.healthanalytics.android.presentation.navigation.DefaultRootComponent
import com.healthanalytics.android.presentation.navigation.RootContent
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initKoin()

    val lifecycle = LifecycleRegistry()
    val rootComponent = DefaultRootComponent(
        componentContext = DefaultComponentContext(lifecycle = lifecycle)
    )

    return ComposeUIViewController {
        MaterialTheme {
            RootContent(component = rootComponent)
        }
    }
} 