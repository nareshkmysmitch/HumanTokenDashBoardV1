
package com.healthanalytics.android.utils

import android.content.Intent
import android.net.Uri
import android.os.Environment
import java.io.File

actual suspend fun saveTextFile(filename: String, content: String): String? {
    return try {
        // Save to Downloads directory in external storage
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        
        val file = File(downloadsDir, filename)
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
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.fromFile(file), "text/csv")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        appContext.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        // Fallback: open file manager to the Downloads folder
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.fromFile(file.parentFile), "resource/folder")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        val chooser = Intent.createChooser(intent, "Share CSV file")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(chooser)
    } catch (e: Exception) {
        e.printStackTrace()
    }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            appContext.startActivity(intent)
        } catch (e2: Exception) {
            e2.printStackTrace()
        }
    }
}
