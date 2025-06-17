package com.healthanalytics.android.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cafe.adriel.voyager.navigator.Navigator
import com.example.humantoken.ui.screens.CartScreen
import com.example.humantoken.ui.screens.ProductDetailScreen
import com.healthanalytics.android.data.api.BloodData
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.payment.RazorpayHandler
import com.healthanalytics.android.presentation.components.BottomNavBar
import com.healthanalytics.android.presentation.components.MainScreen
import com.healthanalytics.android.presentation.components.Pages
import com.healthanalytics.android.presentation.components.Pages.BIOMARKERS_DETAIL
import com.healthanalytics.android.presentation.components.Pages.BIOMARKER_FULL_REPORT
import com.healthanalytics.android.presentation.components.Pages.CART
import com.healthanalytics.android.presentation.components.Pages.CHAT
import com.healthanalytics.android.presentation.components.Pages.CONVERSATION_LIST
import com.healthanalytics.android.presentation.components.Pages.HOME
import com.healthanalytics.android.presentation.components.Pages.MARKETPLACE_DETAIL
import com.healthanalytics.android.presentation.components.Pages.PROFILE
import com.healthanalytics.android.presentation.components.Pages.SCHEDULE_TEST_BOOKING
import com.healthanalytics.android.presentation.components.Pages.TEST_BOOKING
import com.healthanalytics.android.presentation.components.TopAppBar
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.screens.ProfileScreen
import com.healthanalytics.android.presentation.screens.chat.ChatScreenNavWrapper
import com.healthanalytics.android.presentation.screens.chat.ChatScreen as VoyagerChatScreen
import com.healthanalytics.android.presentation.screens.chat.ChatViewModel
import com.healthanalytics.android.presentation.screens.chat.ConversationListNavWrapper
import com.healthanalytics.android.presentation.screens.chat.ConversationListScreen as VoyagerConversationListScreen
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
import org.koin.compose.KoinContext
import org.koin.compose.getKoin
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun HealthAnalyticsApp() {

    AppTheme {

        var currentPages by remember { mutableStateOf<Pages>(HOME) }
        var lastMainPages by remember { mutableStateOf<Pages>(HOME) }
        var biomarker by remember { mutableStateOf(BloodData()) }

        fun navigateTo(pages: Pages) {
            // Remember the last main screen when navigating away from main screens
            if (currentPages is HOME || currentPages is PROFILE || currentPages is CONVERSATION_LIST) {
                lastMainPages = currentPages
            }
            currentPages = pages
        }

        fun navigateBack() {
            currentPages = if (lastMainPages == currentPages) {
                HOME
            } else {
                lastMainPages
            }
        }

        val onboardViewModel: OnboardViewModel = koinInject<OnboardViewModel>()
        val healthDataViewModel: HealthDataViewModel = koinInject<HealthDataViewModel>()
        val preferencesViewModel: PreferencesViewModel = koinInject<PreferencesViewModel>()
        val marketPlaceViewModel: MarketPlaceViewModel = koinInject<MarketPlaceViewModel>()
        val testBookingViewModel: TestBookingViewModel = koinInject<TestBookingViewModel>()
        val chatViewModel: ChatViewModel = koinInject<ChatViewModel>()
        val symptomsViewModel: SymptomsViewModel = koinInject<SymptomsViewModel>()
        val recommendationsViewModel: RecommendationsViewModel =
            koinInject<RecommendationsViewModel>()
        val onBoardUiState by onboardViewModel.onBoardUiState.collectAsStateWithLifecycle()
        var localTestList by remember { mutableStateOf<List<Product>>(emptyList()) }
        when {
            onBoardUiState.isLoading -> CircularProgressIndicator()
            onBoardUiState.hasAccessToken -> {
                when (currentPages) {
                    PROFILE -> {
                        ProfileScreen(
                            onNavigateBack = { navigateTo(HOME) },
                            viewModel = marketPlaceViewModel,
                            onNavigateToTestBooking = {
                                navigateTo(TEST_BOOKING)
                            },
                        )
                    }

                    CONVERSATION_LIST -> {
                        Navigator(ConversationListNavWrapper())
                    }

                    is CHAT -> {
                        val chatScreen = currentPages as CHAT
                        Navigator(ChatScreenNavWrapper(conversationId = chatScreen.conversationId))
                    }

                    is MARKETPLACE_DETAIL -> {
                        val marketplaceScreen = currentPages as MARKETPLACE_DETAIL

                        Navigator(
                            ProductDetailScreen(
                                product = marketplaceScreen.product,
                                viewModel = marketPlaceViewModel,
                            )
                        )

//                        ProductDetailScreen(
//                            product = marketplaceScreen.product,
//                            onNavigateBack = { navigateBack() },
//                            onNavigateToCart = { navigateTo(CART) },
//                            viewModel = marketPlaceViewModel,
//                        )
                    }

                    CART -> {
                        Navigator(CartScreen(viewModel = marketPlaceViewModel))
                    }

                    SCHEDULE_TEST_BOOKING -> {

                        Navigator(ScheduleTestBookingScreen(viewModel = marketPlaceViewModel))

//                        ScheduleTestBookingScreen(
//                            onNavigateBack = { navigateTo(TEST_BOOKING) },
//                            viewModel = marketPlaceViewModel,
//                        )
                    }

                    TEST_BOOKING -> {
                        Navigator(
                            TestBookingScreen(
                                viewModel = testBookingViewModel,
                                marketPlaceViewModel = marketPlaceViewModel,
                            )
                        )

//                        TestBookingScreen(
//                            onNavigateBack = { navigateBack() },
//                            onNavigateToSchedule = {
//                                localTestList = localTestList + it
//                                navigateTo(SCHEDULE_TEST_BOOKING)
//                            },
//                            viewModel = testBookingViewModel,
//                            marketPlaceViewModel = marketPlaceViewModel,
//                        )
                    }

                    HOME -> {
                        HomeScreen(
                            onProfileClick = {
                                navigateTo(PROFILE)
                            },
                            onChatClick = {
                                navigateTo(CONVERSATION_LIST)
                            },
                            onMarketPlaceClick = { product ->
                                navigateTo(MARKETPLACE_DETAIL(product))
                            },
                            onCartClick = {
                                navigateTo(CART)
                            },
                            onSymptomsClick = {
                                navigateTo(Pages.SYMPTOMS)
                            },
                            onBiomarkerFullReportClick = {
                                biomarker = it ?: BloodData()
                                navigateTo(BIOMARKER_FULL_REPORT)
                            },
                            onBiomarker = {
                                biomarker = it ?: BloodData()
                                navigateTo(BIOMARKERS_DETAIL)
                            },
                            healthDataViewModel = healthDataViewModel,
                            preferenceViewModel = preferencesViewModel,
                            recommendationsViewModel = recommendationsViewModel,
                            marketPlaceViewModel = marketPlaceViewModel,
                        )
                    }

                    BIOMARKERS_DETAIL -> {
                        BiomarkerDetailScreen(
                            onNavigateBack = { navigateBack() },
                            biomarker = biomarker,
                            onNavigateFullReport = { navigateTo(BIOMARKER_FULL_REPORT) })
                    }

                    BIOMARKER_FULL_REPORT -> {
                        BioMarkerFullReportScreen(
                            onNavigateBack = {
                                navigateBack()
                            }, biomarker = biomarker
                        )
                    }

                    Pages.SYMPTOMS -> {
                        SymptomsScreen(
                            onNavigateBack = { navigateBack() },
                            viewModel = symptomsViewModel,
                            onNavigateHome = { navigateTo(HOME) })
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
}

@Composable
private inline fun <reified T : ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController,
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
                        onViewAllBiomarkers = {})
                }
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
                    PaymentScreenContainer(
                        onboardViewModel = onboardViewModel,
                        razorpayHandler = razorpayHandler,
                        onBackClick = {
                            navController.navigateUp()
                        },
                        isPaymentCompleted = {
                            isLoggedIn()
                        })
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

    var currentScreen by remember { mutableStateOf(MainScreen.DASHBOARD) }

    fun navigateTo(screen: MainScreen) {
        currentScreen = screen
    }

    fun navigateBack() {
        currentScreen = MainScreen.DASHBOARD
    }

    Scaffold(topBar = {
        TopAppBar(
            title = "Human Token", actions = {
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
}