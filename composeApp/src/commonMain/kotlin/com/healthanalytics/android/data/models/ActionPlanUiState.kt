package com.healthanalytics.android.data.models

data class ActionPlanUiState(
    val recommendations: List<Recommendation> = emptyList(),
    val selectedCategory: String = "All",
    val isLoading: Boolean = false,
    val error: String? = null
)