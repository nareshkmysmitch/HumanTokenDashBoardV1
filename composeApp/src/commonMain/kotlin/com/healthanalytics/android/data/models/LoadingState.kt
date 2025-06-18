package com.healthanalytics.android.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LoadingState(
    val isLoading: Boolean = false,
    val error: String? = null,
)