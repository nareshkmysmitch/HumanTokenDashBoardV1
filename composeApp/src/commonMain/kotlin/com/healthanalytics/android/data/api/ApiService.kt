package com.healthanalytics.android.data.api

import com.example.humantoken.ui.screens.Cart
import com.example.humantoken.ui.screens.EncryptedResponse
import com.healthanalytics.android.utils.EncryptionUtils
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class AddToCartRequest(
    val product_id: String,
    val variant_id: String? = null,
    val quantity: Int = 1,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class UpdateCartRequest(
    val product_id: String,
    val quantity: String
)

interface ApiService {
    suspend fun getProducts(accessToken: String): List<Product?>?
    suspend fun getHealthMetrics(accessToken: String): List<BloodData?>?
    suspend fun addProduct(accessToken: String, productId: String, variantId: String): EncryptedResponse?
    suspend fun updateProduct(accessToken: String, productId: String, quantity: String): EncryptedResponse?
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
        val healthMetricsResponse =
            EncryptionUtils.handleDecryptionResponse<HealthMetrics>(responseBody)
        return healthMetricsResponse?.blood?.data
    }

    override suspend fun addProduct(
        accessToken: String,
        productId: String,
        variantId: String
    ): EncryptedResponse? {
        println("Adding product: $productId, variantId: $variantId")
        val response = httpClient.post("v4/human-token/market-place/cart/add") {
            header("access_token", accessToken)
            contentType(ContentType.Application.Json)
            setBody(AddToCartRequest(
                product_id = productId,
                variant_id = if (variantId.toInt() > 0) variantId.toString() else null
            ))
        }
        val responseBody = response.bodyAsText()
        println("Add product response: $responseBody")
        return EncryptionUtils.handleDecryptionResponse<EncryptedResponse>(responseBody)
    }

    override suspend fun updateProduct(
        accessToken: String,
        productId: String,
        quantity: String
    ): EncryptedResponse? {
        println("Updating product: $productId, quantity: $quantity")
        val response = httpClient.put("v4/human-token/market-place/cart/update") {
            header("access_token", accessToken)
            contentType(ContentType.Application.Json)
            setBody(UpdateCartRequest(productId, quantity))
        }
        val responseBody = response.bodyAsText()
        println("Update product response: $responseBody")
        return EncryptionUtils.handleDecryptionResponse<EncryptedResponse>(responseBody)
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