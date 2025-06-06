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

@Serializable
data class Address(
    val address_line_1: String,
    val address_line_2: String = "",
    val city: String,
    val state: String,
    val pincode: String,
    val country: String,
    val di_address_id: String? = null
)

@Serializable
data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val phone: String,
    val address: Address
)

@Serializable
data class UpdateProfileResponse(
    val status: String,
    val message: String,
    val data: User? = null
)