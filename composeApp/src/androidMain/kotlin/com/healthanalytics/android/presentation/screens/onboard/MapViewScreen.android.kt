package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng as GmsLatLng

actual class LatLng actual constructor(actual val latitude: Double, actual val longitude: Double)

@Composable
actual fun MapView(
    modifier: Modifier,
    markerPosition: LatLng,
    onMarkerDragged: (LatLng) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
            GmsLatLng(markerPosition.latitude, markerPosition.longitude),
            15f
        )
    }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = com.google.maps.android.compose.rememberMarkerState(
                position = GmsLatLng(markerPosition.latitude, markerPosition.longitude)
            ),
            draggable = true,
            onDragEnd = {
                onMarkerDragged(LatLng(it.latitude, it.longitude))
            }
        )
    }
} 