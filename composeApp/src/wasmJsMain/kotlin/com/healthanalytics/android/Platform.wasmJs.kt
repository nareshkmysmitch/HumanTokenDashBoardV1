package com.healthanalytics.android

import androidx.compose.runtime.Composable

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

actual fun getNavigationItems(): List<NavigationItem> {
    TODO("Not yet implemented")
}

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    TODO("Not yet implemented")
}