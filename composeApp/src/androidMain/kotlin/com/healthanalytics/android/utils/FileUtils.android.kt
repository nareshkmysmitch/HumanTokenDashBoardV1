package com.healthanalytics.android.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import android.os.Environment
import java.io.FileOutputStream

actual suspend fun saveTextFile(filename: String, content: String): String? {
    return try {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        
        val file = File(downloadsDir, filename)
        FileOutputStream(file).use { outputStream ->
            outputStream.write(content.toByteArray())
        }
        
        // Share/open the file after saving
        shareFile(file.absolutePath)
        
        file.absolutePath
    } catch (e: Exception) {
        println("Error saving file: ${e.message}")
        null
    }
}

actual fun shareFile(filePath: String) {
    try {
        val file = File(filePath)
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                val uri = Uri.fromFile(file)
                setDataAndType(uri, "text/csv")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            // Try to start the intent
            try {
                val context = androidx.compose.ui.platform.LocalContext.current
                context.startActivity(intent)
            } catch (e: Exception) {
                // Fallback to share intent if no app can handle CSV
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val context = androidx.compose.ui.platform.LocalContext.current
                context.startActivity(Intent.createChooser(shareIntent, "Open CSV with"))
            }
        }
    } catch (e: Exception) {
        println("Error opening file: ${e.message}")
    }
}