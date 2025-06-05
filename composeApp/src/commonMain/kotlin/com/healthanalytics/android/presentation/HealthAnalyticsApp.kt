package com.healthanalytics.android.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.healthanalytics.android.presentation.components.BottomNavBar
import com.healthanalytics.android.presentation.components.MainScreen
import com.healthanalytics.android.presentation.components.TopAppBar
import com.healthanalytics.android.presentation.screens.BiomarkersScreen
import com.healthanalytics.android.presentation.screens.DashboardScreen
import com.healthanalytics.android.presentation.screens.MarketplaceScreen
import com.healthanalytics.android.presentation.screens.RecommendationsScreen
import kotlinx.serialization.Serializable
import androidx.navigation.compose.composable
import com.healthanalytics.android.presentation.screens.onboard.CreateAccountContainer
import com.healthanalytics.android.presentation.screens.onboard.CreateAccountScreen
import com.healthanalytics.android.presentation.screens.onboard.HealthProfileContainer
import com.healthanalytics.android.presentation.screens.onboard.HealthProfileScreen
import com.healthanalytics.android.presentation.screens.onboard.LoginScreen
import com.healthanalytics.android.presentation.screens.onboard.OTPContainer
import com.healthanalytics.android.presentation.screens.onboard.OTPScreen
import com.healthanalytics.android.presentation.screens.onboard.OnboardViewModel
import com.healthanalytics.android.presentation.screens.onboard.PaymentScreen
import com.healthanalytics.android.presentation.screens.onboard.SampleCollectionAddressContainer
import com.healthanalytics.android.presentation.screens.onboard.SampleCollectionAddressScreen
import com.healthanalytics.android.presentation.screens.onboard.ScheduleBloodTestScreen
import org.koin.compose.viewmodel.koinViewModel


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

    OnboardContainer()
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
fun OnboardContainer() {
    val navController = rememberNavController()
    val onboardViewModel: OnboardViewModel = koinViewModel()

    NavHost(
        navController = navController,
        startDestination = OnboardRoute.Login
    ) {
        composable<OnboardRoute.Login> {
            LoginScreen(
                loginState = onboardViewModel.loginState,
                onContinueClick = {
                    onboardViewModel.sendOTP(it)
                },
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
            SampleCollectionAddressScreen(
                onBackClick = {

                }
            )

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
            ScheduleBloodTestScreen(
                onBackClick = {

                }
            )
        }

        composable<OnboardRoute.Payment> {
            PaymentScreen(
                onBackClick = {

                }
            )
        }
    }
}

@Composable
fun HomeScreen(accessToken: String?, onProfileClick: () -> Unit, onChatClick: () -> Unit) {

//    var currentScreen by remember { mutableStateOf(MainScreen.DASHBOARD) }
//
//    fun navigateTo(screen: MainScreen) {
//        currentScreen = screen
//    }
//
//    fun navigateBack() {
//        currentScreen = MainScreen.DASHBOARD
//    }
//
//    Scaffold(topBar = {
//        TopAppBar(
//            title = when (currentScreen) {
//                MainScreen.DASHBOARD -> "Health Analytics"
//                MainScreen.BIOMARKERS -> "BioMarkers"
//                MainScreen.RECOMMENDATIONS -> "Recommendations"
//                MainScreen.MARKETPLACE -> "Market Place"
//                else -> ""
//            }, onProfileClick = onProfileClick, onChatClick = onChatClick
//        )
//    }, bottomBar = {
//        BottomNavBar(
//            currentScreen = currentScreen, onScreenSelected = { screen ->
//                navigateTo(screen)
//            })
//    }) { paddingValues ->
//        Box(
//            modifier = Modifier.fillMaxSize().padding(paddingValues)
//        ) {
//            when (currentScreen) {
//                MainScreen.DASHBOARD -> DashboardScreen(token = accessToken.toString())
//                MainScreen.BIOMARKERS -> BiomarkersScreen(token = accessToken.toString())
//                MainScreen.RECOMMENDATIONS -> RecommendationsScreen()
//                MainScreen.MARKETPLACE -> MarketplaceScreen(token = accessToken.toString())
//            }
//        }
//    }
}