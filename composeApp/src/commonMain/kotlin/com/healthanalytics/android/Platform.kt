package com.healthanalytics.android

import androidx.compose.ui.graphics.vector.ImageVector
import com.healthanalytics.android.presentation.components.Screen

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

data class NavigationItem(
    val screen: Screen, val label: String, val icon: ImageVector? = null
)


expect fun getNavigationItems(): List<NavigationItem>