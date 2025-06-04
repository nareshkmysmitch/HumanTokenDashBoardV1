package com.healthanalytics.android.core

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

actual fun getPlatformEngine(): HttpClientEngine = Darwin.create()