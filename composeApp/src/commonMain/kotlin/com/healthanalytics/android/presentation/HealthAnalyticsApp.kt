package com.healthanalytics.android.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import com.healthanalytics.android.presentation.screens.onboard.CreateAccountContainer
import com.healthanalytics.android.presentation.screens.onboard.HealthProfileContainer
import com.healthanalytics.android.presentation.screens.onboard.LoginScreenContainer
import com.healthanalytics.android.presentation.screens.onboard.OTPContainer
import com.healthanalytics.android.presentation.screens.onboard.OnboardRoute
import com.healthanalytics.android.presentation.screens.onboard.OnboardViewModel
import com.healthanalytics.android.presentation.screens.onboard.PaymentScreen
import com.healthanalytics.android.presentation.screens.onboard.SampleCollectionAddressContainer
import com.healthanalytics.android.presentation.screens.onboard.ScheduleBloodTestContainer
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HealthAnalyticsApp() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var lastMainScreen by remember { mutableStateOf(Screen.HOME) }
    var accessToken by remember { mutableStateOf<String?>(null) }
    var conversationId by remember { mutableStateOf("") }
    var product by remember { mutableStateOf(Product()) }

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

    val onboardViewModel: OnboardViewModel = koinInject<OnboardViewModel>()
    val onBoardUiState by onboardViewModel.onBoardUiState.collectAsStateWithLifecycle()
    when {
        onBoardUiState.isLoading -> { CircularProgressIndicator() }

        onBoardUiState.hasAccessToken -> {
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
                Screen.MARKETPLACE_DETAIL -> {
                    ProductDetailScreen(product = product, onNavigateBack = { navigateBack() })
                }
                Screen.CART -> {
                    CartScreen(
                        onCheckoutClick = { },
                        onBackClick = { navigateBack() },
                    )
                }
                Screen.HOME -> {
                    HomeScreen(accessToken, onProfileClick = {
                        navigateTo(Screen.PROFILE)
                    }, onChatClick = {
                        navigateTo(Screen.CONVERSATION_LIST)
                    }, onMarketPlaceClick = {
                        product = it
                        navigateTo(Screen.MARKETPLACE_DETAIL)
                    }, onCartClick = {
                        navigateTo(Screen.CART)
                    })
                }
            }
        }

        else -> {
            OnboardContainer(
                onboardViewModel = onboardViewModel,
                isLoggedIn = {
                    onboardViewModel.updateOnBoardState()
                }
            )
        }
    }
}


@Composable
private inline fun <reified T : ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(
        viewModelStoreOwner = parentEntry
    )
}

@Composable
fun OnboardContainer(
    isLoggedIn: () -> Unit,
    onboardViewModel: OnboardViewModel
) {
    org.koin.compose.KoinContext {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = OnboardRoute.Login
        ) {
            composable<OnboardRoute.Login> {
                LoginScreenContainer(
                    onboardViewModel = onboardViewModel,
                    navigateToOtpVerification = {
                        navController.navigate(OnboardRoute.OTPVerification)
                    }
                )
            }

            composable<OnboardRoute.OTPVerification> {
                OTPContainer(
                    onboardViewModel = onboardViewModel,
                    onBackClick = {
                        navController.navigateUp()
                    },
                    navigateToAccountCreation = {
                        navController.navigate(OnboardRoute.CreateAccount)
                    }
                )
            }

            composable<OnboardRoute.CreateAccount> {
                CreateAccountContainer(
                    onboardViewModel = onboardViewModel,
                    onBackClick = {
                        navController.navigateUp()
                    },
                    navigateToHealthProfile = {
                        navController.navigate(OnboardRoute.HealthProfile)
                    }
                )
            }

            composable<OnboardRoute.HealthProfile> {
                HealthProfileContainer(
                    onboardViewModel = onboardViewModel,
                    onBackClick = {
                        navController.navigateUp()
                    },
                    navigateToAddress = {
                        navController.navigate(OnboardRoute.SampleCollectionAddress)
                    }
                )
            }

            composable<OnboardRoute.SampleCollectionAddress> {
                SampleCollectionAddressContainer(
                    onboardViewModel = onboardViewModel,
                    onBackClick = {
                        navController.navigateUp()
                    },
                    navigateToBloodTest = {
                        navController.navigate(OnboardRoute.ScheduleBloodTest)
                    }
                )
            }

            composable<OnboardRoute.ScheduleBloodTest> {
                ScheduleBloodTestContainer(
                    onboardViewModel = onboardViewModel,
                    onBackClick = {
                        navController.navigateUp()
                    },
                    navigateToPayment = {
                        navController.navigate(OnboardRoute.Payment)
                    }
                )
            }

            composable<OnboardRoute.Payment> {
                PaymentScreen(
                    onBackClick = {
                        navController.navigateUp()
                    },
                    onContinueClick = {
                        isLoggedIn()
                    }
                )
            }
        }
    }

}

@Composable
fun HomeScreen(
    accessToken: String?,
    onProfileClick: () -> Unit = {},
    onCartClick: () -> Unit,
    onChatClick: () -> Unit = {},
    onMarketPlaceClick: (Product) -> Unit = {},
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
            onEndIconClick = if (currentScreen == MainScreen.MARKETPLACE) onCartClick else onProfileClick,
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
//                MainScreen.BIOMARKERS -> BiomarkersScreen(token = accessToken.toString())
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