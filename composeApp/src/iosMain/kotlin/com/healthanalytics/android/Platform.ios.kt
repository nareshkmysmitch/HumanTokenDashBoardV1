package com.healthanalytics.android

import com.healthanalytics.android.presentation.components.Screen
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()


actual fun getNavigationItems(): List<NavigationItem> = listOf(
    NavigationItem(Screen.DASHBOARD, "Dashboard"),
    NavigationItem(Screen.BIOMARKERS, "BioMarkers"),
    NavigationItem(Screen.RECOMMENDATIONS, "Recommendations"),
    NavigationItem(Screen.MARKETPLACE, "Market Place")
)