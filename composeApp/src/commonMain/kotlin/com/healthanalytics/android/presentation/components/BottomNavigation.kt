package com.healthanalytics.android.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

enum class Screen {
    DASHBOARD, BIOMARKERS, RECOMMENDATIONS, MARKETPLACE
}

data class NavigationItem(
    val screen: Screen, val label: String, val icon: ImageVector
)

val navigationItems = listOf(
    NavigationItem(Screen.DASHBOARD, "Dashboard", Icons.Default.Home),
    NavigationItem(Screen.BIOMARKERS, "BioMarkers", Icons.Default.Info),
    NavigationItem(Screen.RECOMMENDATIONS, "Recommendations", Icons.Default.Settings),
    NavigationItem(Screen.MARKETPLACE, "Market Place", Icons.Default.ShoppingCart)
)

@Composable
fun BottomNavigationBar(
    currentScreen: Screen, onScreenSelected: (Screen) -> Unit
) {
    NavigationBar {
        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentScreen == item.screen,
                onClick = { onScreenSelected(item.screen) })
        }
    }
}