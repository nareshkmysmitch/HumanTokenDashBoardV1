
package com.healthanalytics.android.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.healthanalytics.android.presentation.components.BottomNavBar
import com.healthanalytics.android.presentation.components.TopAppBar
import com.healthanalytics.android.presentation.navigation.AppNavigation
import com.healthanalytics.android.presentation.navigation.Screen
import com.healthanalytics.android.presentation.theme.HealthAnalyticsTheme

@Composable
fun HealthAnalyticsApp() {
    HealthAnalyticsTheme {
        var accessToken by remember { mutableStateOf<String?>(null) }
        val navController = rememberNavController()

        // Hardcoded token for testing - remove in production
        accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiQkVUQV8wMzcyNGE3Yi0wZjA5LTQ1ODYtYmYyMy1hYTQ1NzA5NzVhYjciLCJzZXNzaW9uX2lkIjoiOGM0MmFlMzAtZmVkMC00NTNjLWIwMzEtYmQyYmFjNzQ5N2Y0IiwidXNlcl9pbnRfaWQiOiI0NzUiLCJpYXQiOjE3NDg0OTkwODgsImV4cCI6MTc0OTEwMzg4OH0.jbbY5r1g-SSzYvII3EkcfzFfdDF2OHZwifx9DFuH20E"

        if (accessToken == null) {
            AppNavigation(
                navController = navController,
                startDestination = Screen.Login.route,
                accessToken = accessToken
            )
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = "Health Analytics",
                        onProfileClick = {
                            // Handle profile click
                        },
                        onChatClick = {
                            // Handle chat click
                        }
                    )
                },
                bottomBar = {
                    BottomNavBar(navController = navController)
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                ) {
                    AppNavigation(
                        navController = navController,
                        startDestination = Screen.Dashboard.route,
                        accessToken = accessToken
                    )
                }
            }
        }
    }
}
