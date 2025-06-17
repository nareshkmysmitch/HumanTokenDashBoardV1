package com.healthanalytics.android.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.healthanalytics.android.presentation.screens.health.HealthDataScreen
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceScreen
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationsScreen
import com.healthanalytics.android.presentation.theme.AppStrings
import org.koin.compose.koinInject

sealed class BottomNavScreen : Tab {
    object Health : BottomNavScreen() {
        override val options: TabOptions
            @Composable get() = TabOptions(
                index = 0u,
                title = AppStrings.HEALTH_DATA,
                icon = { Icons.Default.Home } as Painter?)

        @Composable
        override fun Content() {
            HealthDataScreen(
                viewModel = koinInject(),
                prefs = koinInject(),
                onNavigateToDetail = { /* Handle navigation */ })
        }
    }

    object Recommendations : BottomNavScreen() {
        override val options: TabOptions
            @Composable get() = TabOptions(
                index = 1u,
                title = AppStrings.RECOMMENDATIONS,
                icon = { Icons.Default.Recommend } as Painter?)

        @Composable
        override fun Content() {
            RecommendationsScreen(
                viewModel = koinInject(), preferencesViewModel = koinInject()
            )
        }
    }

    object Marketplace : BottomNavScreen() {
        override val options: TabOptions
            @Composable get() = TabOptions(
                index = 2u,
                title = AppStrings.MARKET_PLACE,
                icon = { Icons.Default.ShoppingBasket } as Painter?)

        @Composable
        override fun Content() {
            MarketPlaceScreen(
                viewModel = koinInject(),
                onProductClick = { /* Handle product click */ },
                navigateBack = { /* Handle back navigation */ })
        }
    }
} 