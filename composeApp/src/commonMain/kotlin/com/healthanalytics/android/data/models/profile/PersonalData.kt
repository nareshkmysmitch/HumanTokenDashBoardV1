package com.healthanalytics.android.data.models.profile

import kotlinx.serialization.Serializable

@Serializable
data class PersonalData(
    val pii_data: PiData? = null
)

@Serializable
data class PiData(
    val _id: String? = null,
    val dob: String? = null,
    val email: String? = null,
    var height: Double? = null,
    val lead_id: String? = null,
    val name: String? = null,
    var weight: Double? = null
)