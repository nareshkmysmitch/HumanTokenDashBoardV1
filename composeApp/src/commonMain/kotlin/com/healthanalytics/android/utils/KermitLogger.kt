package com.healthanalytics.android.utils

import androidx.compose.runtime.Composable
import co.touchlab.kermit.Logger

class KermitLogger(private val logger: Logger = Logger.withTag("HumanTokenV2")) {

    fun v(message: () -> String) = logger.v { message() }

    fun d(message: () -> String) = logger.d { message() }

    fun i(message: () -> String) = logger.i { message() }

    fun w(message: () -> String) = logger.w { message() }

    fun e(message: () -> String, throwable: Throwable? = null) = logger.e(throwable) { message() }
}