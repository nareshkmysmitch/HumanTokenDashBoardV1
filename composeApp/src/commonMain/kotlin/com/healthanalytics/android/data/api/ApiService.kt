package com.healthanalytics.android.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

interface ApiService {
    suspend fun getProducts(accessToken: String): List<Product?>?
}

class ApiServiceImpl(private val httpClient: HttpClient) : ApiService {
    override suspend fun getProducts(accessToken: String): List<Product?>? {
        val response = httpClient.get("v4/human-token/market-place/products") {
            header("access_token", accessToken)
        }.body<ProductResponse>()

        println("--> Response :${response.data}")
        println("--> Response :${response.status}")
        println("--> Response :${response.message}")

        return response.data?.products
    }
} 