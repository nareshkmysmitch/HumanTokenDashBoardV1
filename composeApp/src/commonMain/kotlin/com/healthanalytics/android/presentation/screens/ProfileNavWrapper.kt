package com.healthanalytics.android.presentation.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.screens.profile.ProfileScreen

class ProfileNavWrapper : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: MarketPlaceViewModel = koinInject()

        ProfileScreen(
            onNavigateBack = { navigator.pop() },
            viewModel = viewModel,
            onNavigateToTestBooking = {
                navigator.pop()
            })
    }
} 