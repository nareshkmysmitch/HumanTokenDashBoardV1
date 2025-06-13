package com.healthanalytics.android.presentation.screens.onboard.api

import com.healthanalytics.android.data.models.onboard.AccountCreation
import com.healthanalytics.android.data.models.onboard.AccountCreationResponse
import com.healthanalytics.android.data.models.onboard.AuthResponse
import com.healthanalytics.android.data.models.onboard.GenerateOrderId
import com.healthanalytics.android.data.models.onboard.GenerateOrderIdResponse
import com.healthanalytics.android.data.models.onboard.OtpResponse
import com.healthanalytics.android.data.models.onboard.PaymentRequest
import com.healthanalytics.android.data.models.onboard.SendOtp
import com.healthanalytics.android.data.models.onboard.SlotRequest
import com.healthanalytics.android.data.models.onboard.SlotsAvailability
import com.healthanalytics.android.data.models.onboard.UpdateSlot
import com.healthanalytics.android.data.models.onboard.VerifyOtp
import com.healthanalytics.android.utils.EncryptionUtils
import com.healthanalytics.android.utils.EncryptionUtils.toEncryptedRequestBody
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText

class OnboardApiServiceImpl(val httpClient: HttpClient) : OnboardApiService {

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

        return authResponse
    }

    override suspend fun createAccount(creation: AccountCreation): AccountCreationResponse? {
        val response = httpClient.post("v4/human-token/lead") {
            setBody(creation)
        }

        val responseBody = response.bodyAsText()
        val authResponse =
            EncryptionUtils.handleDecryptionResponse<AccountCreationResponse>(responseBody)

        return authResponse
    }

    override suspend fun getSlotsAvailability(slotRequest: SlotRequest): SlotsAvailability? {
        val response = httpClient.post("v3/diagnostics/slots-availability?platform=web") {
            setBody(slotRequest)
        }
        val responseBody = response.bodyAsText()
        val slotAvailability =
            EncryptionUtils.handleDecryptionResponse<SlotsAvailability>(responseBody)
        return slotAvailability
    }

    override suspend fun updateSlot(updateSlot: UpdateSlot): SlotsAvailability? {
        val response = httpClient.post("v3/diagnostics/appointment-slot/update?platform=web") {
            setBody(updateSlot.toEncryptedRequestBody())
        }

        val responseBody = response.bodyAsText()
        val slotUpdated = EncryptionUtils.handleDecryptionResponse<SlotsAvailability>(responseBody)

        return slotUpdated
    }

    override suspend fun generateOrderId(generateOrderId: GenerateOrderId): GenerateOrderIdResponse? {
        val response = httpClient.post("/v4/human-token/payments/orders") {
            setBody(generateOrderId.toEncryptedRequestBody())
        }

        val responseBody = response.bodyAsText()
        val authResponse = EncryptionUtils.handleDecryptionResponse<GenerateOrderIdResponse>(responseBody)

        return authResponse
    }

    override suspend fun getPaymentStatus(paymentRequest: PaymentRequest): OtpResponse? {
        val response = httpClient.get("v3/human-token/payments/orders") {
            url {
                parameters.append("payment_order_id",paymentRequest.payment_order_id)
            }
        }

        val responseBody = response.bodyAsText()
        val authResponse = EncryptionUtils.handleDecryptionResponse<OtpResponse>(responseBody)

        return authResponse
    }
}