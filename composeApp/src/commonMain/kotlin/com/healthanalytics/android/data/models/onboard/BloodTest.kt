package com.healthanalytics.android.data.models.onboard

import kotlinx.serialization.Serializable

@Serializable
data class SlotRequest(
    val date: String,
    val user_timezone: String,
    val lead_id: String
)

@Serializable
data class SlotsAvailability(
    val slots: List<Slot>? = null
)

@Serializable
data class Slot(
    val start_time: String? = null,
    val end_time: String? = null
)

@Serializable
data class UpdateSlot(
    val appointment_date: String,
    val source: String,
    val lead_id: String,
)