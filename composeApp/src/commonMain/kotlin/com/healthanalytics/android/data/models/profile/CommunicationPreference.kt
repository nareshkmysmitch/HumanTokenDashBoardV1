package com.healthanalytics.android.data.models.profile

import kotlinx.serialization.Serializable

@Serializable
data class CommunicationPreference(
    val preference: Preference? = Preference(),
)

@Serializable
data class Preference(
    val communication_preference: String? = null,
)