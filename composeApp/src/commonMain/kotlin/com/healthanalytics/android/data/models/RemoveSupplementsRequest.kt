package com.healthanalytics.android.data.models

import kotlinx.serialization.Serializable

@Serializable
data class RemoveSupplementsRequest(
    val medicine_id: String? = null,
    val profile_id: String? = null,
    val action_id: String? = null,
    val recommendation_id: String? = null,
    val module: String? = null,
    val is_mock: Boolean? = null,
    val event_selection: String? = null,
    val occurrence_id: String? = null,
    val reminder_id: String? = null
)