package com.healthanalytics.android

import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import com.healthanalytics.android.presentation.components.Screen

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

val navigationItems = listOf(
    NavigationItem(Screen.DASHBOARD, "Dashboard", Icons.Default.Home),
    NavigationItem(Screen.BIOMARKERS, "BioMarkers", Icons.Default.Info),
    NavigationItem(Screen.RECOMMENDATIONS, "Recommendations", Icons.Default.Settings),
    NavigationItem(Screen.MARKETPLACE, "Market Place", Icons.Default.ShoppingCart)
)

actual fun getNavigationItems(): List<NavigationItem> = listOf(
    NavigationItem(Screen.DASHBOARD, "Dashboard", Icons.Default.Home),
    NavigationItem(Screen.BIOMARKERS, "BioMarkers", Icons.Default.Info),
    NavigationItem(Screen.RECOMMENDATIONS, "Recommendations", Icons.Default.Settings),
    NavigationItem(Screen.MARKETPLACE, "Market Place", Icons.Default.ShoppingCart)
)