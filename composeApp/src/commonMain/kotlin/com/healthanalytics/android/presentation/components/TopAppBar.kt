package com.healthanalytics.android.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: String,
    isEndIconVisible: Boolean = true,
    isChatVisible: Boolean = true,
    onEndIconClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val endIcon = if (isChatVisible) Icons.Default.AccountCircle else Icons.Default.ShoppingCart
    TopAppBar(
        title = {
            Text(
                text = title, fontWeight = FontWeight.Bold, color = AppColors.white
            )
        }, actions = {
            if (isChatVisible) {
                IconButton(onClick = onChatClick) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Chat",
                        modifier = Modifier.size(24.dp),
                        tint = AppColors.white
                    )
                }
            }
            if (isEndIconVisible) {
                IconButton(onClick = onEndIconClick) {
                    Icon(
                        imageVector = endIcon,
                        contentDescription = "Profile",
                        modifier = Modifier.size(24.dp),
                        tint = AppColors.white
                    )
                }
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppColors.AppBackgroundColor,
            navigationIconContentColor = AppColors.white,
            titleContentColor = AppColors.white,
            actionIconContentColor = AppColors.white
        ), modifier = modifier
    )
}