package com.healthanalytics.android.data.models

data class RecommendationsUiState(
    val recommendations: List<Recommendation> = emptyList(),
    val selectedCategory: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)