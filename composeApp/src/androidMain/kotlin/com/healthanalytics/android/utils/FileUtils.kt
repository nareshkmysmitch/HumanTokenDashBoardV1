package com.healthanalytics.android.utils

import android.content.Context
import java.io.File

// You must set this in your Application class: com.healthanalytics.android.utils.appContext = this
/*
lateinit var appContext: Context

actual suspend fun saveTextFile(filename: String, content: String): String? {
    return try {
        val file = File(appContext.getExternalFilesDir(null), filename)
        file.writeText(content)
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
*/

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.OutputStream

lateinit var appContext: Context

@RequiresApi(Build.VERSION_CODES.Q)
actual suspend fun saveTextFile(filename: String, content: String): String? {
    return try {
        val resolver = appContext.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, filename)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri).use { outputStream: OutputStream? ->
                outputStream?.write(content.toByteArray())
                outputStream?.flush()
            }
            // Return the file path or URI string
            uri.toString()
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}