package com.healthanalytics.android.data.api

import com.healthanalytics.android.utils.EncryptionUtils
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText

interface ApiService {
    suspend fun getProducts(accessToken: String): List<Product?>?
}

class ApiServiceImpl(private val httpClient: HttpClient) : ApiService {
    override suspend fun getProducts(accessToken: String): List<Product?>? {
        val response = httpClient.get("v4/human-token/market-place/products") {
            header("access_token", accessToken)
        }
        val responseBody = response.bodyAsText()
        println("responseBody --> Raw ${responseBody}")
        val productsList = EncryptionUtils.handleEncryptedResponse<ProductData>(responseBody)
        println("responseBody --> ProductsList ${productsList}")
        return productsList?.products
    }
} 