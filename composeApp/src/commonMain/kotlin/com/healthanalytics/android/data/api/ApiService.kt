package com.healthanalytics.android.data.api

import com.healthanalytics.android.data.models.Recommendation
import com.healthanalytics.android.data.models.Recommendations
import com.healthanalytics.android.data.models.RemoveRecommendationRequest
import com.healthanalytics.android.data.models.RemoveRecommendationResponse
import com.healthanalytics.android.utils.EncryptionUtils
import com.healthanalytics.android.utils.EncryptionUtils.toEncryptedRequestBody
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText

interface ApiService {
    suspend fun getProducts(accessToken: String): List<Product?>?
    suspend fun getHealthMetrics(accessToken: String): List<BloodData?>?
    suspend fun getRecommendations(accessToken: String): List<Recommendation>?
    suspend fun removeRecommendation(
        accessToken: String,
        request: RemoveRecommendationRequest,
    ): Boolean?
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

    override suspend fun getRecommendations(accessToken: String): List<Recommendation>? {
        val response = httpClient.get("v4/human-token/recommendation") {
            header("access_token", accessToken)
        }
        val responseBody = response.bodyAsText()
        val recommendationsList =
            EncryptionUtils.handleDecryptionResponse<Recommendations>(responseBody)
        return recommendationsList?.recommendations
    }

    override suspend fun removeRecommendation(
        accessToken: String,
        request: RemoveRecommendationRequest,
    ): Boolean? {
        val encrypted = request.toEncryptedRequestBody()
        val response = httpClient.post("v1/user/reminder/delete") {
            header("access_token", accessToken)
            setBody(request.toEncryptedRequestBody())
        }
        println("$encrypted")
        val responseBody = response.bodyAsText()
        val result =
            EncryptionUtils.handleDecryptionResponse<RemoveRecommendationResponse>(responseBody)
        return result?.isDeleted
    }
} 