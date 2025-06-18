package com.healthanalytics.android.data.models.profile

import kotlinx.serialization.Serializable

@Serializable
data class UpdatedPreferenceResponse(
    val is_updated: Boolean? = null
)