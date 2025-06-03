package com.healthanalytics.android.presentation.components

//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Menu
//import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

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
                Image(
                    painter = painterResource(Res.drawable.ic_calendar_icon),
                    contentDescription = "chat",
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(onClick = onProfileClick) {
//                Icon(
//                    imageVector = Icons.Default.Person,
//                    contentDescription = "Profile"
//                )
                Image(
                    painter = painterResource(Res.drawable.ic_calendar_icon),
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}