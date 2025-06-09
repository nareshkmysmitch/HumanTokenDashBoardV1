package com.healthanalytics.android

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.ComposeUIViewController
//import com.arkivanov.decompose.DefaultComponentContext
//import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.healthanalytics.android.di.initKoin
//import com.healthanalytics.android.navigation.RootComponent
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initKoin()
    
//    val lifecycle = LifecycleRegistry()
//    val root = RootComponent(
//        componentContext = DefaultComponentContext(lifecycle = lifecycle)
//    )
    
    return ComposeUIViewController {
        MaterialTheme {
            App()
        }
    }
} 