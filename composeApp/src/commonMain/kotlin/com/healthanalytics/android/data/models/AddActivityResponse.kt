package com.healthanalytics.android.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AddActivityResponse(
    val isUpdated: Boolean? = null
)