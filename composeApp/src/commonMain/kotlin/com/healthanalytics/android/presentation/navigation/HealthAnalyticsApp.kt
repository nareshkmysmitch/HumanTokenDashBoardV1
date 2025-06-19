package com.healthanalytics.android.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.humantoken.ui.screens.CartScreen
import com.example.humantoken.ui.screens.ProductDetailScreen
import com.healthanalytics.android.data.api.BloodData
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.payment.RazorpayHandler
import com.healthanalytics.android.presentation.components.BottomNavBar
import com.healthanalytics.android.presentation.components.MainScreen
import com.healthanalytics.android.presentation.components.Screen
import com.healthanalytics.android.presentation.components.Screen.BIOMARKERS_DETAIL
import com.healthanalytics.android.presentation.components.Screen.BIOMARKER_FULL_REPORT
import com.healthanalytics.android.presentation.components.Screen.CART
import com.healthanalytics.android.presentation.components.Screen.CHAT
import com.healthanalytics.android.presentation.components.Screen.CONVERSATION_LIST
import com.healthanalytics.android.presentation.components.Screen.HOME
import com.healthanalytics.android.presentation.components.Screen.MARKETPLACE_DETAIL
import com.healthanalytics.android.presentation.components.Screen.PROFILE
import com.healthanalytics.android.presentation.components.Screen.SCHEDULE_TEST_BOOKING
import com.healthanalytics.android.presentation.components.Screen.TEST_BOOKING
import com.healthanalytics.android.presentation.components.TopAppBar
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.screens.profile.ProfileScreen
import com.healthanalytics.android.presentation.screens.chat.ChatScreen
import com.healthanalytics.android.presentation.screens.chat.ChatViewModel
import com.healthanalytics.android.presentation.screens.chat.ConversationListScreen
import com.healthanalytics.android.presentation.screens.health.BioMarkerFullReportScreen
import com.healthanalytics.android.presentation.screens.health.BiomarkerDetailScreen
import com.healthanalytics.android.presentation.screens.health.HealthDataScreen
import com.healthanalytics.android.presentation.screens.health.HealthDataViewModel
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceScreen
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.screens.onboard.CreateAccountContainer
import com.healthanalytics.android.presentation.screens.onboard.GetStartedScreen
import com.healthanalytics.android.presentation.screens.onboard.LoginScreenContainer
import com.healthanalytics.android.presentation.screens.onboard.OTPContainer
import com.healthanalytics.android.presentation.screens.onboard.OnboardRoute
import com.healthanalytics.android.presentation.screens.onboard.PaymentScreenContainer
import com.healthanalytics.android.presentation.screens.onboard.SampleCollectionAddressContainer
import com.healthanalytics.android.presentation.screens.onboard.ScheduleBloodTestContainer
import com.healthanalytics.android.presentation.screens.onboard.viewmodel.OnboardViewModel
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationsTabScreen
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationsViewModel
import com.healthanalytics.android.presentation.screens.symptoms.SymptomsScreen
import com.healthanalytics.android.presentation.screens.symptoms.SymptomsViewModel
import com.healthanalytics.android.presentation.screens.testbooking.ScheduleTestBookingScreen
import com.healthanalytics.android.presentation.screens.testbooking.TestBookingScreen
import com.healthanalytics.android.presentation.screens.testbooking.TestBookingViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppTheme
import com.healthanalytics.android.utils.CsvUtils
import com.healthanalytics.android.utils.saveTextFile
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext
import org.koin.compose.getKoin
import org.koin.compose.koinInject

