package com.healthanalytics.android.presentation.navigation

sealed class AppRoutes(val route: String) {
    data object Home : AppRoutes("home")
    data object Profile : AppRoutes("profile")
    data object Chat : AppRoutes("chat")
} 