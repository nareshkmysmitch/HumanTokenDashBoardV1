package com.healthanalytics.android

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.Shop
import androidx.compose.runtime.Composable
import com.healthanalytics.android.presentation.components.MainScreen

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

actual fun getNavigationItems(): List<NavigationItem> = listOf(
    NavigationItem(MainScreen.DASHBOARD, "Dashboard", Icons.Default.Home),
    NavigationItem(MainScreen.RECOMMENDATIONS, "Recommendations", Icons.Default.Recommend),
    NavigationItem(MainScreen.MARKETPLACE, "Market Place", Icons.Default.Shop),
)

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
}