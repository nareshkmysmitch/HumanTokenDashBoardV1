package com.healthanalytics.android.utils

import android.content.Context
import java.io.File

actual fun saveFile(fileName: String, bytes: ByteArray, context: Any?): String {
    val ctx = context as Context
    val file = File(ctx.cacheDir, fileName)
    file.writeBytes(bytes)
    return file.absolutePath
} 