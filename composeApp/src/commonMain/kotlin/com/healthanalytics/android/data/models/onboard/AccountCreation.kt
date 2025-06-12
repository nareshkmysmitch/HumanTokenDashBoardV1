package com.healthanalytics.android.data.models.onboard

import kotlinx.datetime.LocalDate
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
    val country_code: String,
    val communication_address: CommunicationAddress,
)

@Serializable
data class AccountCreationResponse(
    val lead_id: String? = null
)

@Serializable
data class AccountDetails(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val dob: LocalDate? = null,
    val gender: String = "",
    val weight: String = "",
    val height: String = ""
)

