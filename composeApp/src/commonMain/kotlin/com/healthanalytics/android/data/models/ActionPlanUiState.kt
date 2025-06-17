package com.healthanalytics.android.data.models

import com.healthanalytics.android.utils.AppConstants

data class ActionPlanUiState(
    val recommendations: List<Recommendation> = emptyList(),
    val selectedCategory: String = AppConstants.ALL,
    val isLoading: Boolean = false,
    val error: String? = null
)