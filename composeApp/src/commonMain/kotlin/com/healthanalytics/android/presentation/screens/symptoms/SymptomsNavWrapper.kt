package com.healthanalytics.android.presentation.screens.symptoms

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject

class SymptomsNavWrapper : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: SymptomsViewModel = koinInject()

        SymptomsScreen(
            viewModel = viewModel,
            onNavigateBack = { navigator.pop() },
            onNavigateHome = { 
                // Navigate back to main screen
                navigator.pop()
            }
        )
    }
} 