package com.healthanalytics.android.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.healthanalytics.android.NavigationItem
import com.healthanalytics.android.getNavigationItems

enum class Screen {
    DASHBOARD, BIOMARKERS, RECOMMENDATIONS, MARKETPLACE
}

@Composable
fun BottomNavBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    val items = getNavigationItems()

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentScreen == item.screen,
                onClick = { onScreenSelected(item.screen) },
                icon = {
                    val icon = item.icon
                    if (icon is ImageVector) {
                        Icon(imageVector = icon, contentDescription = item.label)
                    }
                },
                label = { Text(item.label) }
            )
        }
    }
}