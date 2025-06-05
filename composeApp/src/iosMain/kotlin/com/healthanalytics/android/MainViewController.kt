package com.healthanalytics.android

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.window.ComposeUIViewController
import com.healthanalytics.android.di.initKoin

val LocalNativeViewFactory = staticCompositionLocalOf<NativeViewFactory> {
    error("No view factory")
}


fun MainViewController(
    nativeViewFactory: NativeViewFactory,
) = ComposeUIViewController(configure = {
    initKoin()
}) {
    CompositionLocalProvider(LocalNativeViewFactory provides nativeViewFactory) {
        App()
    }
}
