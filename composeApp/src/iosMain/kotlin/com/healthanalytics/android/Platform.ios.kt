package com.healthanalytics.android

import com.healthanalytics.android.presentation.navigation.Screen
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()


actual fun getNavigationItems(): List<NavigationItem> = listOf(
    NavigationItem(Screen.Dashboard.route, "Dashboard", Res.drawable.ic_calendar_icon),
    NavigationItem(Screen.Biomarkers.route, "BioMarkers", Res.drawable.ic_calendar_icon),
    NavigationItem(Screen.Recommendations.route, "Recommendations", Res.drawable.ic_calendar_icon),
    NavigationItem(Screen.Marketplace.route, "Market Place", Res.drawable.ic_calendar_icon)
)