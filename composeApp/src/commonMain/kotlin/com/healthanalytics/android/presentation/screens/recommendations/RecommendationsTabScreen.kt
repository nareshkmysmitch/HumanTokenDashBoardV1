package com.healthanalytics.android.presentation.screens.recommendations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.screens.actionplan.ActionPlanScreen
import com.healthanalytics.android.presentation.theme.AppColors

enum class RecommendationsTab {
    RECOMMENDATIONS, ACTION_PLAN
}

@Composable
fun RecommendationsTabScreen(
    viewModel: RecommendationsViewModel,
    preferencesViewModel: PreferencesViewModel,
    navigateBack: () -> Unit,
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    BackHandler { navigateBack() }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = AppColors.Black,
            contentColor=AppColors.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            RecommendationsTab.values().forEach { tab ->
                Tab(selected = selectedTab == tab, onClick = {
                    viewModel.setSelectedTab(tab)
                }, text = {
                    Text(
                        text = when (tab) {
                            RecommendationsTab.RECOMMENDATIONS -> "Recommendations"
                            RecommendationsTab.ACTION_PLAN -> "Action Plan"
                        }
                    )
                })
            }
        }

        when (selectedTab) {
            RecommendationsTab.RECOMMENDATIONS -> RecommendationsScreen(
                viewModel = viewModel,
                preferencesViewModel = preferencesViewModel,
            )

            RecommendationsTab.ACTION_PLAN -> ActionPlanScreen(viewModel, preferencesViewModel)
        }
    }
} 