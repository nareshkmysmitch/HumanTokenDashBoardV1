package com.healthanalytics.android.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import co.touchlab.kermit.Logger
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.presentation.components.TopAppBar
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.screens.chat.ChatViewModel
import com.healthanalytics.android.presentation.screens.chat.ConversationListNavWrapper
import com.healthanalytics.android.presentation.screens.health.HealthDataViewModel
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.screens.onboard.viewmodel.OnboardViewModel
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationsViewModel
import com.healthanalytics.android.presentation.screens.symptoms.SymptomsViewModel
import com.healthanalytics.android.presentation.screens.testbooking.TestBookingViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import org.koin.compose.koinInject

class MainScreen : Screen {

    @Composable
    override fun Content() {

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

        val bottomNavScreens = listOf(
            BottomNavScreen.Health, BottomNavScreen.Recommendations, BottomNavScreen.Marketplace
        )

        TabNavigator(bottomNavScreens.first()) { tabNavigator ->
            val currentTab = tabNavigator.current
            Scaffold(
                containerColor = AppColors.Black,
                bottomBar = {
                    NavigationBar(
                        containerColor = AppColors.Black, contentColor = Color.White
                    ) {
                        bottomNavScreens.forEach { screen ->

                            Logger.e("screen: $screen")

                            NavigationBarItem(
                                selected = currentTab == screen,
                                onClick = { tabNavigator.current = screen },

                                icon = {
                                    screen.options.icon?.let { icon ->
                                        Icon(painter = icon, contentDescription = null)
                                    } ?: Icon(
                                        painter = rememberVectorPainter(Icons.Default.Help),
                                        contentDescription = "Fallback"
                                    )
                                },

                                label = {
                                    val title = screen.options.title
                                    Text(title)
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
                },
                topBar = {
                    TopAppBar(
                        title = "Human Token", actions = {

                            when (currentTab) {
                                BottomNavScreen.Health -> {
                                    IconButton(onClick = {}) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "symptoms",
                                            modifier = Modifier.size(24.dp),
                                            tint = AppColors.White
                                        )
                                    }
                                    IconButton(onClick = { }) {
                                        Icon(
                                            imageVector = Icons.Default.Chat,
                                            contentDescription = "Chat",
                                            modifier = Modifier.size(24.dp),
                                            tint = AppColors.White
                                        )
                                    }
                                }

                                BottomNavScreen.Recommendations -> {
                                    IconButton(onClick = { }) {
                                        Icon(
                                            imageVector = Icons.Default.AccountCircle,
                                            contentDescription = "Profile",
                                            modifier = Modifier.size(24.dp),
                                            tint = AppColors.White
                                        )
                                    }
                                }

                                BottomNavScreen.Marketplace -> {
                                    IconButton(onClick = { }) {
                                        Icon(
                                            imageVector = Icons.Default.ShoppingCart,
                                            contentDescription = "Cart",
                                            modifier = Modifier.size(24.dp),
                                            tint = AppColors.White
                                        )
                                    }
                                }
                            }

                        })
                },
            ) { paddingValues ->
                Box(
                    modifier = Modifier.fillMaxSize().background(AppColors.Black)
                        .padding(paddingValues)
                ) {
                    CurrentTab()
                }
            }
        }
    }
}
