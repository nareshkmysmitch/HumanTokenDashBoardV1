package com.healthanalytics.android.data.models.home

import com.healthanalytics.android.data.models.Recommendation

data class DiagnosticUiState(
    val recommendations: List<Recommendation> = emptyList(),
    val selectedCategory: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)