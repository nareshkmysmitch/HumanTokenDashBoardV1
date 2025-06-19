
package com.healthanalytics.android.utils

import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

actual suspend fun saveTextFile(filename: String, content: String): String? {
    return try {
        // Save file to Downloads directory
        val downloadsDir = File(appContext.getExternalFilesDir(null), "Downloads")
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        
        val file = File(downloadsDir, filename)
        file.writeText(content)
        
        // Open the file automatically
        openFile(file)
        
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun openFile(file: File) {
    try {
        val uri: Uri = FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.fileprovider",
            file
        )
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/csv")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        appContext.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        // If opening fails, try to open the parent directory
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.fromFile(file.parentFile), "resource/folder")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            appContext.startActivity(intent)
        } catch (e2: Exception) {
            e2.printStackTrace()
        }
    }
}
