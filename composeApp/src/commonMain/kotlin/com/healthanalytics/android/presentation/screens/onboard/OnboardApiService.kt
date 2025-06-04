package com.healthanalytics.android.presentation.screens.onboard

import com.healthanalytics.android.data.models.onboard.AuthResponse
import com.healthanalytics.android.data.models.onboard.OtpResponse
import com.healthanalytics.android.data.models.onboard.VerifyOtp


interface OnboardApiService {
    suspend fun sendOTP(phoneNumber: String): AuthResponse?
    suspend fun verifyOTP(verifyOtp: VerifyOtp): OtpResponse?
}