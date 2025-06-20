package com.healthanalytics.android.presentation.screens.health

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.healthanalytics.android.data.models.home.SymptomsData

class SymptomsDetailsWrapper(
    private val symptomsData: SymptomsData?,
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        SymptomsDetailsScreen(symptomsData, onNavigateBack = { navigator.pop() })
    }

}