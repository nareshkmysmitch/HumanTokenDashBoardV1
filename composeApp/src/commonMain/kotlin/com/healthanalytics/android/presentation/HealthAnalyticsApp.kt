package com.healthanalytics.android.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.healthanalytics.android.presentation.components.BottomNavBar
import com.healthanalytics.android.presentation.components.Screen
import com.healthanalytics.android.presentation.components.TopAppBar
import com.healthanalytics.android.presentation.screens.BiomarkersScreen
import com.healthanalytics.android.presentation.screens.DashboardScreen
import com.healthanalytics.android.presentation.screens.LoginScreen
import com.healthanalytics.android.presentation.screens.MarketplaceScreen
import com.healthanalytics.android.presentation.screens.ProfileScreen
import com.healthanalytics.android.presentation.screens.RecommendationsScreen
import com.healthanalytics.android.presentation.theme.HealthAnalyticsTheme

@Composable
fun HealthAnalyticsApp() {
    HealthAnalyticsTheme {
        var accessToken by remember { mutableStateOf<String?>(null) }
        var navigationStack by remember { mutableStateOf(listOf(Screen.DASHBOARD)) }
        val currentScreen = navigationStack.last()
        
        fun navigateTo(screen: Screen) {
            navigationStack = navigationStack + screen
        }
        
        fun navigateBack() {
            if (navigationStack.size > 1) {
                navigationStack = navigationStack.dropLast(1)
            }
        }

        accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiQkVUQV8wMzcyNGE3Yi0wZjA5LTQ1ODYtYmYyMy1hYTQ1NzA5NzVhYjciLCJzZXNzaW9uX2lkIjoiOGM0MmFlMzAtZmVkMC00NTNjLWIwMzEtYmQyYmFjNzQ5N2Y0IiwidXNlcl9pbnRfaWQiOiI0NzUiLCJpYXQiOjE3NDg0OTkwODgsImV4cCI6MTc0OTEwMzg4OH0.jbbY5r1g-SSzYvII3EkcfzFfdDF2OHZwifx9DFuH20E"

        if (accessToken == null) {
            LoginScreen(
                onLoginSuccess = { token ->
                    accessToken = token
                })
        } else {
            Scaffold(topBar = {
                TopAppBar(
                    title = when (currentScreen) {
                        Screen.DASHBOARD -> "Health Analytics"
                        Screen.BIOMARKERS -> "BioMarkers"
                        Screen.RECOMMENDATIONS -> "Recommendations"
                        Screen.MARKETPLACE -> "Market Place"
                    }, onProfileClick = {
                        navigateTo(Screen.PROFILE)
                    }, onChatClick = {
                        // Handle chat click
                    })
            }, bottomBar = {
                BottomNavBar(
                    currentScreen = currentScreen, onScreenSelected = { screen ->
                        navigateTo(screen)
                    })
            }) { paddingValues ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                ) {
                    when (currentScreen) {
                        Screen.DASHBOARD -> DashboardScreen(token = accessToken.toString())
                        Screen.BIOMARKERS -> BiomarkersScreen(token = accessToken.toString())
                        Screen.RECOMMENDATIONS -> RecommendationsScreen()
                        Screen.MARKETPLACE -> MarketplaceScreen(token = accessToken.toString())
                        Screen.PROFILE -> ProfileScreen(onNavigateBack = { navigateBack() })
                    }
                }
            }
        }
    }
}