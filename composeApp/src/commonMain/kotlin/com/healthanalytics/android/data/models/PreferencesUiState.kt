package com.healthanalytics.android.data.models

data class PreferencesUiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)