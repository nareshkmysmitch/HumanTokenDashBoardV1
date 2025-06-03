package com.healthanalytics.android

import com.healthanalytics.android.presentation.components.NavigationItem

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getNavigationItems(): List<NavigationItem>