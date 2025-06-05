package com.healthanalytics.android.data.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkConfig {

    private const val BASE_URL = "https://api.stg.dh.deepholistics.com/"
    private const val CLIENT_ID = "JmEfoQ2sP18APIiX9z0nY3vlDAKHIp8nKuyV"
    private const val USER_TIMEZONE = "Asia/Calcutta"

    fun createHttpClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(Logging) {
                level = LogLevel.ALL
            }

            defaultRequest {
                url(BASE_URL)
                contentType(ContentType.Application.Json)
                header("client_id", CLIENT_ID)
                header("user_timezone", USER_TIMEZONE)
            }
        }
    }
} 