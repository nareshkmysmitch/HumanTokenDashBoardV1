package com.healthanalytics.android.data.api

data class PreferencesUiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)