package com.healthanalytics.android.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.healthanalytics.android.data.api.BloodData
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.presentation.screens.health.HealthDataViewModel
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationsViewModel

// Health Component
interface HealthComponent {
    val viewModel: HealthDataViewModel
    val onNavigateToSymptoms: () -> Unit
    val onNavigateToConversationList: () -> Unit
    val onNavigateToBiomarkerDetail: (BloodData?) -> Unit
}

class DefaultHealthComponent(
    componentContext: ComponentContext,
    override val viewModel: HealthDataViewModel,
    override val onNavigateToSymptoms: () -> Unit,
    override val onNavigateToConversationList: () -> Unit,
    override val onNavigateToBiomarkerDetail: (BloodData?) -> Unit,
) : HealthComponent, ComponentContext by componentContext

// Recommendations Component
interface RecommendationsComponent {
    val viewModel: RecommendationsViewModel
    val onNavigateToProfile: () -> Unit
}

class DefaultRecommendationsComponent(
    componentContext: ComponentContext,
    override val viewModel: RecommendationsViewModel,
    override val onNavigateToProfile: () -> Unit,
) : RecommendationsComponent, ComponentContext by componentContext

// Marketplace Component
interface MarketplaceComponent {
    val viewModel: MarketPlaceViewModel
    val onNavigateToCart: () -> Unit
    val onNavigateToProductDetail: (Product) -> Unit
}

class DefaultMarketplaceComponent(
    componentContext: ComponentContext,
    override val viewModel: MarketPlaceViewModel,
    override val onNavigateToCart: () -> Unit,
    override val onNavigateToProductDetail: (Product) -> Unit,
) : MarketplaceComponent, ComponentContext by componentContext 