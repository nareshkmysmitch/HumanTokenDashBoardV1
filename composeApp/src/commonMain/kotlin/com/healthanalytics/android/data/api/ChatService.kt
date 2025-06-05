package com.healthanalytics.android.data.api

import com.healthanalytics.android.data.models.ChatResponse
import com.healthanalytics.android.data.models.ConversationResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

interface ChatService {
    suspend fun getConversations(
        accessToken: String, page: Int, perPage: Int = 40
    ): ConversationResponse

    suspend fun getConversation(
        accessToken: String, conversationId: String, page: Int, perPage: Int = 40
    ): ChatResponse

    suspend fun sendMessage(
        accessToken: String, conversationId: String, content: String
    ): ChatResponse
}

class ChatServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://api.chat.stg.dh.deepholistics.com/api"
) : ChatService {

    override suspend fun getConversations(
        accessToken: String, page: Int, perPage: Int
    ): ConversationResponse {
        return httpClient.get("$baseUrl/conversations") {
            parameter("page", page)
            parameter("per_page", perPage)
            header("access_token", accessToken)

        }.body()
    }

    override suspend fun getConversation(
        accessToken: String, conversationId: String, page: Int, perPage: Int
    ): ChatResponse {
        return httpClient.get("$baseUrl/conversations/$conversationId") {
            parameter("page", page)
            parameter("per_page", perPage)
            header("access_token", accessToken)
        }.body()
    }

    override suspend fun sendMessage(
        accessToken: String, conversationId: String, content: String
    ): ChatResponse {
        return httpClient.post("$baseUrl/conversations/$conversationId/messages") {
            header("access_token", accessToken)
            setBody(mapOf("content" to content))
        }.body()
    }
} 