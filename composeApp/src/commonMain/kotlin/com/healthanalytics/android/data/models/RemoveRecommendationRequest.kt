package com.healthanalytics.android.data.models

import kotlinx.serialization.Serializable

@Serializable
data class RemoveRecommendationRequest(
    val reminder_id: String,
    val occurrence_id: String,
    val event_selection: String? = null,
    val is_mock: Boolean = false,
    val module: String? = null,
    val recommendation_id: String,
    val action_id: String,
)

@Serializable
data class RemoveRecommendationResponse(
    val isDeleted: Boolean = false
)
