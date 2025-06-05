package com.healthanalytics.android.data.models.onboard

import kotlinx.serialization.Serializable

@Serializable
data class AccountCreation(
    val mobile: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val gender: String,
    val height: String,
    val weight: String,
    val communication_address: CommunicationAddress,
)

