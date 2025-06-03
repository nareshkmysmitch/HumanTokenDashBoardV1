package com.healthanalytics.android

import com.healthanalytics.android.presentation.components.MainScreen
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()


actual fun getNavigationItems(): List<NavigationItem> = listOf(
    NavigationItem(MainScreen.DASHBOARD, "Dashboard", Res.drawable.ic_calendar_icon),
    NavigationItem(MainScreen.BIOMARKERS, "BioMarkers", Res.drawable.ic_calendar_icon),
    NavigationItem(MainScreen.RECOMMENDATIONS, "Recommendations", Res.drawable.ic_calendar_icon),
    NavigationItem(MainScreen.MARKETPLACE, "Market Place", Res.drawable.ic_calendar_icon)
)