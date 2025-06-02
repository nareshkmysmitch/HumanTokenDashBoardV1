package com.healthanalytics.android

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "HumanTokenDashBoardV1",
    ) {
        App()
    }
}