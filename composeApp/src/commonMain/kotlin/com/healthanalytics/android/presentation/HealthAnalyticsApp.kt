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
import com.healthanalytics.android.data.models.Product
import com.healthanalytics.android.di.initKoin
import com.healthanalytics.android.presentation.components.BottomNavBar
import com.healthanalytics.android.presentation.components.MainScreen
import com.healthanalytics.android.presentation.components.Screen
import com.healthanalytics.android.presentation.components.TopAppBar
import com.healthanalytics.android.presentation.health.HealthDataScreen
import com.healthanalytics.android.presentation.screens.BiomarkersScreen
import com.healthanalytics.android.presentation.screens.ProfileScreen
import com.healthanalytics.android.presentation.screens.chat.ChatScreen
import com.healthanalytics.android.presentation.screens.chat.ConversationListScreen
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceScreen

//private val koin = initKoin()

@Composable
fun HealthAnalyticsApp() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var lastMainScreen by remember { mutableStateOf(Screen.HOME) }
    var accessToken by remember { mutableStateOf<String?>(null) }
    var conversationId by remember { mutableStateOf("") }

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
//        LoginScreen(
//            onLoginSuccess = { token ->
//                accessToken = token
//            })
    } else {
        when (currentScreen) {
            Screen.PROFILE -> ProfileScreen(onNavigateBack = { navigateBack() })
            Screen.CHAT -> {
                ChatScreen(conversationId, onNavigateBack = { })
            }

            Screen.HOME -> HomeScreen(accessToken, onProfileClick = {
                navigateTo(Screen.PROFILE)
            }, onChatClick = {
                conversationId = it
                navigateTo(Screen.CHAT)
            }, onMarketPlaceClick = {
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
    onChatClick: (String) -> Unit,
    onMarketPlaceClick: (Product) -> Unit,
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
            title = "Human Token", onProfileClick = onProfileClick, onChatClick = {
                onChatClick("123")
            }
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
                MainScreen.DASHBOARD -> HealthDataScreen()
                MainScreen.BIOMARKERS -> BiomarkersScreen(token = accessToken.toString())
                MainScreen.RECOMMENDATIONS -> ConversationListScreen(onNavigateToChat = { conversationId ->
                    onChatClick(conversationId)
                })

                MainScreen.MARKETPLACE -> MarketPlaceScreen()
            }
        }
    }
}