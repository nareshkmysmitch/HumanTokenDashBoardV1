package com.healthanalytics.android

import androidx.compose.runtime.Composable
import com.healthanalytics.android.presentation.components.MainScreen

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

data class NavigationItem(
    val screen: MainScreen, val label: String, val icon: Any? = null
)


expect fun getNavigationItems(): List<NavigationItem>

@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)

