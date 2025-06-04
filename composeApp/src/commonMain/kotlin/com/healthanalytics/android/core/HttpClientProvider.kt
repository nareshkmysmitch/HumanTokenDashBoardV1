package com.healthanalytics.android.core

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect fun getPlatformEngine(): HttpClientEngine

private const val BASE_URL = "https://api.stg.dh.deepholistics.com/"
private const val CLIENT_ID = "JmEfoQ2sP18APIiX9z0nY3vlDAKHIp8nKuyV"
private const val USER_TIMEZONE = "Asia/Calcutta"

val sharedHttpClient by lazy {
    HttpClient(getPlatformEngine()) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
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

        // Optional: common settings like timeouts, logging
    }
}