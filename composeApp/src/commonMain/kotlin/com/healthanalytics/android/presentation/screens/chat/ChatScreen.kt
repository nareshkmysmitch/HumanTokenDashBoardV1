package com.healthanalytics.android.presentation.screens.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.models.Message
import com.healthanalytics.android.presentation.components.FilledAppButton
import com.healthanalytics.android.presentation.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel,
) {
    val uiState by viewModel.chatState.collectAsState()
    val messageInput by viewModel.messageInput.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Initial load of chat messages
    LaunchedEffect(conversationId) {
        viewModel.loadChat(conversationId)
    }
    BackHandler(enabled = true, onBack = { onNavigateBack() })
    // Scroll to bottom when messages are loaded or updated
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ChatUiState.Success -> {
                if (state.messages.isNotEmpty()) {
                    listState.scrollToItem(state.messages.size - 1)
                }
            }

            else -> {}
        }
    }

    Scaffold(modifier = modifier.fillMaxSize(), topBar = {
        TopAppBar(
            title = {
                when (val state = uiState) {
                    is ChatUiState.Success -> Text(
                        text = "Chat", color = AppColors.Black
                    )

                    else -> Text(
                        text = "Chat", color = AppColors.Black
                    )
                }
            }, navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack, contentDescription = "Back", tint = AppColors.Black
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AppColors.AppBackgroundColor,
                navigationIconContentColor = AppColors.Black,
                titleContentColor = AppColors.Black
            )
        )
    }, bottomBar = {
        ChatInput(
            value = messageInput, onValueChange = viewModel::updateMessageInput, onSend = {
                viewModel.sendMessage(conversationId)
                viewModel.updateMessageInput("")
                coroutineScope.launch {
                    delay(500)
                    viewModel.loadChat(conversationId)
                }
            })
    }) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ChatUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ChatUiState.Success -> {
                    ChatMessages(
                        messages = state.messages,
                        isLoadingMore = state.isLoadingMore,
                        canLoadMore = state.canLoadMore,
                        listState = listState,
                        onLoadMore = {
                            if (state.canLoadMore) {
                                viewModel.loadChat(conversationId, isLoadingMore = true)
                            }
                        })
                }

                is ChatUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.loadChat(conversationId) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessages(
    messages: List<Message>,
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    listState: LazyListState,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        reverseLayout = false,
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center).size(24.dp)
                    )
                }
            }
        }

        items(messages) { message ->
            ChatMessage(message = message)
        }
    }

    // Check for pagination when scrolling up (near the top)
    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (listState.firstVisibleItemIndex < 5 && messages.isNotEmpty() && canLoadMore) {
            onLoadMore()
        }
    }
}

@Composable
private fun ChatMessage(
    message: Message, modifier: Modifier = Modifier
) {
    val isUserMessage = message.sender.toString() != "bot"
    val backgroundColor = if (isUserMessage) {
        AppColors.HotPink
    } else {
        AppColors.SpearMint
    }

    val contentColor = if (isUserMessage) {
        AppColors.white
    } else {
        AppColors.Black
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = backgroundColor,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.content.toString(),
                    color = contentColor,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = formatDateString(message.created_at.toString()),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.7f),
                    modifier = Modifier.align(if (isUserMessage) Alignment.End else Alignment.Start)
                        .padding(top = if (isUserMessage) 0.dp else 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier, tonalElevation = 2.dp, color = AppColors.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Ask you asdsddfsf...")
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.DarkPurple,
                    unfocusedBorderColor = AppColors.DarkPurple.copy(alpha = 0.5f),
                    focusedContainerColor = AppColors.white,
                    unfocusedContainerColor = AppColors.white
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            FilledAppButton(
                onClick = onSend,
                enabled = value.isNotBlank(),
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}