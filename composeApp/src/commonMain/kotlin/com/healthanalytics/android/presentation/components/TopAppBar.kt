package com.healthanalytics.android.presentation.components

//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Menu
//import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: String,
    onProfileClick: () -> Unit,
    onChatClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = onChatClick) {
//                Icon(
//                    imageVector = Icons.Default.Menu,
//                    contentDescription = "Chat"
//                )
            }
            IconButton(onClick = onProfileClick) {
//                Icon(
//                    imageVector = Icons.Default.Person,
//                    contentDescription = "Profile"
//                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}