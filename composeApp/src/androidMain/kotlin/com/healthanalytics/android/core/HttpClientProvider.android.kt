package com.healthanalytics.android.core

import io.ktor.client.engine.*
import io.ktor.client.engine.android.*

actual fun getPlatformEngine(): HttpClientEngine = Android.create()