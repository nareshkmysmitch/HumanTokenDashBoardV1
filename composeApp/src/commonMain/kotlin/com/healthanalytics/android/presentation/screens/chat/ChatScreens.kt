package com.healthanalytics.android.presentation.screens.chat

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.healthanalytics.android.presentation.screens.chat.ChatViewModel
import org.koin.compose.koinInject

class ConversationListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ChatViewModel = koinInject()

        ConversationListScreen(
            onNavigateToChat = { id ->
                navigator.push(ChatScreen(conversationId = id))
            }, onNavigateBack = {
                navigator.pop()
            }, viewModel = viewModel
        )
    }
}

class ChatScreen(private val conversationId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ChatViewModel = koinInject()

        ChatScreen(
            conversationId = conversationId, onNavigateBack = {
                navigator.pop()
            }, viewModel = viewModel
        )
    }
} 