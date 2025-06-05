
package com.healthanalytics.android

import androidx.compose.runtime.Composable
import com.healthanalytics.android.presentation.components.MainScreen
import com.healthanalytics.android.presentation.components.Screen
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon

class DesktopPlatform : Platform {
    override val name: String = "Desktop"
}

actual fun getPlatform(): Platform = DesktopPlatform()

actual fun getNavigationItems(): List<NavigationItem> = listOf(
    NavigationItem(MainScreen.DASHBOARD, "Dashboard", Res.drawable.ic_calendar_icon),
    NavigationItem(MainScreen.BIOMARKERS, "BioMarkers", Res.drawable.ic_calendar_icon),
    NavigationItem(MainScreen.RECOMMENDATIONS, "Recommendations", Res.drawable.ic_calendar_icon),
    NavigationItem(MainScreen.MARKETPLACE, "Market Place", Res.drawable.ic_calendar_icon)
)

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    TODO("Not yet implemented")
}