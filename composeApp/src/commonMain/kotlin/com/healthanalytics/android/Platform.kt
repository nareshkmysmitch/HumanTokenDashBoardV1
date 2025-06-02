package com.healthanalytics.android

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform