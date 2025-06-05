package com.healthanalytics.android.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: String,
    isEndIconVisible: Boolean = true,
    isChatVisible: Boolean = true,
    onEndIconClick: () -> Unit = {},
    onChatClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            if (isChatVisible) {
                IconButton(onClick = onChatClick) {
                    Image(
                        painter = painterResource(Res.drawable.ic_calendar_icon),
                        contentDescription = "chat",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            if (isEndIconVisible) {
                IconButton(onClick = onEndIconClick) {
                    Image(
                        painter = painterResource(Res.drawable.ic_calendar_icon),
                        contentDescription = "Profile",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}