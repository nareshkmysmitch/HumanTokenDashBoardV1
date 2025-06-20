package com.healthanalytics.android.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.humantoken.ui.screens.ProductDetailScreen
import com.healthanalytics.android.presentation.screens.health.BiomarkerDetailNavWrapper
import com.healthanalytics.android.presentation.screens.health.HealthDataScreen
import com.healthanalytics.android.presentation.screens.health.HealthDataViewModel
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceScreen
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationsScreen
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationsTab
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationsTabScreen
import com.healthanalytics.android.presentation.theme.AppStrings
import org.koin.compose.koinInject

sealed class BottomNavScreen : Tab {

    object Health : BottomNavScreen() {
        @Composable
        override fun Content() {
            val mainNavigator = LocalMainNavigator.current
            val healthDataViewModel: HealthDataViewModel = koinInject()
            HealthDataScreen(
                viewModel = healthDataViewModel,
                onNavigateToDetail = { biomarker ->
                    mainNavigator.push(BiomarkerDetailNavWrapper(biomarker = biomarker))
                })
        }

        override val options: TabOptions
            @Composable get() = TabOptions(
                index = 1u,
                title = AppStrings.HEALTH_DATA,
                icon = rememberVectorPainter(Icons.Default.Home)
            )
    }


//    object Profile : BottomNavScreen() {
//        @Composable
//        override fun Content() {
//            val mainNavigator = LocalMainNavigator.current
//
//            HealthDataScreen(
//                viewModel = koinInject(), prefs = koinInject(), onNavigateToDetail = { biomarker ->
//                    mainNavigator.push(BiomarkerDetailNavWrapper(biomarker = biomarker))
//                })
//        }
//
//        override val options: TabOptions
//            @Composable get() = TabOptions(
//                index = 1u,
//                title = AppStrings.HEALTH_DATA,
//                icon = rememberVectorPainter(Icons.Default.Home)
//            )
//    }


    object Recommendations : BottomNavScreen() {
        @Composable
        override fun Content() {

            RecommendationsTabScreen(
                viewModel = koinInject(), preferencesViewModel = koinInject(), navigateBack = {})
        }

        override val options: TabOptions
            @Composable get() = TabOptions(
                index = 2u,
                title = AppStrings.RECOMMENDATIONS,
                icon = rememberVectorPainter(Icons.Default.Recommend)
            )
    }

    object Marketplace : BottomNavScreen() {
        @Composable
        override fun Content() {
            val mainNavigator = LocalMainNavigator.current
            val viewModel =
                koinInject<com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel>()

            MarketPlaceScreen(viewModel = viewModel, onProductClick = { product ->
                mainNavigator.push(
                    ProductDetailScreen(
                        product = product, viewModel = viewModel
                    )
                )
            }, navigateBack = { /* Not needed in tab navigation */ })
        }

        override val options: TabOptions
            @Composable get() = TabOptions(
                index = 3u,
                title = AppStrings.MARKET_PLACE,
                icon = rememberVectorPainter(Icons.Default.ShoppingBasket)
            )
    }
}