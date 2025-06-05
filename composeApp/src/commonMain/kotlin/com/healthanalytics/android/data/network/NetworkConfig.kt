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

      const val BASE_URL = "https://api.stg.dh.deepholistics.com/"
      const val CLIENT_ID = "JmEfoQ2sP18APIiX9z0nY3vlDAKHIp8nKuyV"
      const val USER_TIMEZONE = "Asia/Calcutta"

    fun createHttpClient(accessToken: String? = null): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
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