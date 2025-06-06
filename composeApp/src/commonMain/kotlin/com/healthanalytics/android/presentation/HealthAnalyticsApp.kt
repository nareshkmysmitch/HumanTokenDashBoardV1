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
import com.example.humantoken.ui.screens.CartScreen
import com.example.humantoken.ui.screens.ProductDetailScreen
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.presentation.components.BottomNavBar
import com.healthanalytics.android.presentation.components.MainScreen
import com.healthanalytics.android.presentation.components.Screen
import com.healthanalytics.android.presentation.components.TopAppBar
import com.healthanalytics.android.presentation.health.HealthDataScreen
import com.healthanalytics.android.presentation.recommendations.RecommendationsScreen
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
    var product by remember { mutableStateOf(Product()) }
    //val preferencesViewModel: PreferencesViewModel = koinViewModel()


    fun navigateTo(screen: Screen) {
        // Remember the last main screen when navigating away from main screens
        if (currentScreen in listOf(
                Screen.CONVERSATION_LIST,
                Screen.MARKETPLACE_DETAIL,
                Screen.CHAT,
                Screen.PROFILE,
                Screen.HOME
            )
        ) {
            lastMainScreen = currentScreen
        }
        currentScreen = screen
    }

    fun navigateBack() {
        // Navigate back to the last main screen instead of always going to dashboard
        currentScreen = lastMainScreen
    }

    accessToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMmE5YzRmYTMtY2ZmMi00Mzg3LTlhNTgtMGNjYzg1MmVjMmRiIiwic2Vzc2lvbl9pZCI6IjU3NDlkMjRhLWJhOTktNGI4YS05NDU1LTAyYjllMTcwNGNiMiIsInVzZXJfaW50X2lkIjoiNDM4IiwiaWF0IjoxNzQ5MDE4OTMzLCJleHAiOjE3NDk2MjM3MzN9.N93PEw2D0lfLOBus0XFKxF-bKAyvSw_YSS9k_kfNVls"

    if (accessToken == null) {
//        LoginScreen(
//            onLoginSuccess = { token ->
//                accessToken = token
//            })
    } else {
        when (currentScreen) {
            Screen.PROFILE -> ProfileScreen(onNavigateBack = { navigateBack() })
            Screen.CONVERSATION_LIST -> {
                ConversationListScreen(
                    onNavigateToChat = { id ->
                        conversationId = id
                        navigateTo(Screen.CHAT)
                    },
                    onNavigateBack = { navigateTo(Screen.HOME) }
                )
            }

            Screen.CHAT -> {
                ChatScreen(conversationId, onNavigateBack = { navigateBack() })
            }

            Screen.HOME -> HomeScreen(
                accessToken, onProfileClick = {
                navigateTo(Screen.PROFILE)
            }, onChatClick = {
                navigateTo(Screen.CONVERSATION_LIST)
            }, onMarketPlaceClick = {
                product = it
                println("product -> Ha2$it")
                navigateTo(Screen.MARKETPLACE_DETAIL)
            },
                onCartClick = {
                    navigateTo(Screen.CART)
                }
            )

            Screen.MARKETPLACE_DETAIL -> ProductDetailScreen(product, onNavigateBack = {
                navigateBack()
            })

            Screen.CART -> {
                CartScreen(
                    onBackClick = {
                        navigateBack()
                    },
                    onCheckoutClick = {

                    },
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    accessToken: String?,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit,
    onChatClick: () -> Unit,
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
            title = "Human Token",
            onEndIconClick = if(currentScreen == MainScreen.MARKETPLACE) onCartClick else onProfileClick,
            onChatClick = onChatClick,
            isChatVisible = currentScreen != MainScreen.MARKETPLACE,
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
                MainScreen.RECOMMENDATIONS -> RecommendationsScreen()
                MainScreen.MARKETPLACE -> {
                    MarketPlaceScreen(onProductClick = {
                        onMarketPlaceClick(it)
                        println("product -> Ha1$it")
                    })
                }
            }
        }
    }
}