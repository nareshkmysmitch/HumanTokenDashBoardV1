package com.healthanalytics.android.data.api

data class HealthDataUiState(
    val metrics: List<BloodData?> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: String? = null,
    val searchQuery: String = "",
    val lastUpdated: BloodData? = null,
)