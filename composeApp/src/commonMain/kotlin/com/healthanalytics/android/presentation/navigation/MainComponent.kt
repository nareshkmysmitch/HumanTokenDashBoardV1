package com.healthanalytics.android.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.healthanalytics.android.data.api.BloodData
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.presentation.screens.health.HealthDataViewModel
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationsViewModel
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface MainComponent {
    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Health(val component: HealthComponent) : Child()
        data class Recommendations(val component: RecommendationsComponent) : Child()
        data class Marketplace(val component: MarketplaceComponent) : Child()
    }
}

class DefaultMainComponent(
    componentContext: ComponentContext,
    private val onNavigateToSymptoms: () -> Unit,
    private val onNavigateToConversationList: () -> Unit,
    private val onNavigateToProfile: () -> Unit,
    private val onNavigateToCart: () -> Unit,
    private val onNavigateToBiomarkerDetail: (BloodData?) -> Unit,
    private val onNavigateToBioMarkerFullReport: (BloodData?) -> Unit,
    private val onNavigateToProductDetail: (Product) -> Unit,
) : MainComponent, ComponentContext by componentContext, KoinComponent {

    private val navigation = StackNavigation<Config>()
    
    // Inject ViewModels
    private val healthDataViewModel: HealthDataViewModel by inject()
    private val recommendationsViewModel: RecommendationsViewModel by inject()
    private val marketPlaceViewModel: MarketPlaceViewModel by inject()

    override val childStack: Value<ChildStack<*, MainComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Health,
            handleBackButton = false, // We handle back button manually for tabs
            childFactory = ::createChild,
        )

    private fun createChild(config: Config, componentContext: ComponentContext): MainComponent.Child =
        when (config) {
            is Config.Health -> MainComponent.Child.Health(
                component = DefaultHealthComponent(
                    componentContext = componentContext,
                    viewModel = healthDataViewModel,
                    onNavigateToSymptoms = onNavigateToSymptoms,
                    onNavigateToConversationList = onNavigateToConversationList,
                    onNavigateToBiomarkerDetail = onNavigateToBiomarkerDetail
                )
            )
            
            is Config.Recommendations -> MainComponent.Child.Recommendations(
                component = DefaultRecommendationsComponent(
                    componentContext = componentContext,
                    viewModel = recommendationsViewModel,
                    onNavigateToProfile = onNavigateToProfile
                )
            )
            
            is Config.Marketplace -> MainComponent.Child.Marketplace(
                component = DefaultMarketplaceComponent(
                    componentContext = componentContext,
                    viewModel = marketPlaceViewModel,
                    onNavigateToCart = onNavigateToCart,
                    onNavigateToProductDetail = onNavigateToProductDetail
                )
            )
        }

    fun onTabSelected(tab: TabType) {
        val config = when (tab) {
            TabType.Health -> Config.Health
            TabType.Recommendations -> Config.Recommendations
            TabType.Marketplace -> Config.Marketplace
        }
        navigation.push(config)
    }

    enum class TabType {
        Health, Recommendations, Marketplace
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Health : Config
        
        @Serializable
        data object Recommendations : Config
        
        @Serializable
        data object Marketplace : Config
    }
} 