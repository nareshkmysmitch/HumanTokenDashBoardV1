package com.healthanalytics.android.data.api

import com.healthanalytics.android.utils.EncryptionUtils
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText

interface ApiService {
    suspend fun getProducts(accessToken: String): List<Product?>?
    suspend fun getHealthMetrics(accessToken: String): List<HealthMetric>?
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

    override suspend fun getHealthMetrics(accessToken: String): List<HealthMetric>? {
        val response = httpClient.get("v4/health/metrics") {
            header("access_token", accessToken)
        }
        val responseBody = response.bodyAsText()
        val healthMetricsResponse = EncryptionUtils.handleDecryptionResponse<HealthMetricsResponse>(responseBody)
        return healthMetricsResponse?.data
    }
} 