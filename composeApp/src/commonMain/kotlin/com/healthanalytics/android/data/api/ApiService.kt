package com.healthanalytics.android.data.api

import com.example.humantoken.ui.screens.Cart
import com.example.humantoken.ui.screens.EncryptedResponse
import com.healthanalytics.android.data.models.AddActivityRequest
import com.healthanalytics.android.data.models.AddActivityResponse
import com.healthanalytics.android.data.models.AddSupplementRequest
import com.healthanalytics.android.data.models.AddressData
import com.healthanalytics.android.data.models.ProfileUpdateResponse
import com.healthanalytics.android.data.models.Recommendation
import com.healthanalytics.android.data.models.Recommendations
import com.healthanalytics.android.data.models.RemoveRecommendationRequest
import com.healthanalytics.android.data.models.RemoveRecommendationResponse
import com.healthanalytics.android.data.models.RemoveSupplementsRequest
import com.healthanalytics.android.data.models.UpdateProfileRequest
import com.healthanalytics.android.utils.EncryptionUtils
import com.healthanalytics.android.utils.EncryptionUtils.toEncryptedRequestBody
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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

    suspend fun addSupplementToPlan(
        accessToken: String,
        request: AddSupplementRequest,
    ): Boolean?

    suspend fun addActivityToPlan(
        accessToken: String,
        request: AddActivityRequest,
    ): Boolean?

    suspend fun updateProfile(
        accessToken: String, request: UpdateProfileRequest
    ): ProfileUpdateResponse?

    suspend fun getAddresses(accessToken: String): AddressData?
    suspend fun addProduct(
        accessToken: String, productId: String, variantId: String
    ): EncryptedResponse?

    suspend fun updateProduct(
        accessToken: String, productId: String, quantity: String
    ): EncryptedResponse?

    suspend fun getCartList(accessToken: String): List<Cart?>?
    suspend fun getProductDetails(accessToken: String, productId: String): Product?
    suspend fun logout(accessToken: String): Boolean
    suspend fun getTestBookings(accessToken: String): List<Product?>?
    suspend fun getBiomarkerReport(
        accessToken: String, type: String, metricId: String
    ): BiomarkerReportData?
}


