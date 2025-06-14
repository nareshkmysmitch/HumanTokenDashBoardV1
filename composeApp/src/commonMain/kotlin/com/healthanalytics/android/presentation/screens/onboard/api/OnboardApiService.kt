package com.healthanalytics.android.presentation.screens.onboard.api

import com.healthanalytics.android.data.models.onboard.AccountCreation
import com.healthanalytics.android.data.models.onboard.AccountCreationResponse
import com.healthanalytics.android.data.models.onboard.AuthResponse
import com.healthanalytics.android.data.models.onboard.GenerateOrderId
import com.healthanalytics.android.data.models.onboard.GenerateOrderIdResponse
import com.healthanalytics.android.data.models.onboard.OtpResponse
import com.healthanalytics.android.data.models.onboard.PaymentRequest
import com.healthanalytics.android.data.models.onboard.SlotRequest
import com.healthanalytics.android.data.models.onboard.SlotsAvailability
import com.healthanalytics.android.data.models.onboard.UpdateSlot
import com.healthanalytics.android.data.models.onboard.VerifyOtp

interface OnboardApiService {
    suspend fun sendOTP(phoneNumber: String): AuthResponse?
    suspend fun verifyOTP(verifyOtp: VerifyOtp): OtpResponse?
    suspend fun createAccount(creation: AccountCreation): AccountCreationResponse?
    suspend fun getSlotsAvailability(slotRequest: SlotRequest): SlotsAvailability?
    suspend fun updateSlot(updateSlot: UpdateSlot): SlotsAvailability?
    suspend fun getPaymentStatus(paymentRequest: PaymentRequest): OtpResponse?
    suspend fun generateOrderId(generateOrderId: GenerateOrderId): GenerateOrderIdResponse?
}