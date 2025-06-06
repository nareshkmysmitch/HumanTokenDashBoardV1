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
    val address: String,
    val pincode: String,
    val address_line_1: String,
    val address_line_2: String? = null,
    val city: String,
    val state: String,
    val country: String,
    val address_type: String? = null
)

@Serializable
data class AddressItem(
    val address_id: String,
    val address: Address
)

@Serializable
data class AddressData(
    val address_list: List<AddressItem>
)

@Serializable
data class AddressListResponse(
    val status: String,
    val message: String,
    val data: AddressData
)

@Serializable
data class UpdateAddressListResponse(
    val address_line_1: String? = null,
    val address_line_2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    val country: String? = null,
    val di_address_id: String? = null
)

@Serializable
data class ProfileUpdateResponse(
    val message: String,
    val updated_fields: List<String>,
    val data: String
)

@Serializable
data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val phone: String,
    val address: UpdateAddressListResponse,
    val di_address_id: String,
)

@Serializable
data class UpdateProfileResponse(
    val status: String,
    val message: String,
    val data: User? = null
)