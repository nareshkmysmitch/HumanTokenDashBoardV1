package com.healthanalytics.android.data.models.profile

import kotlinx.serialization.Serializable

@Serializable
data class UploadCommunicationPreference(
    val communication_preference: String? = null,
    val fields: List<String?>? = null
)