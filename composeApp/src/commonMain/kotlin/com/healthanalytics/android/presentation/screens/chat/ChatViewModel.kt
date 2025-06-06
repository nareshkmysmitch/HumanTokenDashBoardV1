package com.healthanalytics.android.presentation.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.api.ChatApiService
import com.healthanalytics.android.data.models.Conversation
import com.healthanalytics.android.data.models.Message
import com.healthanalytics.android.utils.KermitLogger
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ConversationsUiState {
    data object Loading : ConversationsUiState()
    data class Success(
        val conversations: List<Conversation?>?,
        val isLoadingMore: Boolean = false,
        val canLoadMore: Boolean = false
    ) : ConversationsUiState()

    data class Error(val message: String) : ConversationsUiState()
}

sealed class ChatUiState {
    data object Loading : ChatUiState()
    data class Success(

        val messages: List<Message>,
        val isLoadingMore: Boolean = false,
        val canLoadMore: Boolean = false
    ) : ChatUiState()

    data class Error(val message: String) : ChatUiState()
}

class ChatViewModel(
    private val chatApiService: ChatApiService, private val log: KermitLogger
) : ViewModel() {

    private val _conversationsState =
        MutableStateFlow<ConversationsUiState>(ConversationsUiState.Loading)
    val conversationsState = _conversationsState.asStateFlow()

    private val _chatState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val chatState = _chatState.asStateFlow()

    private val _messageInput = MutableStateFlow("")
    val messageInput = _messageInput.asStateFlow()

    private var currentConversationsPage = 1
    private var currentChatPage = 1
    private var totalConversationsPages = 1
    private var totalChatPages = 1

    init {
        loadConversations()
    }

//    private val dummyAccessToken =
//        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNDM3OGVlYzItYTM4YS00MjAyLTk1Y2EtZDQwNGYwM2I5ZjlmIiwic2Vzc2lvbl9pZCI6IjI2ZTJhZWMzLWEwMGQtNDU0My05NWExLTNmZjk3YTVkMDQ3OCIsInVzZXJfaW50X2lkIjoiNzYiLCJwcm9maWxlX2lkIjoiNjUiLCJsZWFkX2lkIjoiY2QwOWJhOTAtMDI1ZC00OTI5LWI4MTMtNjI5MGUyNDU0NDI2IiwiaWF0IjoxNzQ5MTg4NTAwLCJleHAiOjE3NDk3OTMzMDB9.5B7JoGbwMuGLpUx6-PIK1rMloOusjtpYK6wxayHEFXo"
//

    private val dummyAccessToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNDM3OGVlYzItYTM4YS00MjAyLTk1Y2EtZDQwNGYwM2I5ZjlmIiwic2Vzc2lvbl9pZCI6IjJhODEyNTI3LWZmM2MtNGEyZS05MDE4LTQxODNiYWIzOGViZSIsInVzZXJfaW50X2lkIjoiNzYiLCJwcm9maWxlX2lkIjoiNjUiLCJsZWFkX2lkIjoiY2QwOWJhOTAtMDI1ZC00OTI5LWI4MTMtNjI5MGUyNDU0NDI2IiwiaWF0IjoxNzQ5MTA4Nzc1LCJleHAiOjE3NDk3MTM1NzV9.KaZlT_JMKdsol2hzKBsOpwYiQjQTQfWaJrjNN5gsJnQ"

    fun loadConversations(isLoadingMore: Boolean = false) {
        if (isLoadingMore && currentConversationsPage >= totalConversationsPages) return

        viewModelScope.launch {
            try {
                if (isLoadingMore) {
                    updateConversationsLoadingState(true)
                } else {
                    _conversationsState.value = ConversationsUiState.Loading
                }

                val response = chatApiService.getConversations(
                    dummyAccessToken, page = if (isLoadingMore) currentConversationsPage + 1 else 1
                )

                if (isLoadingMore) {
                    val currentConversations: List<Conversation?>? =
                        (_conversationsState.value as? ConversationsUiState.Success)?.conversations
                            ?: emptyList()
                    _conversationsState.value = ConversationsUiState.Success(
                        conversations = currentConversations?.plus(response.conversations),
                        canLoadMore = (response.page?.toInt() ?: 0) < (response.pages?.toInt() ?: 0)
                    )
                    currentConversationsPage = response.page?.toInt() ?: 0
                } else {
                    _conversationsState.value = ConversationsUiState.Success(
                        conversations = response.conversations,
                        canLoadMore = (response.page?.toInt() ?: 0) < (response.pages?.toInt() ?: 0)
                    )
                    currentConversationsPage = 1
                }
                totalConversationsPages = response.pages?.toInt() ?: 0

            } catch (e: Exception) {
                _conversationsState.value = ConversationsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadChat(conversationId: String, isLoadingMore: Boolean = false) {
        if (isLoadingMore && currentChatPage >= totalChatPages) return

        viewModelScope.launch {
            try {
                if (isLoadingMore) {
                    updateChatLoadingState(true)
                } else {
                    _chatState.value = ChatUiState.Loading
                }

                val chatResponse = chatApiService.getConversation(
                    dummyAccessToken,
                    conversationId = conversationId,
                    page = if (isLoadingMore) currentChatPage + 1 else 1
                )

                if (isLoadingMore) {
                    val currentState = _chatState.value as? ChatUiState.Success ?: return@launch
                    _chatState.value = ChatUiState.Success(

                        messages = currentState.messages + chatResponse.messages,
                        canLoadMore = (chatResponse.page?.toInt()
                            ?: 0) < (chatResponse.pages?.toInt() ?: 0)
                    )
                    currentChatPage = chatResponse.page?.toInt() ?: 0
                } else {
                    _chatState.value = ChatUiState.Success(

                        messages = chatResponse.messages,
                        canLoadMore = (chatResponse.page?.toInt()
                            ?: 0) < (chatResponse.pages?.toInt() ?: 0)
                    )
                    currentChatPage = 1
                }
                totalChatPages = chatResponse.pages?.toInt() ?: 0

            } catch (e: Exception) {
                _chatState.value = ChatUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun sendMessage(conversationId: String) {
        val content = _messageInput.value.trim()
        if (content.isEmpty()) return

        viewModelScope.launch {
            try {
                val response = chatApiService.sendMessage(dummyAccessToken, conversationId, content)
                val currentState = _chatState.value as? ChatUiState.Success ?: return@launch

                _chatState.value = currentState.copy(
                    messages = currentState.messages + response.messages
                )
                _messageInput.value = ""
            } catch (e: Exception) {
                // Handle error (could show a snackbar or other UI feedback)
            }
        }
    }

    fun updateMessageInput(input: String) {
        _messageInput.value = input
    }

    private fun updateConversationsLoadingState(isLoadingMore: Boolean) {
        val currentState = _conversationsState.value as? ConversationsUiState.Success ?: return
        _conversationsState.value = currentState.copy(isLoadingMore = isLoadingMore)
    }

    private fun updateChatLoadingState(isLoadingMore: Boolean) {
        val currentState = _chatState.value as? ChatUiState.Success ?: return
        _chatState.value = currentState.copy(isLoadingMore = isLoadingMore)
    }
}