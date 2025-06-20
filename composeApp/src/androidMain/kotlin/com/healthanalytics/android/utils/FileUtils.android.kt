package com.healthanalytics.android.utils

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

actual suspend fun saveTextFile(filename: String, content: String): String? {
    return try {

        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }

        val file = File(downloadsDir, filename)
        file.writeText(content)

        // Notify the MediaScanner so the file appears in file managers
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(file)
        appContext.sendBroadcast(intent)
//        openCsvFile(file)
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun openCsvFile(csvFile: File) {
    val context = appContext
    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        csvFile
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "text/csv")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    val chooser = Intent.createChooser(intent, "Open CSV file")
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    Handler(Looper.getMainLooper()).postDelayed({
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(chooser)
        } else {
            Toast.makeText(context, "No app found to open CSV file", Toast.LENGTH_SHORT).show()
        }
    }, 500)
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

actual fun openCsvFile(path: String) {
    openCsvFile(File(path))
}
