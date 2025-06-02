package com.healthanalytics.android.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val phone: String,
    val name: String? = null,
    val email: String? = null,
    val isVerified: Boolean = false
)

@Serializable
data class AuthRequest(
    val phone: String
)

@Serializable
data class OtpVerifyRequest(
    val phone: String,
    val otp: String
)

@Serializable
data class AuthResponse(
    val status: String,
    val message: String,
    val data: AuthData? = null
)

@Serializable
data class AuthData(
    val access_token: String,
    val user: User
)