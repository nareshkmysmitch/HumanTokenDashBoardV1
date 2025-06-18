package com.healthanalytics.android.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.healthanalytics.android.data.api.BloodData
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.presentation.screens.chat.ChatViewModel
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.screens.symptoms.SymptomsViewModel

// Symptoms Component
interface SymptomsComponent {
    val viewModel: SymptomsViewModel
    val onNavigateBack: () -> Unit
}

class DefaultSymptomsComponent(
    componentContext: ComponentContext,
    override val viewModel: SymptomsViewModel,
    override val onNavigateBack: () -> Unit,
) : SymptomsComponent, ComponentContext by componentContext

// Conversation List Component
interface ConversationListComponent {
    val viewModel: ChatViewModel
    val onNavigateBack: () -> Unit
    val onNavigateToChat: (String) -> Unit
}

class DefaultConversationListComponent(
    componentContext: ComponentContext,
    override val viewModel: ChatViewModel,
    override val onNavigateBack: () -> Unit,
    override val onNavigateToChat: (String) -> Unit,
) : ConversationListComponent, ComponentContext by componentContext

// Profile Component
interface ProfileComponent {
    val viewModel: MarketPlaceViewModel
    val onNavigateBack: () -> Unit
}

class DefaultProfileComponent(
    componentContext: ComponentContext,
    override val viewModel: MarketPlaceViewModel,
    override val onNavigateBack: () -> Unit,
) : ProfileComponent, ComponentContext by componentContext

// Cart Component
interface CartComponent {
    val viewModel: MarketPlaceViewModel
    val onNavigateBack: () -> Unit
}

class DefaultCartComponent(
    componentContext: ComponentContext,
    override val viewModel: MarketPlaceViewModel,
    override val onNavigateBack: () -> Unit,
) : CartComponent, ComponentContext by componentContext

// Biomarker Detail Component
interface BiomarkerDetailComponent {
    val biomarker: BloodData?
    val onNavigateBack: () -> Unit
    val onNavigateToFullReport: (BloodData?) -> Unit
}

class DefaultBiomarkerDetailComponent(
    componentContext: ComponentContext,
    override val biomarker: BloodData?,
    override val onNavigateBack: () -> Unit,
    override val onNavigateToFullReport: (BloodData?) -> Unit,
) : BiomarkerDetailComponent, ComponentContext by componentContext

// BioMarker Full Report Component
interface BioMarkerFullReportComponent {
    val biomarker: BloodData?
    val onNavigateBack: () -> Unit
}

class DefaultBioMarkerFullReportComponent(
    componentContext: ComponentContext,
    override val biomarker: BloodData?,
    override val onNavigateBack: () -> Unit,
) : BioMarkerFullReportComponent, ComponentContext by componentContext

// Product Detail Component
interface ProductDetailComponent {
    val product: Product
    val viewModel: MarketPlaceViewModel
    val onNavigateBack: () -> Unit
}

class DefaultProductDetailComponent(
    componentContext: ComponentContext,
    override val product: Product,
    override val viewModel: MarketPlaceViewModel,
    override val onNavigateBack: () -> Unit,
) : ProductDetailComponent, ComponentContext by componentContext 