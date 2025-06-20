package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.healthanalytics.android.presentation.components.TopAppBar

// Data class for LatLng
expect class LatLng(latitude: Double, longitude: Double) {
    val latitude: Double
    val longitude: Double
}

// Expect composable for MapView
@Composable
expect fun MapView(
    modifier: Modifier = Modifier,
    markerPosition: LatLng,
    onMarkerDragged: (LatLng) -> Unit
)

class MapViewScreen(
    private val initialPosition: LatLng,
    private val initialAddress: String = "",
    private val onBack: () -> Unit
) : Screen {
    @Composable
    override fun Content() {
        var markerPosition by remember { mutableStateOf(initialPosition) }
        var address by remember { mutableStateOf(initialAddress) }

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = "Select Location",
                actions = {},
                modifier = Modifier,
            )
            // Back button overlay (platform-specific, can be added in TopAppBar impl)
            Spacer(modifier = Modifier.height(8.dp))
            MapView(
                modifier = Modifier.weight(1f),
                markerPosition = markerPosition,
                onMarkerDragged = { newPos ->
                    markerPosition = newPos
                    // Address fetching logic will be in platform-specific impl
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
} 