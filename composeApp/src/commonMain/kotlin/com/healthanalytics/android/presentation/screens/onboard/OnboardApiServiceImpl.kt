package com.healthanalytics.android.presentation.screens.onboard

import com.healthanalytics.android.data.models.onboard.AuthResponse
import com.healthanalytics.android.data.models.onboard.OtpResponse
import com.healthanalytics.android.data.models.onboard.SendOtp
import com.healthanalytics.android.data.models.onboard.VerifyOtp
import com.healthanalytics.android.utils.EncryptionUtils
import com.healthanalytics.android.utils.EncryptionUtils.toEncryptedRequestBody
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.utils.io.InternalAPI

class OnboardApiServiceImpl(val httpClient: HttpClient) : OnboardApiService {
    @OptIn(InternalAPI::class)
    override suspend fun sendOTP(phoneNumber: String): AuthResponse? {

        val response = httpClient.post("v4/human-token/lead/send-otp") {
            setBody(
                SendOtp(
                    mobile = phoneNumber,
                    country_code = "91"
                ).toEncryptedRequestBody()
            )
        }

        val responseBody = response.bodyAsText()
        val authResponse = EncryptionUtils.handleDecryptionResponse<AuthResponse>(responseBody)

        println("response.....${response}")
        println("responseBody.....${responseBody}")
        println("authResponse.....${authResponse}")

        return authResponse
    }

    override suspend fun verifyOTP(verifyOtp: VerifyOtp): OtpResponse? {
        val response = httpClient.post("v4/human-token/lead/verify-otp") {
            setBody(
                verifyOtp.toEncryptedRequestBody()
            )
        }

        val responseBody = response.bodyAsText()
        val authResponse = EncryptionUtils.handleDecryptionResponse<OtpResponse>(responseBody)

        println("response.....${response}")
        println("responseBody.....${responseBody}")
        println("authResponse.....${authResponse}")
        return authResponse
    }
}