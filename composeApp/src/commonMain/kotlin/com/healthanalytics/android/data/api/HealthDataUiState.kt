package com.healthanalytics.android.data.api

import com.healthanalytics.android.data.models.home.BloodData
import com.healthanalytics.android.data.models.home.SymptomsData

data class HealthDataUiState(
    val biomarker: List<BloodData?> = emptyList(),
    val symptomsData: List<SymptomsData?> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: String? = null,
    val searchQuery: String = "",
    val lastUpdated: BloodData? = null,
)