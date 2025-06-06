package com.healthanalytics.android.data.models.onboard

import kotlinx.serialization.Serializable

@Serializable
data class OtpResponse(
    val access_token: String?=null,
    val ht_coupon: String?=null,
    val is_eligible: Boolean?=null,
    val is_free_user: Boolean?=null,
    val is_paid: Boolean?=null,
    val is_verified: Boolean?=null,
    val lead_id: String?=null,
    val pii_user: PiiUser?=null,
    val refresh_token: String?=null
)

@Serializable
data class PiiUser(
    val _id: String?=null,
    val billing_address: BillingAddress?=null,
    val communication_address: CommunicationAddress?=null,
    val country_code: String?=null,
    val createdAt: String?=null,
    val customer_id: String?=null,
    val dob: String?=null,
    val email: String?=null,
    val gender: String?=null,
    val height: Int?=null,
    val is_customer: Boolean?=null,
    val lead_id: String?=null,
    val lead_source: String?=null,
    val mobile: String?=null,
    val name: String?=null,
    val updatedAt: String?=null,
    val weight: Int?=null
)

@Serializable
data class BillingAddress(
    val _id: String?=null,
    val address: String?=null,
    val address_line_1: String?=null,
    val city: String?=null,
    val country: String?=null,
    val pincode: String?=null,
    val state: String?=null
)

@Serializable
data class CommunicationAddress(
    val _id: String?=null,
    val address: String?=null,
    val address_line_1: String?=null,
    val address_line_2: String?=null,
    val city: String?=null,
    val country: String?=null,
    val pincode: String?=null,
    val state: String?=null,
)