@Composable
fun HealthAnalyticsApp() {

    AppTheme {

        var currentScreen by remember { mutableStateOf<Screen>(Screen.HOME) }
        var lastMainScreen by remember { mutableStateOf<Screen>(Screen.HOME) }
        var biomarker by remember { mutableStateOf(BloodData()) }

        fun navigateTo(screen: Screen) {
            // Remember the last main screen when navigating away from main screens
            if (currentScreen is HOME || currentScreen is PROFILE || currentScreen is CONVERSATION_LIST) {
                lastMainScreen = currentScreen
            }
            currentScreen = screen
        }

        fun navigateBack() {
            currentScreen = if (lastMainScreen == currentScreen) {
                HOME
            } else {
                lastMainScreen
            }
        }


        val healthDataViewModel: HealthDataViewModel = koinInject<HealthDataViewModel>()
        val preferencesViewModel: PreferencesViewModel = koinInject<PreferencesViewModel>()
        val marketPlaceViewModel: MarketPlaceViewModel = koinInject<MarketPlaceViewModel>()
        val testBookingViewModel: TestBookingViewModel = koinInject<TestBookingViewModel>()
        val chatViewModel: ChatViewModel = koinInject<ChatViewModel>()
        val symptomsViewModel: SymptomsViewModel = koinInject<SymptomsViewModel>()
        val recommendationsViewModel: RecommendationsViewModel =
            koinInject<RecommendationsViewModel>()

        var localTestList by remember { mutableStateOf<List<Product>>(emptyList()) }


        val onboardViewModel: OnboardViewModel = koinInject<OnboardViewModel>()
        val onBoardUiState by onboardViewModel.onBoardUiState.collectAsStateWithLifecycle()
        when {
            onBoardUiState.isLoading -> CircularProgressIndicator()
            onBoardUiState.hasAccessToken -> {
                // Navigate to the main screen
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
}

@Composable
fun OnboardContainer(
    isLoggedIn: () -> Unit, onboardViewModel: OnboardViewModel,
) {
    KoinContext {
        val navController = rememberNavController()
        val razorpayHandler: RazorpayHandler = getKoin().get()

        Scaffold(
            containerColor = AppColors.backgroundDark
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = OnboardRoute.GetStarted,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<OnboardRoute.GetStarted> {
                    GetStartedScreen(
                        onGetStarted = { navController.navigate(OnboardRoute.Login) },
                        onLogin = { navController.navigate(OnboardRoute.Login) },
                        onViewAllBiomarkers = {}
                    )
                }
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

                        }, navigateToAccountCreation = {
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
                    PaymentScreenContainer(
                        onboardViewModel = onboardViewModel,
                        razorpayHandler = razorpayHandler,
                        onBackClick = {
                            navController.navigateUp()
                        },
                        isPaymentCompleted = {
                            isLoggedIn()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    healthDataViewModel: HealthDataViewModel,
    preferenceViewModel: PreferencesViewModel,
    marketPlaceViewModel: MarketPlaceViewModel,
    onProfileClick: () -> Unit = {},
    onSymptomsClick: () -> Unit = {},
    onCartClick: () -> Unit,
    onChatClick: () -> Unit = {},
    onBiomarker: (BloodData?) -> Unit = {},
    onMarketPlaceClick: (Product) -> Unit = {},
    onBiomarkerFullReportClick: (BloodData?) -> Unit = {},
    recommendationsViewModel: RecommendationsViewModel,
) {

    val uiState by healthDataViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var isCsvStarted by remember { mutableStateOf(false) }
    val metrics: List<BloodData?> = uiState.metrics
    val isLoading: Boolean = uiState.isLoading
    val error: String? = uiState.error
    val selectedFilter: String? = uiState.selectedFilter
    val searchQuery: String = uiState.searchQuery
    val lastUpdated: BloodData? = uiState.lastUpdated
    println("uiState --> $isLoading, $error, $selectedFilter, $searchQuery, ${lastUpdated}, ${metrics.size}")

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
            actions = {
                when (currentScreen) {
                    MainScreen.DASHBOARD -> {
                        IconButton(onClick = onSymptomsClick) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "symptoms",
                                modifier = Modifier.size(24.dp),
                                tint = AppColors.White
                            )
                        }
                        IconButton(onClick = onChatClick) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Chat",
                                modifier = Modifier.size(24.dp),
                                tint = AppColors.White
                            )
                        }
                        IconButton(onClick = { isCsvStarted = true }) {
                            Icon(
                                imageVector = Icons.Default.ImportExport,
                                contentDescription = "Export CSV",
                                tint = AppColors.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    MainScreen.RECOMMENDATIONS -> {
                        IconButton(onClick = onProfileClick) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "symptoms",
                                modifier = Modifier.size(24.dp),
                                tint = AppColors.White
                            )
                        }
                    }

                    MainScreen.MARKETPLACE -> {
                        IconButton(onClick = onCartClick) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Chat",
                                modifier = Modifier.size(24.dp),
                                tint = AppColors.White
                            )
                        }
                    }
                }
            }

        )
    }, bottomBar = {
        BottomNavBar(
            currentScreen = currentScreen, onScreenSelected = { screen ->
                navigateTo(screen)
            })
    }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (currentScreen) {
                MainScreen.DASHBOARD -> HealthDataScreen(
                    healthDataViewModel,
                    preferenceViewModel,
                    onNavigateToDetail = { onBiomarker(it) })

                MainScreen.RECOMMENDATIONS -> {
                    RecommendationsTabScreen(
                        navigateBack = {
                            navigateBack()
                        },
                        viewModel = recommendationsViewModel,
                        preferencesViewModel = preferenceViewModel
                    )
                }

                MainScreen.MARKETPLACE -> {
                    MarketPlaceScreen(
                        onProductClick = {
                            onMarketPlaceClick(it)
                            println("product -> Ha1$it")
                        },
                        navigateBack = {
                            navigateBack()
                        },
                        viewModel = marketPlaceViewModel,
                    )
                }
            }
        }
    }
    if (isCsvStarted) {
        if (metrics.isNotEmpty()) {
            val csv = CsvUtils.bloodDataListToCsv(metrics)
            scope.launch {
                val filePath = saveTextFile("biomarkers.csv", csv)
                println("CSV saved to: $filePath")
                // Optionally show a Snackbar or Toast here
            }
        } else {
            println("No metrics to export.")
        }
        isCsvStarted = false
    }
}