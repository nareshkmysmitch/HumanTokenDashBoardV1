
package com.healthanalytics.android.utils

import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

actual suspend fun saveTextFile(filename: String, content: String): String? {
    return try {
        // Save to app's internal storage (files directory)
        val internalDir = appContext.filesDir
        val file = File(internalDir, filename)
        file.writeText(content)
        
        // Open the file with system default app
        openFileWithSystem(file)
        
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun openFileWithSystem(file: File) {
    try {
        val uri = FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.fileprovider",
            file
        )
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/csv")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        appContext.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        // Fallback: try to open with any app that can handle text files
        try {
            val uri = FileProvider.getUriForFile(
                appContext,
                "${appContext.packageName}.fileprovider",
                file
            )
            
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "text/plain")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            appContext.startActivity(intent)
        } catch (fallbackException: Exception) {
            fallbackException.printStackTrace()
        }
    }
}

actual fun shareFile(filePath: String) {
    try {
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.fileprovider",
            file
        )
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooser = Intent.createChooser(intent, "Share CSV file")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(chooser)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
