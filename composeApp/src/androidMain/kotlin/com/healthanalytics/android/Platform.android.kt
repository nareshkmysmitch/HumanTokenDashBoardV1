package com.healthanalytics.android

import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.Shop
import androidx.compose.runtime.Composable
import com.healthanalytics.android.presentation.components.MainScreen
import androidx.activity.compose.BackHandler as AndroidBackHandler

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun getNavigationItems(): List<NavigationItem> = listOf(
    NavigationItem(MainScreen.DASHBOARD, "Dashboard", Icons.Default.Home),
    NavigationItem(MainScreen.RECOMMENDATIONS, "Recommendations", Icons.Default.Recommend),
    NavigationItem(MainScreen.MARKETPLACE, "Market Place", Icons.Default.Shop),
)

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    AndroidBackHandler(enabled = enabled, onBack = onBack)
}
