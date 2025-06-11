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
import com.healthanalytics.android.data.api.BloodData
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.presentation.components.BottomNavBar
import com.healthanalytics.android.presentation.components.MainScreen
import com.healthanalytics.android.presentation.components.Screen
import com.healthanalytics.android.presentation.components.Screen.*
import com.healthanalytics.android.presentation.components.TopAppBar
import com.healthanalytics.android.presentation.recommendations.RecommendationsScreen
import com.healthanalytics.android.presentation.screens.ProfileScreen
import com.healthanalytics.android.presentation.screens.chat.ChatScreen
import com.healthanalytics.android.presentation.screens.chat.ConversationListScreen
import com.healthanalytics.android.presentation.screens.health.BioMarkerFullReportScreen
import com.healthanalytics.android.presentation.screens.health.BiomarkerDetailScreen
import com.healthanalytics.android.presentation.screens.health.HealthDataScreen
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
    var currentScreen by remember { mutableStateOf<Screen>(Screen.HOME) }
    var lastMainScreen by remember { mutableStateOf<Screen>(Screen.HOME) }
    var biomarker by remember { mutableStateOf(BloodData()) }

    fun navigateTo(screen: Screen) {
        // Remember the last main screen when navigating away from main screens
        if (currentScreen is HOME || currentScreen is Screen.PROFILE || currentScreen is Screen.CONVERSATION_LIST) {
            lastMainScreen = currentScreen
        }
        currentScreen = screen
    }

    fun navigateBack() {
        currentScreen = lastMainScreen
    }

    val onboardViewModel: OnboardViewModel = koinInject<OnboardViewModel>()
    val onBoardUiState by onboardViewModel.onBoardUiState.collectAsStateWithLifecycle()
    when {
        onBoardUiState.isLoading -> CircularProgressIndicator()
        onBoardUiState.hasAccessToken -> {
            when (currentScreen) {
                PROFILE -> ProfileScreen(onNavigateBack = { navigateBack() })
                CONVERSATION_LIST -> {
                    ConversationListScreen(onNavigateToChat = { id ->
                        navigateTo(CHAT(conversationId = id))
                    }, onNavigateBack = { navigateTo(HOME) })
                }

                is CHAT -> {
                    val chatScreen = currentScreen as CHAT
                    ChatScreen(
                        conversationId = chatScreen.conversationId,
                        onNavigateBack = { navigateBack() })
                }

                is MARKETPLACE_DETAIL -> {
                    val marketplaceScreen = currentScreen as MARKETPLACE_DETAIL
                    ProductDetailScreen(
                        product = marketplaceScreen.product, onNavigateBack = { navigateBack() })
                }


                CART -> CartScreen(onCheckoutClick = { }, onBackClick = { navigateBack() })

                BIOMARKERS_DETAIL -> {
                    BiomarkerDetailScreen(
                        onNavigateBack = { navigateBack() },
                        biomarker = biomarker,
                        onNavigateFullReport = navigateBack()
                    )
                }

                BIOMARKER_FULL_REPORT -> {
                    BioMarkerFullReportScreen(
                        onNavigateBack = {
                            navigateBack()
                        }, biomarker = biomarker
                    )
                }

                HOME -> {
                    HomeScreen(onProfileClick = {
                        navigateTo(PROFILE)
                    }, onChatClick = {
                        navigateTo(CONVERSATION_LIST)
                    }, onMarketPlaceClick = { product ->
                        navigateTo(MARKETPLACE_DETAIL(product))
                    }, onCartClick = {
                        navigateTo(CART)
                    }, onBiomarkerFullReportClick = {
                        biomarker = it ?: BloodData()
                        navigateTo(BIOMARKER_FULL_REPORT)
                    }, onBiomarker = {
                        biomarker = it ?: BloodData()
                        navigateTo(BIOMARKERS_DETAIL)
                    })
                }
            }
        }

        else -> {
            OnboardContainer(
                onboardViewModel = onboardViewModel, isLoggedIn = {
                    onboardViewModel.updateOnBoardState()
                })
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
    isLoggedIn: () -> Unit, onboardViewModel: OnboardViewModel
) {
    org.koin.compose.KoinContext {
        val navController = rememberNavController()
        NavHost(
            navController = navController, startDestination = OnboardRoute.Login
        ) {
            composable<OnboardRoute.Login> {
                LoginScreenContainer(
                    onboardViewModel = onboardViewModel, navigateToOtpVerification = {
                        navController.navigate(OnboardRoute.OTPVerification)
                    })
            }

            composable<OnboardRoute.OTPVerification> {
                OTPContainer(onboardViewModel = onboardViewModel, onBackClick = {
                    navController.navigateUp()
                }, navigateToAccountCreation = {
                    navController.navigate(OnboardRoute.CreateAccount)
                })
            }

            composable<OnboardRoute.CreateAccount> {
                CreateAccountContainer(onboardViewModel = onboardViewModel, onBackClick = {
                    navController.navigateUp()
                }, navigateToHealthProfile = {
                    navController.navigate(OnboardRoute.HealthProfile)
                })
            }

            composable<OnboardRoute.HealthProfile> {
                HealthProfileContainer(onboardViewModel = onboardViewModel, onBackClick = {
                    navController.navigateUp()
                }, navigateToAddress = {
                    navController.navigate(OnboardRoute.SampleCollectionAddress)
                })
            }

            composable<OnboardRoute.SampleCollectionAddress> {
                SampleCollectionAddressContainer(
                    onboardViewModel = onboardViewModel,
                    onBackClick = {
                        navController.navigateUp()
                    },
                    navigateToBloodTest = {
                        navController.navigate(OnboardRoute.ScheduleBloodTest)
                    })
            }

            composable<OnboardRoute.ScheduleBloodTest> {
                ScheduleBloodTestContainer(onboardViewModel = onboardViewModel, onBackClick = {
                    navController.navigateUp()
                }, navigateToPayment = {
                    navController.navigate(OnboardRoute.Payment)
                })
            }

            composable<OnboardRoute.Payment> {
                PaymentScreen(onBackClick = {
                    navController.navigateUp()
                }, onContinueClick = {
                    isLoggedIn()
                })
            }
        }
    }
}

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit = {},
    onCartClick: () -> Unit,
    onChatClick: () -> Unit = {},
    onBiomarker: (BloodData?) -> Unit = {},
    onMarketPlaceClick: (Product) -> Unit = {},
    onBiomarkerFullReportClick: (BloodData?) -> Unit = {},
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
                MainScreen.DASHBOARD -> HealthDataScreen(onNavigateToDetail = { onBiomarker(it) })
                MainScreen.RECOMMENDATIONS -> RecommendationsScreen(navigateBack = { navigateBack() })
                MainScreen.MARKETPLACE -> {
                    MarketPlaceScreen(onProductClick = {
                        onMarketPlaceClick(it)
                        println("product -> Ha1$it")
                    }, navigateBack = {
                        navigateBack()
                    })
                }
            }
        }
    }
}