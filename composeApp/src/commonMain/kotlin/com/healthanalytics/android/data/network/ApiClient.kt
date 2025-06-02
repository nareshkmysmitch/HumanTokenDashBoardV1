package com.healthanalytics.android.data.network

import com.healthanalytics.android.data.models.ApiResponse
import com.healthanalytics.android.data.models.AuthRequest
import com.healthanalytics.android.data.models.AuthResponse
import com.healthanalytics.android.data.models.Biomarker
import com.healthanalytics.android.data.models.CartItem
import com.healthanalytics.android.data.models.OtpVerifyRequest
import com.healthanalytics.android.data.models.Product
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
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
            level = LogLevel.INFO
        }
        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
        }
    }

    companion object {
        private const val BASE_URL =
            "https://api.stg.dh.deepholistics.com"
    }

    suspend fun sendOtp(phone: String): AuthResponse {
        return client.post("$BASE_URL/v4/human-token/lead/send-otp") {
            setBody(AuthRequest(phone))
        }.body()
    }

    suspend fun verifyOtp(phone: String, otp: String): AuthResponse {
        return client.post("$BASE_URL/v4/human-token/lead/verify-otp") {
            setBody(OtpVerifyRequest(phone, otp))
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