class ApiServiceImpl(
    private val httpClient: HttpClient, private val json: Json = Json { ignoreUnknownKeys = true }
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
        return healthMetricsResponse?.blood?.bloodData
    }

    override suspend fun updateProfile(
        accessToken: String, request: UpdateProfileRequest
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
        val addressListResponse =
            EncryptionUtils.handleDecryptionResponse<AddressData>(responseBody)
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

    override suspend fun addProduct(
        accessToken: String, productId: String, variantId: String
    ): EncryptedResponse? {
        println("Adding product: $productId, variantId: $variantId")

        val requestObject = buildJsonObject {
            put("product_id", productId)
            if(variantId.isNotEmpty()) {
                put("variant_id", variantId)
            }
        }

        val response = httpClient.post("v4/human-token/market-place/cart/add") {
            header("access_token", accessToken)
            setBody(requestObject.toEncryptedRequestBody())
        }
        println("response --> $response")
        val responseBody = response.bodyAsText()
        println("Add product response: $responseBody")
        return try {
            val encryptedResponse = json.decodeFromString<EncryptedResponse>(responseBody)
            encryptedResponse
        } catch (e: Exception) {
            println("Error handling add product response: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    override suspend fun updateProduct(
        accessToken: String, productId: String, quantity: String
    ): EncryptedResponse? {
        println("Updating product: $productId, quantity: $quantity")
        val requestObject = buildJsonObject {
            put("product_id", productId)
            put("quantity", quantity)
        }

        val response = httpClient.put("v4/human-token/market-place/cart/update") {
            header("access_token", accessToken)
            setBody(requestObject.toEncryptedRequestBody())
        }
        val responseBody = response.bodyAsText()
        println("Update product response: $responseBody")
        return try {
            val encryptedResponse = json.decodeFromString<EncryptedResponse>(responseBody)
            encryptedResponse
        } catch (e: Exception) {
            println("Error handling update product response: ${e.message}")
            e.printStackTrace()
            null
        }
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

    override suspend fun getProductDetails(accessToken: String, productId: String): Product? {
        val response = httpClient.get("v4/human-token/market-place/product/$productId") {
            header("access_token", accessToken)
        }
        val responseBody = response.bodyAsText()
        println("Product details response --> Raw ${responseBody}")

        try {
            val encryptedResponse = json.decodeFromString<EncryptedResponse>(responseBody)
            val productResponse = EncryptionUtils.handleDecryptionResponse<Product>(
                """{"status":"${encryptedResponse.status}","message":"${encryptedResponse.message}","data":"${encryptedResponse.data}"}"""
            )
            println("Product response --> Decrypted ${productResponse}")
            return productResponse
        } catch (e: Exception) {
            println("Error handling product details response: ${e.message}")
            e.printStackTrace()
            return null
        }
    }

    override suspend fun logout(accessToken: String): Boolean {
        return try {
            val response = httpClient.post("v1/user/logout") {
                header("access_token", accessToken)
            }
            response.status.value in 200..299
        } catch (e: Exception) {
            println("Error during logout: ${e.message}")
            false
        }
    }

    override suspend fun getTestBookings(accessToken: String): List<Product?>? {
        val response = httpClient.get("v4/human-token/market-place/products") {
            header("access_token", accessToken)
            parameter("category", "gene,gut,blood")
            parameter("limit", "6")
        }
        val responseBody = response.bodyAsText()
        println("Test Booking response --> Raw ${responseBody}")

        try {
            val encryptedResponse = json.decodeFromString<EncryptedResponse>(responseBody)
            val productResponse = EncryptionUtils.handleDecryptionResponse<ProductData>(
                """{"status":"${encryptedResponse.status}","message":"${encryptedResponse.message}","data":"${encryptedResponse.data}"}"""
            )
            println("Product response --> Decrypted ${productResponse}")
            return productResponse?.products
        } catch (e: Exception) {
            println("Error handling test booking response: ${e.message}")
            e.printStackTrace()
            return null
        }
    }

    override suspend fun getBiomarkerReport(
        accessToken: String, type: String, metricId: String
    ): BiomarkerReportData? {
        val response = httpClient.get("v4/human-token/health-data/metric") {
            header("access_token", accessToken)
            //   header("client_id", "qXsGPcHJkb9MTwD5fNFpzRrngjtvy4dW")
            header("user_timezone", "Asia/Calcutta")
            url {
                parameters.append("type", type)
                parameters.append("metric_id", metricId)
            }
        }
        val responseBody = response.bodyAsText()
        println()
        val reportResponse =
            EncryptionUtils.handleDecryptionResponse<BiomarkerReportData>(responseBody)
        return reportResponse
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

    override suspend fun addSupplementToPlan(
        accessToken: String,
        request: AddSupplementRequest,
    ): Boolean {
        val response = httpClient.post("v1/medicine/add") {
            header("access_token", accessToken)
            setBody(request.toEncryptedRequestBody())
        }
        val responseBody = response.bodyAsText()
        val result = EncryptionUtils.handleDecryptionResponse<AddActivityResponse>(responseBody)
        return result != null
    }

    override suspend fun addActivityToPlan(
        accessToken: String,
        request: AddActivityRequest,
    ): Boolean {
        val response = httpClient.post("v1/user/reminder/create") {
            header("access_token", accessToken)
            setBody(request.toEncryptedRequestBody())
        }
        val responseBody = response.bodyAsText()
        val result = EncryptionUtils.handleDecryptionResponse<AddActivityResponse>(responseBody)
        return result != null

    }
}
