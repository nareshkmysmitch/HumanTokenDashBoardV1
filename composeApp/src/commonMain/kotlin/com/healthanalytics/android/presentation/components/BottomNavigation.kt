package com.healthanalytics.android.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.getNavigationItems
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

enum class Screen {
    HOME, PROFILE, CHAT
}

enum class MainScreen {
    DASHBOARD, BIOMARKERS, RECOMMENDATIONS, MARKETPLACE
}

@Composable
fun BottomNavBar(
    currentScreen: MainScreen,
    onScreenSelected: (MainScreen) -> Unit
) {
    val items = getNavigationItems()

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentScreen == item.screen,
                onClick = { onScreenSelected(item.screen) },
                icon = {
                    when (val icon = item.icon) {
                        is ImageVector -> Icon(imageVector = icon, contentDescription = item.label)
                        else -> {
                            Image(
                                painter = painterResource(icon as DrawableResource),
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                label = { Text(item.label) }
            )
        }
    }
}
