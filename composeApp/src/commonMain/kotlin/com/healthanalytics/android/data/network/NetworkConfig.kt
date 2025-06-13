package com.healthanalytics.android.data.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.healthanalytics.android.data.repositories.PreferencesRepository

object NetworkConfig : KoinComponent {

    const val BASE_URL = "https://api.stg.dh.deepholistics.com/"
    const val CLIENT_ID = "JmEfoQ2sP18APIiX9z0nY3vlDAKHIp8nKuyV"
    const val USER_TIMEZONE = "Asia/Calcutta"
    
    private val preferencesRepository: PreferencesRepository by inject()
    
    var chatAccessToken: String = ""
        private set
        
    init {
        // Initialize chatAccessToken from preferences
        runBlocking {
            chatAccessToken = preferencesRepository.accessToken.first() ?: ""
        }
    }

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
//                header("Authorization", chatAccessToken)
            }
        }
    }
} 