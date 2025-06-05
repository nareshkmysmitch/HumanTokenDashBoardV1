package com.healthanalytics.android.data.api

import com.example.humantoken.ui.screens.Cart
import com.example.humantoken.ui.screens.EncryptedResponse
import com.healthanalytics.android.utils.EncryptionUtils
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

interface ApiService {
    suspend fun getProducts(accessToken: String): List<Product?>?
    suspend fun getHealthMetrics(accessToken: String): List<BloodData?>?
    suspend fun getCartList(accessToken: String): List<Cart?>?
}

class ApiServiceImpl(
    private val httpClient: HttpClient,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : ApiService {
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
        val healthMetricsResponse = EncryptionUtils.handleDecryptionResponse<HealthMetrics>(responseBody)
        return healthMetricsResponse?.blood?.data
    }

    override suspend fun getCartList(accessToken: String): List<Cart?>? {
        val response = httpClient.get("v4/human-token/market-place/cart") {
            header("access_token", accessToken)
        }
        val responseBody = response.bodyAsText()
        println("Cart response --> Raw ${responseBody}")
        
        try {
            // First parse the encrypted response
            val encryptedResponse = json.decodeFromString<EncryptedResponse>(responseBody)
            
            // Use handleDecryptionResponse to decrypt the data array
            val cartList = EncryptionUtils.handleDecryptionResponse<List<Cart>>(
                """{"status":"${encryptedResponse.status}","message":"${encryptedResponse.message}","data":"${encryptedResponse.data}"}"""
            )
            
            println("Cart response --> Decrypted ${cartList}")
            return cartList
        } catch (e: Exception) {
            println("Error handling cart response: ${e.message}")
            e.printStackTrace()
            return null
        }
    }
} 