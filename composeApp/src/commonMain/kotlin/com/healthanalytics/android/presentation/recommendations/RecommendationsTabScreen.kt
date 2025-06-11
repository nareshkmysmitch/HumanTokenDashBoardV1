package com.healthanalytics.android.presentation.recommendations

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.healthanalytics.android.presentation.actionplan.ActionPlanScreen

enum class RecommendationsTab {
    RECOMMENDATIONS,
    ACTION_PLAN
}

@Composable
fun RecommendationsTabScreen() {
    var selectedTab by remember { mutableStateOf(RecommendationsTab.RECOMMENDATIONS) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            modifier = Modifier.fillMaxWidth()
        ) {
            RecommendationsTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            text = when (tab) {
                                RecommendationsTab.RECOMMENDATIONS -> "Recommendations"
                                RecommendationsTab.ACTION_PLAN -> "Action Plan"
                            }
                        )
                    }
                )
            }
        }

        // Content
        when (selectedTab) {
            RecommendationsTab.RECOMMENDATIONS -> RecommendationsScreen()
            RecommendationsTab.ACTION_PLAN -> ActionPlanScreen()
        }
    }
} 