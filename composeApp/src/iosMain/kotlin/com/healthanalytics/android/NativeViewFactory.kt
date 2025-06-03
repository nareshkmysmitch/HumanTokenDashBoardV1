package com.healthanalytics.android


interface NativeViewFactory {
    fun showAlertDialog(primaryText: String, secondaryText: String, onDismiss: () -> Unit, onLogout: () -> Unit)
}