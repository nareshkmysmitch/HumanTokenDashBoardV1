package com.healthanalytics.android.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AddSupplementRequest(
    val type: String,
    val sub_type: String,
    val title: String,
    val frequency: String,
    val scheduled_time: String,
    val days_of_the_week: List<Int>,
    val is_mock: Boolean = false,
    val module: String,
    val recommendation_id: String,
    val action_id: String,
    val profile_id: String,
    val name: String,
    val shape: String,
    val color: String,
    val time: List<String>,
    val duration: Int,
)