
package com.healthanalytics.android.presentation.screens

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS doesn't have a system back button, so this is a no-op
}
