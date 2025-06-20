package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

actual class LatLng actual constructor(actual val latitude: Double, actual val longitude: Double)

@Composable
actual fun MapView(
    modifier: Modifier,
    markerPosition: LatLng,
    onMarkerDragged: (LatLng) -> Unit
) {
    // TODO: Implement with Google Maps iOS SDK and Compose interop
    androidx.compose.foundation.layout.Box(modifier) {
        androidx.compose.material3.Text("Map goes here (iOS)")
    }
} 