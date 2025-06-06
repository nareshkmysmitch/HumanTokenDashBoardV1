package com.healthanalytics.android.data.models.onboard

import kotlinx.serialization.Serializable

@Serializable
data class SendOtp(
    val mobile: String,
    val country_code: String,
)

@Serializable
data class VerifyOtp(
    val mh: String,
    val country_code: String,
    val otp: String,
)

@Serializable
data class AuthResponse(
    val mh: String? = null
)