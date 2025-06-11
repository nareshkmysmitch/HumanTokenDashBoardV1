
package com.healthanalytics.android

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import com.healthanalytics.android.presentation.components.MainScreen
import com.healthanalytics.android.presentation.components.Screen

class DesktopPlatform : Platform {
    override val name: String = "Desktop"
}

actual fun getPlatform(): Platform = DesktopPlatform()

actual fun getNavigationItems(): List<NavigationItem> = listOf(
    NavigationItem(MainScreen.DASHBOARD, "Dashboard", Icons.Default.Home),
    NavigationItem(MainScreen.RECOMMENDATIONS, "Recommendations", Icons.Default.Recommend),
    NavigationItem(MainScreen.MARKETPLACE, "Market Place", Icons.Default.Shop),
)

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    TODO("Not yet implemented")
}