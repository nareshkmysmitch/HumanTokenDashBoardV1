package com.healthanalytics.android.presentation.screens.health

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.healthanalytics.android.data.api.BloodData

class BioMarkerFullReportNavWrapper(
    private val biomarker: BloodData?
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        BioMarkerFullReportScreen(
            biomarker = biomarker,
            onNavigateBack = { navigator.pop() }
        )
    }
} 