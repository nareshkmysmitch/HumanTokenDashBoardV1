package com.healthanalytics.android.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.humantoken.ui.screens.ProductDetailScreen
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.presentation.components.BottomNavBar
import com.healthanalytics.android.presentation.components.MainScreen
import com.healthanalytics.android.presentation.components.Screen
import com.healthanalytics.android.presentation.components.TopAppBar
import com.healthanalytics.android.presentation.screens.BiomarkersScreen
import com.healthanalytics.android.presentation.screens.DashboardScreen
import com.healthanalytics.android.presentation.screens.LoginScreen
import com.healthanalytics.android.presentation.screens.MarketplaceScreen
import com.healthanalytics.android.presentation.screens.ProfileScreen
import com.healthanalytics.android.presentation.screens.RecommendationsScreen

@Composable
fun HealthAnalyticsApp() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var lastMainScreen by remember { mutableStateOf(Screen.HOME) }
    var accessToken by remember { mutableStateOf<String?>(null) }

    fun navigateTo(screen: Screen) {
        // Remember the last main screen when navigating away from main screens
        if (currentScreen in listOf(Screen.CHAT, Screen.PROFILE, Screen.HOME)) {
            lastMainScreen = currentScreen
        }
        currentScreen = screen
    }

    fun navigateBack() {
        // Navigate back to the last main screen instead of always going to dashboard
        currentScreen = lastMainScreen
    }

    accessToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiQkVUQV8wMzcyNGE3Yi0wZjA5LTQ1ODYtYmYyMy1hYTQ1NzA5NzVhYjciLCJzZXNzaW9uX2lkIjoiOGM0MmFlMzAtZmVkMC00NTNjLWIwMzEtYmQyYmFjNzQ5N2Y0IiwidXNlcl9pbnRfaWQiOiI0NzUiLCJpYXQiOjE3NDg0OTkwODgsImV4cCI6MTc0OTEwMzg4OH0.jbbY5r1g-SSzYvII3EkcfzFfdDF2OHZwifx9DFuH20E"

    if (accessToken == null) {
        LoginScreen(
            onLoginSuccess = { token ->
                accessToken = token
            })
    } else {
        when (currentScreen) {
            Screen.PROFILE -> ProfileScreen(onNavigateBack = { navigateBack() })
            Screen.CHAT -> ProfileScreen(onNavigateBack = { navigateBack() })
            Screen.HOME -> HomeScreen(
                accessToken, onProfileClick = {
                navigateTo(Screen.PROFILE)
            }, onChatClick = {
                navigateTo(Screen.CHAT)
            },
                onMarketPlaceClick = {
                    navigateTo(Screen.MARKETPLACE_DETAIL)
                })

            Screen.MARKETPLACE_DETAIL -> ProductDetailScreen()
        }
    }
}


@Composable
fun HomeScreen(
    accessToken: String?,
    onProfileClick: () -> Unit,
    onChatClick: () -> Unit,
    onMarketPlaceClick: (Product) -> Unit
) {

    var currentScreen by remember { mutableStateOf(MainScreen.DASHBOARD) }

    fun navigateTo(screen: MainScreen) {
        currentScreen = screen
    }

    fun navigateBack() {
        currentScreen = MainScreen.DASHBOARD
    }

    Scaffold(topBar = {
        TopAppBar(
            title = when (currentScreen) {
                MainScreen.DASHBOARD -> "Health Analytics"
                MainScreen.BIOMARKERS -> "BioMarkers"
                MainScreen.RECOMMENDATIONS -> "Recommendations"
                MainScreen.MARKETPLACE -> "Market Place"
                else -> ""
            }, onProfileClick = onProfileClick, onChatClick = onChatClick
        )
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
                MainScreen.DASHBOARD -> DashboardScreen(token = accessToken.toString())
                MainScreen.BIOMARKERS -> BiomarkersScreen(token = accessToken.toString())
                MainScreen.RECOMMENDATIONS -> RecommendationsScreen()
                MainScreen.MARKETPLACE -> MarketplaceScreen(
                    token = accessToken.toString(),
                    onProductClick = {
                        onMarketPlaceClick
                    }
                )
            }
        }
    }
}