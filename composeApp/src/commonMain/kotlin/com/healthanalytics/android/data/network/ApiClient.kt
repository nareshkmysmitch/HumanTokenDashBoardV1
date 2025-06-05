package com.healthanalytics.android.data.network

import com.healthanalytics.android.data.models.ApiResponse
import com.healthanalytics.android.data.models.AuthRequest
import com.healthanalytics.android.data.models.AuthResponse
import com.healthanalytics.android.data.models.Biomarker
import com.healthanalytics.android.data.models.CartItem
import com.healthanalytics.android.data.models.OtpVerifyRequest
import com.healthanalytics.android.data.models.Product
import com.healthanalytics.android.data.network.NetworkConfig.CLIENT_ID
import com.healthanalytics.android.data.network.NetworkConfig.USER_TIMEZONE

import com.healthanalytics.android.utils.EncryptionUtils.toEncryptedRequestBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println(message)
                }
            }
            level = LogLevel.ALL
        }

        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
        }

        defaultRequest {
            url(NetworkConfig.BASE_URL)
            contentType(ContentType.Application.Json)
            header("client_id", CLIENT_ID)
            header("user_timezone", USER_TIMEZONE)
        }
    }

    companion object {
        private const val BASE_URL = "https://api.stg.dh.deepholistics.com"
    }

    suspend fun sendOtp(phone: String): AuthResponse {
        return client.post("$BASE_URL/v4/human-token/lead/send-otp") {
            setBody(AuthRequest(phone).toEncryptedRequestBody())
        }.body()
    }

    suspend fun verifyOtp(phone: String, otp: String): AuthResponse {
        return client.post("$BASE_URL/v4/human-token/lead/verify-otp") {
            setBody(OtpVerifyRequest(phone, otp).toEncryptedRequestBody())
        }.body()
    }

    suspend fun getHealthData(token: String): ApiResponse<List<Biomarker>> {
        return client.get("$BASE_URL/v4/human-token/health-data") {
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun getProducts(token: String): ApiResponse<List<Product>> {
        return client.get("$BASE_URL/v4/human-token/market-place/products") {
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun getCartItems(token: String): ApiResponse<List<CartItem>> {
        return client.get("$BASE_URL/v4/human-token/cart-items") {
            header("Authorization", "Bearer $token")
        }.body()
    }
}