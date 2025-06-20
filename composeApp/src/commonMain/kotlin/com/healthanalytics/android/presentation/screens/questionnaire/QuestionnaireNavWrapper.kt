package com.healthanalytics.android.presentation.screens.questionnaire

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.healthanalytics.android.presentation.screens.questionnaire.viewmodel.QuestionnaireViewModel
import org.koin.compose.koinInject

class QuestionnaireNavWrapper : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val questionnaireViewModel: QuestionnaireViewModel = koinInject()

        QuestionnaireScreen(
            viewModel = questionnaireViewModel,
            onNavigateToCompleted = {
                navigator.pop()
            },
            onNavigateToHome = {
                navigator.pop()
            },
            onNavigateToContinuation = {
                navigator.pop()
            },
        )
    }
}