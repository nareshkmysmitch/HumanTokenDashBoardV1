package com.healthanalytics.android.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.healthanalytics.android.presentation.components.BottomNavigationBar
import com.healthanalytics.android.presentation.components.Screen
import com.healthanalytics.android.presentation.components.TopAppBar
import com.healthanalytics.android.presentation.screens.BiomarkersScreen
import com.healthanalytics.android.presentation.screens.DashboardScreen
import com.healthanalytics.android.presentation.screens.LoginScreen
import com.healthanalytics.android.presentation.screens.MarketplaceScreen
import com.healthanalytics.android.presentation.screens.RecommendationsScreen
import com.healthanalytics.android.presentation.theme.HealthAnalyticsTheme

@Composable
fun HealthAnalyticsApp() {
    HealthAnalyticsTheme {
        var accessToken by remember { mutableStateOf<String?>(null) }
        var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }

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
                        // Handle profile click
                    }, onChatClick = {
                        // Handle chat click
                    })
            }, bottomBar = {
                BottomNavigationBar(
                    currentScreen = currentScreen, onScreenSelected = { screen ->
                        currentScreen = screen
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
                    }
                }
            }
        }
    }
}