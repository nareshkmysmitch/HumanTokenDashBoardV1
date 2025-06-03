package com.healthanalytics.android

import com.healthanalytics.android.presentation.components.Screen

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

data class NavigationItem(
    val screen: Screen, val label: String, val icon: Any? = null
)


expect fun getNavigationItems(): List<NavigationItem>