package com.healthanalytics.android.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.healthanalytics.android.presentation.screens.health.HealthDataScreen
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceScreen
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationsScreen
import com.healthanalytics.android.presentation.theme.AppStrings
import org.koin.compose.koinInject




sealed class BottomNavScreen : Tab {

    protected lateinit var _options: TabOptions

    override val options: TabOptions
        get() = _options

    object Health : BottomNavScreen() {
        @Composable
        override fun Content() {
            val icon = rememberVectorPainter(Icons.Default.Home)
            _options = remember {
                TabOptions(
                    index = 0u,
                    title = AppStrings.HEALTH_DATA,
                    icon = icon
                )
            }

            HealthDataScreen(
                viewModel = koinInject(),
                prefs = koinInject(),
                onNavigateToDetail = { /* Handle navigation */ }
            )
        }
    }

    object Recommendations : BottomNavScreen() {
        @Composable
        override fun Content() {
            val icon = rememberVectorPainter(Icons.Default.Recommend)
            _options = remember {
                TabOptions(
                    index = 1u,
                    title = AppStrings.RECOMMENDATIONS,
                    icon = icon
                )
            }

            RecommendationsScreen(
                viewModel = koinInject(),
                preferencesViewModel = koinInject()
            )
        }
    }

    object Marketplace : BottomNavScreen() {
        @Composable
        override fun Content() {
            val icon = rememberVectorPainter(Icons.Default.ShoppingBasket)
            _options = remember {
                TabOptions(
                    index = 2u,
                    title = AppStrings.MARKET_PLACE,
                    icon = icon
                )
            }

            MarketPlaceScreen(
                viewModel = koinInject(),
                onProductClick = { /* Handle product click */ },
                navigateBack = { /* Handle back navigation */ }
            )
        }
    }

    companion object {
        val items = listOf(Health, Recommendations, Marketplace)
    }
}

