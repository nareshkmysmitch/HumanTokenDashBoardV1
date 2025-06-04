package com.healthanalytics.android.data.models.onboard

data class OtpResponse(
    val access_token: String,
    val ht_coupon: Any,
    val is_eligible: Boolean,
    val is_free_user: Boolean,
    val is_paid: Boolean,
    val is_verified: Boolean,
    val lead_id: String,
    val pii_user: PiiUser,
    val refresh_token: String
)

data class PiiUser(
    val _id: String,
    val billing_address: BillingAddress,
    val communication_address: CommunicationAddress,
    val country_code: String,
    val createdAt: String,
    val customer_id: String,
    val dob: String,
    val email: String,
    val gender: String,
    val height: Int,
    val is_customer: Boolean,
    val lead_id: String,
    val lead_source: String,
    val mobile: String,
    val name: String,
    val updatedAt: String,
    val weight: Int
)

data class BillingAddress(
    val _id: String,
    val address: String,
    val address_line_1: String,
    val city: String,
    val country: String,
    val pincode: String,
    val state: String
)

data class CommunicationAddress(
    val _id: String,
    val address: String,
    val address_line_1: String,
    val city: String,
    val country: String,
    val pincode: String,
    val state: String
)