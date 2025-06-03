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
    DASHBOARD, BIOMARKERS, RECOMMENDATIONS, MARKETPLACE
}

@Composable
fun BottomNavBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    val items = getNavigationItems()

    NavigationBar {
        val navigationItems = listOf(
            NavigationItem(Screen.DASHBOARD.route, "Dashboard"),
            NavigationItem(Screen.BIOMARKERS.route, "BioMarkers"),
            NavigationItem(Screen.RECOMMENDATIONS.route, "Recommendations"),
            NavigationItem(Screen.MARKETPLACE.route, "Market Place")
        )

        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    when (item.icon) {
                        is ImageVector -> Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                        is DrawableResource -> Image(
                            painter = painterResource(item.icon),
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}