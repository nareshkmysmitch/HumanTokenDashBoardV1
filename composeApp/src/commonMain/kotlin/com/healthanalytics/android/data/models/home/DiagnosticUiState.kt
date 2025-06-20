package com.healthanalytics.android.data.models.home

import com.healthanalytics.android.data.models.Recommendation

data class DiagnosticUiState(
    val diagnostics: List<LocalDiagnostic> = emptyList(),
    val selectedCategory: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)