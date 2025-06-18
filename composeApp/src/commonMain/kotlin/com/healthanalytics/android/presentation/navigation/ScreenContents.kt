package com.healthanalytics.android.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.presentation.screens.health.HealthDataScreen
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceScreen
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationsScreen
import com.healthanalytics.android.presentation.screens.symptoms.SymptomsScreen
import com.healthanalytics.android.presentation.screens.health.BiomarkerDetailScreen
import com.healthanalytics.android.presentation.screens.health.BioMarkerFullReportScreen
import com.healthanalytics.android.presentation.screens.ProfileScreen
import com.healthanalytics.android.presentation.screens.chat.ConversationListScreen
import com.example.humantoken.ui.screens.CartScreen
import com.example.humantoken.ui.screens.ProductDetailScreen
import com.healthanalytics.android.presentation.theme.AppColors

// Tab Screen Contents
@Composable
fun HealthContent(component: HealthComponent) {
    HealthDataScreen(
        viewModel = component.viewModel,
        prefs = org.koin.compose.koinInject(),
        onNavigateToDetail = { biomarker ->
            component.onNavigateToBiomarkerDetail(biomarker)
        }
    )
}

@Composable
fun RecommendationsContent(component: RecommendationsComponent) {
    RecommendationsScreen(
        viewModel = component.viewModel,
        preferencesViewModel = org.koin.compose.koinInject()
    )
}

@Composable
fun MarketplaceContent(component: MarketplaceComponent) {
    MarketPlaceScreen(
        viewModel = component.viewModel,
        onProductClick = { product ->
            component.onNavigateToProductDetail(product)
        },
        navigateBack = { /* Not needed in tab navigation */ }
    )
}

// Detail Screen Contents
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomsContent(component: SymptomsComponent, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Symptoms") },
                navigationIcon = {
                    IconButton(onClick = { component.onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Black,
                    titleContentColor = AppColors.White
                )
            )
        },
        containerColor = AppColors.Black
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppColors.Black)
                .padding(paddingValues)
        ) {
            SymptomsScreen(
                viewModel = component.viewModel,
                onNavigateBack = { component.onNavigateBack() },
                onNavigateHome = { component.onNavigateBack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListContent(component: ConversationListComponent, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conversations") },
                navigationIcon = {
                    IconButton(onClick = { component.onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Black,
                    titleContentColor = AppColors.White
                )
            )
        },
        containerColor = AppColors.Black
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppColors.Black)
                .padding(paddingValues)
        ) {
            ConversationListScreen(
                onNavigateToChat = { conversationId ->
                    component.onNavigateToChat(conversationId)
                },
                viewModel = component.viewModel,
                navigator = org.koin.compose.koinInject()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(component: ProfileComponent, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { component.onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Black,
                    titleContentColor = AppColors.White
                )
            )
        },
        containerColor = AppColors.Black
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppColors.Black)
                .padding(paddingValues)
        ) {
            ProfileScreen(
                onNavigateBack = { component.onNavigateBack() },
                viewModel = component.viewModel,
                onNavigateToTestBooking = { /* Handle test booking */ }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartContent(component: CartComponent, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cart") },
                navigationIcon = {
                    IconButton(onClick = { component.onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Black,
                    titleContentColor = AppColors.White
                )
            )
        },
        containerColor = AppColors.Black
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppColors.Black)
                .padding(paddingValues)
        ) {
            CartScreen(viewModel = component.viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiomarkerDetailContent(component: BiomarkerDetailComponent, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Biomarker Details") },
                navigationIcon = {
                    IconButton(onClick = { component.onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Black,
                    titleContentColor = AppColors.White
                )
            )
        },
        containerColor = AppColors.Black
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppColors.Black)
                .padding(paddingValues)
        ) {
            BiomarkerDetailScreen(
                biomarker = component.biomarker,
                onNavigateBack = { component.onNavigateBack() },
                onNavigateFullReport = { biomarker ->
                    component.onNavigateToFullReport(biomarker)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BioMarkerFullReportContent(component: BioMarkerFullReportComponent, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Full Report") },
                navigationIcon = {
                    IconButton(onClick = { component.onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Black,
                    titleContentColor = AppColors.White
                )
            )
        },
        containerColor = AppColors.Black
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppColors.Black)
                .padding(paddingValues)
        ) {
            BioMarkerFullReportScreen(
                biomarker = component.biomarker ?: com.healthanalytics.android.data.api.BloodData(),
                onNavigateBack = { component.onNavigateBack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailContent(component: ProductDetailComponent, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = { component.onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Black,
                    titleContentColor = AppColors.White
                )
            )
        },
        containerColor = AppColors.Black
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppColors.Black)
                .padding(paddingValues)
        ) {
            ProductDetailScreen(
                product = component.product,
                viewModel = component.viewModel
            )
        }
    }
} 