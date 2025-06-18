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
import com.healthanalytics.android.presentation.screens.chat.ChatViewModel
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.screens.symptoms.SymptomsViewModel
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface RootComponent {
    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Main(val component: MainComponent) : Child()
        data class Symptoms(val component: SymptomsComponent) : Child()
        data class ConversationList(val component: ConversationListComponent) : Child()
        data class Profile(val component: ProfileComponent) : Child()
        data class Cart(val component: CartComponent) : Child()
        data class BiomarkerDetail(val component: BiomarkerDetailComponent) : Child()
        data class BioMarkerFullReport(val component: BioMarkerFullReportComponent) : Child()
        data class ProductDetail(val component: ProductDetailComponent) : Child()
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext, KoinComponent {

    private val navigation = StackNavigation<Config>()
    
    // Inject ViewModels
    private val symptomsViewModel: SymptomsViewModel by inject()
    private val chatViewModel: ChatViewModel by inject()
    private val marketPlaceViewModel: MarketPlaceViewModel by inject()

    override val childStack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Main,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    private fun createChild(config: Config, componentContext: ComponentContext): RootComponent.Child =
        when (config) {
            is Config.Main -> RootComponent.Child.Main(
                component = DefaultMainComponent(
                    componentContext = componentContext,
                    onNavigateToSymptoms = { navigation.push(Config.Symptoms) },
                    onNavigateToConversationList = { navigation.push(Config.ConversationList) },
                    onNavigateToProfile = { navigation.push(Config.Profile) },
                    onNavigateToCart = { navigation.push(Config.Cart) },
                    onNavigateToBiomarkerDetail = { biomarker -> 
                        navigation.push(Config.BiomarkerDetail(biomarker = biomarker))
                    },
                    onNavigateToBioMarkerFullReport = { biomarker -> 
                        navigation.push(Config.BioMarkerFullReport(biomarker = biomarker))
                    },
                    onNavigateToProductDetail = { product -> 
                        navigation.push(Config.ProductDetail(product = product))
                    }
                )
            )
            
            is Config.Symptoms -> RootComponent.Child.Symptoms(
                component = DefaultSymptomsComponent(
                    componentContext = componentContext,
                    viewModel = symptomsViewModel,
                    onNavigateBack = { navigation.pop() }
                )
            )
            
            is Config.ConversationList -> RootComponent.Child.ConversationList(
                component = DefaultConversationListComponent(
                    componentContext = componentContext,
                    viewModel = chatViewModel,
                    onNavigateBack = { navigation.pop() },
                    onNavigateToChat = { conversationId -> 
                        // Handle chat navigation if needed
                    }
                )
            )
            
            is Config.Profile -> RootComponent.Child.Profile(
                component = DefaultProfileComponent(
                    componentContext = componentContext,
                    viewModel = marketPlaceViewModel,
                    onNavigateBack = { navigation.pop() }
                )
            )
            
            is Config.Cart -> RootComponent.Child.Cart(
                component = DefaultCartComponent(
                    componentContext = componentContext,
                    viewModel = marketPlaceViewModel,
                    onNavigateBack = { navigation.pop() }
                )
            )
            
            is Config.BiomarkerDetail -> RootComponent.Child.BiomarkerDetail(
                component = DefaultBiomarkerDetailComponent(
                    componentContext = componentContext,
                    biomarker = config.biomarker,
                    onNavigateBack = { navigation.pop() },
                    onNavigateToFullReport = { biomarker -> 
                        navigation.push(Config.BioMarkerFullReport(biomarker = biomarker))
                    }
                )
            )
            
            is Config.BioMarkerFullReport -> RootComponent.Child.BioMarkerFullReport(
                component = DefaultBioMarkerFullReportComponent(
                    componentContext = componentContext,
                    biomarker = config.biomarker,
                    onNavigateBack = { navigation.pop() }
                )
            )
            
            is Config.ProductDetail -> RootComponent.Child.ProductDetail(
                component = DefaultProductDetailComponent(
                    componentContext = componentContext,
                    product = config.product,
                    viewModel = marketPlaceViewModel,
                    onNavigateBack = { navigation.pop() }
                )
            )
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Main : Config
        
        @Serializable
        data object Symptoms : Config
        
        @Serializable
        data object ConversationList : Config
        
        @Serializable
        data object Profile : Config
        
        @Serializable
        data object Cart : Config
        
        @Serializable
        data class BiomarkerDetail(val biomarker: BloodData?) : Config
        
        @Serializable
        data class BioMarkerFullReport(val biomarker: BloodData?) : Config
        
        @Serializable
        data class ProductDetail(val product: Product) : Config
    }
} 