package com.healthanalytics.android.data.api

import com.healthanalytics.android.data.models.Recommendation
import com.healthanalytics.android.data.models.Recommendations
import com.healthanalytics.android.data.models.RemoveRecommendationRequest
import com.healthanalytics.android.data.models.RemoveRecommendationResponse
import com.healthanalytics.android.data.models.RemoveSupplementsRequest
import com.healthanalytics.android.utils.EncryptionUtils
import com.healthanalytics.android.utils.EncryptionUtils.toEncryptedRequestBody
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.Serializable

interface ApiService {
    suspend fun getProducts(accessToken: String): List<Product?>?
    suspend fun getHealthMetrics(accessToken: String): List<BloodData?>?
    suspend fun getRecommendations(accessToken: String): List<Recommendation>?
    suspend fun removeRecommendation(
        accessToken: String,
        request: RemoveRecommendationRequest,
    ): Boolean?

    suspend fun removeSupplements(
        accessToken: String,
        request: RemoveSupplementsRequest,
    ): Boolean?

    suspend fun addSupplementToPlan(accessToken: String, request: AddSupplementRequest): Boolean
    suspend fun addActivityToPlan(accessToken: String, request: AddActivityRequest): Boolean
}

@Serializable
data class AddSupplementRequest(
    val type: String = "medicine",
    val sub_type: String = "supplement",
    val title: String,
    val frequency: String = "daily",
    val scheduled_time: String = "20:00",
    val days_of_the_week: List<Int> = listOf(0, 1, 2, 3, 4, 5, 6),
    val is_mock: Boolean = false,
    val module: String = "recommendation",
    val recommendation_id: String,
    val action_id: String,
    val profile_id: String = "65",
    val name: String,
    val shape: String = "round",
    val color: String = "#000000",
    val time: List<String> = listOf("20:00"),
    val duration: Int = 90
)

@Serializable
data class AddActivityRequest(
    val type: String = "activity",
    val sub_type: String,
    val title: String,
    val frequency: String = "daily",
    val scheduled_time: String = "07:00",
    val days_of_the_week: List<Int> = listOf(0, 1, 2, 3, 4, 5, 6),
    val is_mock: Boolean = false,
    val module: String = "recommendation",
    val recommendation_id: String,
    val action_id: String
)

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
        val response = httpClient.post("v1/user/reminder/delete") {
            header("access_token", accessToken)
            setBody(request.toEncryptedRequestBody())
        }
        val responseBody = response.bodyAsText()
        val result =
            EncryptionUtils.handleDecryptionResponse<RemoveRecommendationResponse>(responseBody)
        return result?.isDeleted
    }

    override suspend fun removeSupplements(
        accessToken: String,
        request: RemoveSupplementsRequest,
    ): Boolean? {
        val response = httpClient.post("v1/medicine/delete") {
            header("access_token", accessToken)
            setBody(request.toEncryptedRequestBody())
        }
        val responseBody = response.bodyAsText()
        val result =
            EncryptionUtils.handleDecryptionResponse<RemoveRecommendationResponse>(responseBody)
        return result?.isDeleted
    }

    override suspend fun addSupplementToPlan(accessToken: String, request: AddSupplementRequest): Boolean {
        val response = httpClient.post("v1/medicine/add") {
            header("access_token", accessToken)
            setBody(request)
        }
        val responseBody = response.bodyAsText()
        val result = EncryptionUtils.handleDecryptionResponse<ApiResult>(responseBody)
        return result?.status == "success"
    }

    override suspend fun addActivityToPlan(accessToken: String, request: AddActivityRequest): Boolean {
        val response = httpClient.post("v1/user/reminder/create") {
            header("access_token", accessToken)
            setBody(request)
        }
        val responseBody = response.bodyAsText()
        val result = EncryptionUtils.handleDecryptionResponse<ApiResult>(responseBody)
        return result?.status == "success"
    }
} 