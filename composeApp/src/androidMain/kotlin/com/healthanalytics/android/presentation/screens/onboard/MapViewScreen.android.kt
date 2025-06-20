package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.android.gms.maps.model.LatLng as GmsLatLng

actual class LatLng actual constructor(actual val latitude: Double, actual val longitude: Double)


@OptIn(MapsComposeExperimentalApi::class)
@Composable
actual fun MapView(
    modifier: Modifier, markerPosition: LatLng, onMarkerDragged: (LatLng) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            GmsLatLng(markerPosition.latitude, markerPosition.longitude), 14f
        )
    }

    val markerState =
        rememberMarkerState(position = GmsLatLng(markerPosition.latitude, markerPosition.longitude))

    GoogleMap(
        modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = markerState, draggable = true
        )

        MapEffect(markerState) { map ->
            map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                override fun onMarkerDragStart(p0: Marker) {}

                override fun onMarkerDrag(p0: Marker) {}

                override fun onMarkerDragEnd(p0: Marker) {
                    onMarkerDragged(LatLng(p0.position.latitude, p0.position.longitude))
                }
            })
        }
    }
}


//@Composable
//actual fun MapView(
//    modifier: Modifier, markerPosition: LatLng, onMarkerDragged: (LatLng) -> Unit
//) {
//    val cameraPositionState = rememberCameraPositionState {
//        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
//            GmsLatLng(markerPosition.latitude, markerPosition.longitude), 15f
//        )
//    }
//
//
//    GoogleMap(
//        modifier = modifier,
//        cameraPositionState = cameraPositionState,
//        onMarkerDragEnd = { marker ->
//            onMarkerDragged(
//                LatLng(marker.position.latitude, marker.position.longitude)
//            )
//        }
//    ) {
//        Marker(
//            state = rememberMarkerState(
//                position = GmsLatLng(markerPosition.latitude, markerPosition.longitude)
//            ),
//            draggable = true
//        )
//    }
//
//    GoogleMap(
//        modifier = modifier,
//        cameraPositionState = cameraPositionState,
//
//        ) {
//
//    }
//}