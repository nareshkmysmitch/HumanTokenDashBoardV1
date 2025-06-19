
package com.healthanalytics.android.utils

import android.content.Intent
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

actual suspend fun saveTextFile(filename: String, content: String): String? {
    return try {
        // Save to app's private external files directory (doesn't require WRITE_EXTERNAL_STORAGE)
        val file = File(appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename)
        file.writeText(content)
        
        // Open the file with system default app using direct file URI
        openFileWithSystem(file)
        
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun openFileWithSystem(file: File) {
    try {
        val uri = Uri.fromFile(file)
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/csv")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        // Check if there's an app that can handle this intent
        if (intent.resolveActivity(appContext.packageManager) != null) {
            appContext.startActivity(intent)
        } else {
            // Fallback: share the file instead
            shareFile(file.absolutePath)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        // Fallback: try to share the file
        shareFile(file.absolutePath)
    }
}

actual fun shareFile(filePath: String) {
    try {
        val file = File(filePath)
        val uri = Uri.fromFile(file)
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "CSV file: ${file.name}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        val chooser = Intent.createChooser(intent, "Share CSV file")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(chooser)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
