package com.healthanalytics.android.presentation.screens.chat

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.touchlab.kermit.Logger
import org.koin.compose.koinInject

class ConversationListScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ChatViewModel = koinInject()


//        , onNavigateBack = {
//            Logger.d("Back button clicked")
//            navigator.pop()
//        }

        ConversationListScreen(
            onNavigateToChat = { id ->
                navigator.push(ChatScreen(conversationId = id))
            }, viewModel = viewModel, navigator = navigator
        )
    }
}

class ChatScreen(private val conversationId: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ChatViewModel = koinInject()

        ChatScreen(
            conversationId = conversationId,
            onNavigateBack = { navigator.pop() },
            viewModel = viewModel
        )
    }
} 