
package com.healthanalytics.android

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import com.healthanalytics.android.presentation.components.Screen

class DesktopPlatform : Platform {
    override val name: String = "Desktop"
}

actual fun getPlatform(): Platform = DesktopPlatform()

actual fun getNavigationItems(): List<NavigationItem> = listOf(
    NavigationItem(Screen.DASHBOARD, "Dashboard", Icons.Default.Home),
    NavigationItem(Screen.BIOMARKERS, "BioMarkers", Icons.Default.Info),
    NavigationItem(Screen.RECOMMENDATIONS, "Recommendations", Icons.Default.Settings),
    NavigationItem(Screen.MARKETPLACE, "Market Place", Icons.Default.ShoppingCart)
)
