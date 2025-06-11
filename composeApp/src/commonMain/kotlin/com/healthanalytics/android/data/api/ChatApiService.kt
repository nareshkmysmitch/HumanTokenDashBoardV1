package com.healthanalytics.android.data.api

import com.healthanalytics.android.data.models.ChatResponse
import com.healthanalytics.android.data.models.ConversationResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

interface ChatApiService {
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


class ChatApiServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://api.chat.stg.dh.deepholistics.com/api"
) : ChatApiService {

    override suspend fun getConversations(
        accessToken: String, page: Int, perPage: Int
    ): ConversationResponse {
        return httpClient.get("$baseUrl/conversations") {

            parameter("page", page)
            parameter("per_page", perPage)

        }.body()
    }

    override suspend fun getConversation(
        accessToken: String, conversationId: String, page: Int, perPage: Int
    ): ChatResponse {
        return httpClient.get("$baseUrl/conversations/$conversationId") {
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()
    }

    // https://api.chat.stg.dh.deepholistics.com/api/messages/send_message
    //{"conversation_id":317,"message":"Hi"}

    override suspend fun sendMessage(
        accessToken: String, conversationId: String, content: String
    ): ChatResponse {

        val requestData = SendMessage(conversation_id = conversationId, message = content)
        return httpClient.post("$baseUrl/messages/send_message") {
            setBody(requestData)
//            parameter("conversation_id", conversationId)
//            parameter("message", content)
        }.body()
    }
} 