package com.healthanalytics.android

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

actual fun getNavigationItems(): List<NavigationItem> {
    TODO("Not yet implemented")
}