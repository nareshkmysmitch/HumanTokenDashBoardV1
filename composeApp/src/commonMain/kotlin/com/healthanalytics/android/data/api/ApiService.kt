package com.healthanalytics.android.data.api

import com.healthanalytics.android.data.models.Recommendation
import com.healthanalytics.android.data.models.Recommendations
import com.healthanalytics.android.data.models.AddressData
import com.healthanalytics.android.data.models.ProfileUpdateResponse
import com.healthanalytics.android.utils.EncryptionUtils
import com.healthanalytics.android.data.models.UpdateProfileRequest
import com.healthanalytics.android.data.models.UpdateProfileResponse
import com.healthanalytics.android.utils.EncryptionUtils.toEncryptedRequestBody
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface ApiService {
    suspend fun getProducts(accessToken: String): List<Product?>?
    suspend fun getHealthMetrics(accessToken: String): List<BloodData?>?
    suspend fun getRecommendations(accessToken: String): List<Recommendation>?
    suspend fun updateProfile(accessToken: String, request: UpdateProfileRequest): ProfileUpdateResponse?
    suspend fun getAddresses(accessToken: String): AddressData?
}

class ApiServiceImpl(private val httpClient: HttpClient) : ApiService {
    override suspend fun getProducts(accessToken: String): List<Product?>? {
        val response = httpClient.get("v4/human-token/market-place/products") {
            header("access_token", accessToken)
        }
        val responseBody = response.bodyAsText()
        println("responseBody --> Raw ${responseBody}")
        val productsList = EncryptionUtils.handleDecryptionResponse<ProductData>(responseBody)
        println("responseBody --> ProductsList ${productsList}")
        return productsList?.products
    }

    override suspend fun getHealthMetrics(accessToken: String): List<BloodData?>? {
        val response = httpClient.get("v4/human-token/health-data") {
            header("access_token", accessToken)
        }
        val responseBody = response.bodyAsText()
        val healthMetricsResponse =
            EncryptionUtils.handleDecryptionResponse<HealthMetrics>(responseBody)
        return healthMetricsResponse?.blood?.data
    }

    override suspend fun updateProfile(
        accessToken: String,
        request: UpdateProfileRequest
    ): ProfileUpdateResponse? {
        val response = httpClient.put("v4/human-token/lead/update-profile") {
            header("access_token", accessToken)
            contentType(ContentType.Application.Json)
            setBody(request.toEncryptedRequestBody())
        }
        val responseBody = response.bodyAsText()
        println("responseBody --> Raw ${responseBody}")
        return Json.decodeFromString<ProfileUpdateResponse>(responseBody)
    }

    override suspend fun getAddresses(accessToken: String): AddressData? {
        val response = httpClient.get("v4/human-token/market-place/address") {
            header("access_token", accessToken)
        }
        val responseBody = response.bodyAsText()
        println("Address response --> Raw ${responseBody}")
        val addressListResponse = EncryptionUtils.handleDecryptionResponse<AddressData>(responseBody)
        if (addressListResponse != null) {
            return addressListResponse
        }
        return null
    }

    override suspend fun getRecommendations(accessToken: String): List<Recommendation>? {
        val response = httpClient.get("v4/human-token/recommendation") {
            header("access_token", accessToken)
        }
        val responseBody = response.bodyAsText()
        val recommendationsList =
            EncryptionUtils.handleDecryptionResponse<Recommendations>(responseBody)
        return recommendationsList?.recommendations
    }
}