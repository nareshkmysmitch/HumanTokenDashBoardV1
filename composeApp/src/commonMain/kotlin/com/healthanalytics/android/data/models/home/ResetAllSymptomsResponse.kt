package com.healthanalytics.android.data.models.home

import kotlinx.serialization.Serializable

@Serializable
data class ResetAllSymptomsResponse(
    val isReset: Boolean? = null
)