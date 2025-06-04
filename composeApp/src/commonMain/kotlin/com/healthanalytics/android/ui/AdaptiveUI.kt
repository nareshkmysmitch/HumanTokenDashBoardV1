package com.healthanalytics.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ShowAlertDialog(modifier: Modifier, title: String, message: String, onDismiss: () -> Unit, onLogout: () -> Unit)
