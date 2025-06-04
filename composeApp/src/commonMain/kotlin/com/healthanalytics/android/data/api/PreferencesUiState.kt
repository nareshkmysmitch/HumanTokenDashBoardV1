package com.healthanalytics.android.data.api

data class PreferencesUiState(
    val accessToken: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)