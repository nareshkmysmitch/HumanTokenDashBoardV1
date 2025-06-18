package com.healthanalytics.android

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.healthanalytics.android.presentation.navigation.DefaultRootComponent
import com.healthanalytics.android.presentation.navigation.RootContent
import com.healthanalytics.android.presentation.theme.AppTheme

@Composable
fun App() {
    val lifecycle = LifecycleRegistry()
    val rootComponent = DefaultRootComponent(
        componentContext = DefaultComponentContext(lifecycle = lifecycle)
    )
    
    AppTheme {
        RootContent(component = rootComponent)
    }
}