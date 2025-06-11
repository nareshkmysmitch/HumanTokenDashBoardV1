package com.healthanalytics.android.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ConversationResponse(
    val conversations: List<Conversation> = emptyList<Conversation>(),
    val page: Int? = null,
    val pages: Int? = null,
    val per_page: Int? = null,
    val status: String? = null,
    val total: Int? = null
)

@Serializable
data class Conversation(
    val created_at: String? = null,
    val id: Int? = null,
    val title: String? = null,
    val updated_at: String? = null
)

@Serializable
data class ChatResponse(
    val messages: List<Message> = emptyList<Message>(),
    val page: Int? = null,
    val pages: Int? = null,
    val per_page: Int? = null,
    val status: String? = null,
    val total: Int? = null
)

@Serializable
data class Message(
    val comment: String? = null,
    val content: String? = null,
    val created_at: String? = null,
    val id: Int? = null,
    val original_file_name: String? = null,
    val rating: String? = null,
    val sender: String? = null,
    val trace_id: String? = null,
    val url: String? = null
)


//@Serializable
//data class ConversationResponse(
//    val data: List<Conversation>,
//    val pagination: Pagination
//)
//
//@Serializable
//data class Conversation(
//    val id: String,
//    val title: String,
//    @SerialName("created_at")
//    val createdAt: String,
//    @SerialName("updated_at")
//    val updatedAt: String,
//    @SerialName("last_message")
//    val lastMessage: Message? = null
//)
//
//@Serializable
//data class Message(
//    val id: String,
//    val content: String,
//    @SerialName("created_at")
//    val createdAt: String,
//    @SerialName("sender_type")
//    val senderType: String, // "user" or "system"
//    @SerialName("conversation_id")
//    val conversationId: String
//)
//
//@Serializable
//data class ChatResponse(
//    val data: ChatData,
//    val message: String,
//    val status: Int
//)
//
//@Serializable
//data class ChatData(
//    val conversation: Conversation,
//    val messages: List<Message>,
//    val pagination: Pagination
//)
//
//@Serializable
//data class Pagination(
//    @SerialName("current_page")
//    val currentPage: Int,
//    @SerialName("total_pages")
//    val totalPages: Int,
//    val total: Int,
//    @SerialName("per_page")
//    val perPage: Int
//)