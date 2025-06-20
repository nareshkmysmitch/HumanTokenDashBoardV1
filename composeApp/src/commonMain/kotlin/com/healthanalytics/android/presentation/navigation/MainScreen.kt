package com.healthanalytics.android.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.example.humantoken.ui.screens.CartScreen
import com.healthanalytics.android.payment.RazorpayHandler
import com.healthanalytics.android.presentation.screens.ProfileNavWrapper
import com.healthanalytics.android.presentation.screens.chat.ConversationListNavWrapper
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.screens.onboard.CreateAccountContainer
import com.healthanalytics.android.presentation.screens.onboard.LoginScreenContainer
import com.healthanalytics.android.presentation.screens.onboard.LoginScreenNav
import com.healthanalytics.android.presentation.screens.onboard.OTPContainer
import com.healthanalytics.android.presentation.screens.onboard.OnboardRoute
import com.healthanalytics.android.presentation.screens.onboard.PaymentScreenContainer
import com.healthanalytics.android.presentation.screens.onboard.ScheduleBloodTestContainer
import com.healthanalytics.android.presentation.screens.onboard.viewmodel.OnboardViewModel
import com.healthanalytics.android.presentation.screens.symptoms.SymptomsNavWrapper
import com.healthanalytics.android.presentation.theme.AppColors
import org.koin.compose.KoinContext
import org.koin.compose.getKoin
import org.koin.compose.koinInject

val LocalMainNavigator = staticCompositionLocalOf<Navigator> {
    error("LocalMainNavigator not provided")
}

class MainScreen : Screen {

    @Composable
    fun TopBarActions(currentTab: Tab, mainNavigator: Navigator) {

        val marketPlaceViewModel: MarketPlaceViewModel = koinInject()

        when (currentTab) {
            BottomNavScreen.Health -> {
                IconButton(onClick = { mainNavigator.push(SymptomsNavWrapper()) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "symptoms",
                        tint = AppColors.White
                    )
                }
                IconButton(onClick = { mainNavigator.push(ConversationListNavWrapper()) }) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Chat",
                        tint = AppColors.White
                    )
                }
            }

            BottomNavScreen.Recommendations -> {
                IconButton(onClick = { mainNavigator.push(ProfileNavWrapper()) }) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = AppColors.White
                    )
                }
            }

            BottomNavScreen.Marketplace -> {
                IconButton(onClick = {
                    mainNavigator.push(CartScreen(viewModel = marketPlaceViewModel))
                }) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = AppColors.White
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val onboardViewModel: OnboardViewModel = koinInject()
        val razorpayHandler: RazorpayHandler = getKoin().get()
        val onBoardUiState = onboardViewModel.onBoardUiState.collectAsStateWithLifecycle().value

        if (onBoardUiState.isLoading) {
            CircularProgressIndicator()
        } else if (!onBoardUiState.hasAccessToken) {
            OnboardNavWrapper(
                onboardViewModel = onboardViewModel,
                razorpayHandler = razorpayHandler,
                isLoggedIn = { onboardViewModel.updateOnBoardState() }).Content()
        } else {
            Navigator(BottomNavScreen.Health) { mainNavigator ->

                val bottomNavScreens = listOf(
                    BottomNavScreen.Health,
                    BottomNavScreen.Recommendations,
                    BottomNavScreen.Marketplace
                )

                val currentScreen = mainNavigator.lastItem

                if (currentScreen is BottomNavScreen) {
                    // Render Bottom Nav UI
                    TabNavigator(currentScreen) { tabNavigator ->

                        val currentTab = tabNavigator.current

                        CompositionLocalProvider(LocalMainNavigator provides mainNavigator) {
                            Scaffold(
                                containerColor = AppColors.Black,

                                topBar = {
                                    TopAppBar(
                                        title = { Text("Human Token") }, actions = {
                                            TopBarActions(
                                                currentTab = currentTab,
                                                mainNavigator = mainNavigator
                                            )
                                        }, colors = TopAppBarDefaults.topAppBarColors(
                                            containerColor = AppColors.Black,
                                            titleContentColor = Color.White
                                        )
                                    )
                                },

                                bottomBar = {
                                    NavigationBar(
                                        containerColor = AppColors.Black, contentColor = Color.White
                                    ) {
                                        bottomNavScreens.forEach { screen ->
                                            NavigationBarItem(
                                                selected = currentTab == screen,
                                                onClick = { tabNavigator.current = screen },
                                                icon = {
                                                    screen.options.icon?.let { icon ->
                                                        Icon(
                                                            painter = icon,
                                                            contentDescription = null
                                                        )
                                                    } ?: Icon(
                                                        painter = rememberVectorPainter(Icons.Default.Help),
                                                        contentDescription = "Fallback"
                                                    )
                                                },
                                                label = {
                                                    Text(screen.options.title)
                                                },
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = AppColors.Pink,
                                                    selectedTextColor = AppColors.Pink,
                                                    indicatorColor = AppColors.Black,
                                                    unselectedIconColor = Color.White,
                                                    unselectedTextColor = Color.White
                                                )
                                            )
                                        }
                                    }
                                }) { paddingValues ->
                                Box(
                                    modifier = Modifier.fillMaxSize().background(AppColors.Black)
                                        .padding(paddingValues)
                                ) {
                                    CurrentTab()
                                }
                            }
                        }
                    }
                } else {
                    // Not a tab -> render directly (no bottom nav)
                    currentScreen.Content()
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
                    startDestination = OnboardRoute.Login,
                    modifier = Modifier.padding(innerPadding)
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
}

class OnboardNavWrapper(
    private val onboardViewModel: OnboardViewModel,
    private val razorpayHandler: RazorpayHandler,
    private val isLoggedIn: () -> Unit
) : Screen {
    @Composable
    override fun Content() {
        Navigator(
            LoginScreenNav(
                onboardViewModel = onboardViewModel,
                razorpayHandler = razorpayHandler,
                isLoggedIn = isLoggedIn
            )
        )
    }
}

