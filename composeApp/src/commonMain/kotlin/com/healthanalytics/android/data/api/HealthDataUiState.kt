package com.healthanalytics.android.data.api

import com.healthanalytics.android.data.models.home.BloodData

data class HealthDataUiState(
    val biomarker: List<BloodData?> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: String? = null,
    val searchQuery: String = "",
    val lastUpdated: BloodData? = null,
)