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
import com.healthanalytics.android.presentation.screens.actionplan.ActionPlanScreen
import org.koin.compose.koinInject

enum class RecommendationsTab {
    RECOMMENDATIONS, ACTION_PLAN
}

@Composable
fun RecommendationsTabScreen(
    viewModel: RecommendationsViewModel = koinInject(),
    navigateBack: () -> Unit,
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    BackHandler { navigateBack() }
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab.ordinal, modifier = Modifier.fillMaxWidth()
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
            RecommendationsTab.RECOMMENDATIONS -> RecommendationsScreen()
            RecommendationsTab.ACTION_PLAN -> ActionPlanScreen()
        }
    }
} 