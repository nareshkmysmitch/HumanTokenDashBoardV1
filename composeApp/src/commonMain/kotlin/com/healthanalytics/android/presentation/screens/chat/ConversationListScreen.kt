package com.healthanalytics.android.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.models.Conversation
import com.healthanalytics.android.presentation.theme.AppColors
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    onNavigateToChat: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel,
    navigator: cafe.adriel.voyager.navigator.Navigator
) {

    BackHandler(enabled = true, onBack = { navigator.pop() })

    val uiState by viewModel.conversationsState.collectAsState()
    println("conversation list screen $uiState")

//    viewModel.loadConversations()

    Scaffold(modifier = modifier.fillMaxSize(), topBar = {
        TopAppBar(
            title = {
                Text(text = "Conversations", color = AppColors.textPrimary)
            }, navigationIcon = {
                IconButton(onClick = {
                    Logger.e { "Back button clicked" }
                    navigator.pop()
                }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = AppColors.textPrimary
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AppColors.Black,
                navigationIconContentColor = AppColors.textPrimary,
                titleContentColor = AppColors.textPrimary
            )
        )
    }, floatingActionButton = {
        FloatingActionButton(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 48.dp),
            onClick = { },
            containerColor = AppColors.Pink,
            contentColor = AppColors.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Chat")
        }
    }) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(AppColors.Black)
        ) {
            when (val state = uiState) {
                is ConversationsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ConversationsUiState.Success -> {
                    if (state.conversations?.isEmpty() == true) {
                        EmptyState(modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {

                            state.conversations?.let { conversationList ->
                                items(conversationList) { conversation ->
                                    ConversationItem(
                                        conversation = conversation, onClick = {
                                            onNavigateToChat(conversation?.id.toString())
                                        })
                                }
                            }

                            if (state.isLoadingMore) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.align(Alignment.Center).size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is ConversationsUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.loadConversations() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}


@Composable
private fun ConversationItem(
    conversation: Conversation?, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick), color = AppColors.Black
    ) {

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    modifier = Modifier.size(42.dp).padding(3.dp),
                    shape = MaterialTheme.shapes.small,
                    color = AppColors.PurpleTitle
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = AppColors.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Conversation " + conversation?.id.toString(),
                        color = AppColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatDateString(conversation?.updated_at.toString()),
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.textPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No conversations yet",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun ErrorState(
    message: String, onRetry: () -> Unit, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

fun formatDateString(dateString: String): String {
    return try {
        var modifiedDate = if (dateString.contains("z")) {
            dateString
        } else {
            dateString + "z"
        }

        val instant = Instant.parse(modifiedDate)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${
            localDateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }
        } ${localDateTime.dayOfMonth}"
    } catch (e: Exception) {
        Logger.e("Error formatting date", e)
        dateString
    }
}