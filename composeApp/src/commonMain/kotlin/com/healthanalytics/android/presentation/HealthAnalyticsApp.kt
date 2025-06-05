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
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.humantoken.ui.screens.ProductDetailScreen
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.presentation.components.BottomNavBar
import com.healthanalytics.android.presentation.components.MainScreen
import com.healthanalytics.android.presentation.components.TopAppBar
import com.healthanalytics.android.presentation.health.HealthDataScreen
import com.healthanalytics.android.presentation.screens.BiomarkersScreen
import com.healthanalytics.android.presentation.screens.DashboardScreen
import com.healthanalytics.android.presentation.screens.MarketplaceScreen
import com.healthanalytics.android.presentation.screens.LoginScreen
import com.healthanalytics.android.presentation.screens.ProfileScreen
import com.healthanalytics.android.presentation.screens.RecommendationsScreen
import com.healthanalytics.android.presentation.screens.onboard.CreateAccountContainer
import com.healthanalytics.android.presentation.screens.onboard.HealthProfileContainer
import com.healthanalytics.android.presentation.screens.onboard.LoginScreenContainer
import com.healthanalytics.android.presentation.screens.onboard.OTPContainer
import com.healthanalytics.android.presentation.screens.onboard.PaymentScreen
import com.healthanalytics.android.presentation.screens.onboard.SampleCollectionAddressContainer
import com.healthanalytics.android.presentation.screens.onboard.ScheduleBloodTestContainer
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceScreen


@Composable
fun HealthAnalyticsApp() {
//    var currentScreen by remember { mutableStateOf(MainScreen.DASHBOARD) }
//    var lastMainScreen by remember { mutableStateOf(MainScreen.DASHBOARD) }
//    var accessToken by remember { mutableStateOf<String?>(null) }
//

//    fun navigateTo(screen: Screen) {
//        // Remember the last main screen when navigating away from main screens
//        if (currentScreen in listOf(MainScreen.DASHBOARD, MainScreen.BIOMARKERS, MainScreen.RECOMMENDATIONS, MainScreen.MARKETPLACE)) {
//            lastMainScreen = currentScreen
//        }
////        currentScreen = screen
//    }
//
//    fun navigateBack() {
//        // Navigate back to the last main screen instead of always going to dashboard
//        currentScreen = lastMainScreen
//    }
//
//    accessToken =
//        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiQkVUQV8wMzcyNGE3Yi0wZjA5LTQ1ODYtYmYyMy1hYTQ1NzA5NzVhYjciLCJzZXNzaW9uX2lkIjoiOGM0MmFlMzAtZmVkMC00NTNjLWIwMzEtYmQyYmFjNzQ5N2Y0IiwidXNlcl9pbnRfaWQiOiI0NzUiLCJpYXQiOjE3NDg0OTkwODgsImV4cCI6MTc0OTEwMzg4OH0.jbbY5r1g-SSzYvII3EkcfzFfdDF2OHZwifx9DFuH20E"
//
//    if (accessToken == null) {
//        LoginScreen(
//            onLoginSuccess = { token ->
//                accessToken = token
//            })
//    } else {
//        when (currentScreen) {
//            Screen.PROFILE -> ProfileScreen(onNavigateBack = { navigateBack() })
//            Screen.CHAT -> ProfileScreen(onNavigateBack = { navigateBack() })
//            Screen.HOME -> HomeScreen(accessToken, onProfileClick = {
//                navigateTo(Screen.PROFILE)
//            }, onChatClick = {
//                navigateTo(Screen.CHAT)
//            })
//        }
//    }

    var isLogin by remember { mutableStateOf(false) }

    if (isLogin){
        HomeScreen(
            accessToken = "",
        )
    }else{
        OnboardContainer(
            isLoggedIn = {
                isLogin = true
            }
        )
    }

}

sealed class OnboardRoute {

    @Serializable
    data object Login : OnboardRoute()

    @Serializable
    data object OTPVerification : OnboardRoute()

    @Serializable
    data object CreateAccount : OnboardRoute()

    @Serializable
    data object HealthProfile : OnboardRoute()

    @Serializable
    data object SampleCollectionAddress : OnboardRoute()

    @Serializable
    data object ScheduleBloodTest : OnboardRoute()

    @Serializable
    data object Payment : OnboardRoute()
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
fun OnboardContainer(isLoggedIn:() -> Unit) {
    val navController = rememberNavController()
//    val onboardViewModel: OnboardViewModel = koinViewModel()

    NavHost(
        navController = navController,
        startDestination = OnboardRoute.Login
    ) {
        composable<OnboardRoute.Login> {
            LoginScreenContainer(
                navigateToOtpVerification = {
                    navController.navigate(OnboardRoute.OTPVerification)
                }
            )
        }

        composable<OnboardRoute.OTPVerification> {
            OTPContainer(
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

@Composable
fun HomeScreen(accessToken: String?, onProfileClick: () -> Unit={}, onChatClick: () -> Unit={}) {
fun HomeScreen(
    accessToken: String?,
    onProfileClick: () -> Unit,
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
            title = "Human Token", onProfileClick = onProfileClick, onChatClick = onChatClick
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

                MainScreen.MARKETPLACE -> MarketPlaceScreen()

            }
        }
    }
}