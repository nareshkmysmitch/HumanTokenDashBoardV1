package com.healthanalytics.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.healthanalytics.android.LocalNativeViewFactory

@Composable
actual fun ShowAlertDialog(
    modifier: Modifier,
    title: String,
    message: String,
    onNegativeTxt: String,
    onPositiveTxt: String,
    onDismiss: () -> Unit,
    onLogout: () -> Unit,
) {
    val view = LocalNativeViewFactory.current

    LaunchedEffect(Unit) {
        view.showAlertDialog(
            primaryText = title,
            secondaryText = message,
            onDismiss = onDismiss,
            onLogout = onLogout,
            onNegativeTxt=onNegativeTxt,
            onPositiveTxt=onPositiveTxt
        )
    }
}
