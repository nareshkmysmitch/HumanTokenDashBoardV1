package com.healthanalytics.android.presentation.screens.dashboard

import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.data.api.ProductData
import com.healthanalytics.android.utils.EncryptionUtils
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText


interface DashboardApiService {
    suspend fun getDashboard(accessToken: String): List<Product?>?
}
