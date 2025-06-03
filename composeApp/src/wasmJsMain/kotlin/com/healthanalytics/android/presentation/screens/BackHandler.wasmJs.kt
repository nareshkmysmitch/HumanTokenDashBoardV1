
package com.healthanalytics.android.presentation.screens

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // Web doesn't have a system back button in this context, so this is a no-op
}
