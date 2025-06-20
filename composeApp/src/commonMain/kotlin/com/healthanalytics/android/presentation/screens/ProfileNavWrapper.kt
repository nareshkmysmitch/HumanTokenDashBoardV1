package com.healthanalytics.android.presentation.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.screens.profile.ProfileScreen
import com.healthanalytics.android.presentation.screens.questionnaire.QuestionnaireNavWrapper
import com.healthanalytics.android.presentation.screens.questionnaire.viewmodel.QuestionnaireViewModel
import org.koin.compose.koinInject

class ProfileNavWrapper : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: MarketPlaceViewModel = koinInject()
        val questionnaireViewModel: QuestionnaireViewModel = koinInject()

        ProfileScreen(
            onNavigateBack = { navigator.pop() },
            viewModel = viewModel,
            onNavigateToTestBooking = {
                navigator.pop()
            },
            questionnaireViewModel = questionnaireViewModel,
            onQuestionnaireNavigate = {
                questionnaireViewModel.saveQuestionnaireDetails(
                    assessmentId = "105",
                    nextQuestionId = 0,
                    displayName = "LifeStyle"
                )
                navigator.push(QuestionnaireNavWrapper())
            }
        )
    }
} 