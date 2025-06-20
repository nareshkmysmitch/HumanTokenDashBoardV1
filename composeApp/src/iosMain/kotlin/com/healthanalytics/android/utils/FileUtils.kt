package com.healthanalytics.android.utils

import platform.Foundation.*
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
actual suspend fun saveTextFile(filename: String, content: String): String? {
    return try {
        val paths = NSSearchPathForDirectoriesInDomains(
            directory = NSDocumentDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true
        )
        val documentsDirectory = paths.firstOrNull() as? String ?: return null
        val filePath = "$documentsDirectory/$filename"
        val nsString = content as NSString

        memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()
            val success = nsString.writeToFile(
                filePath,
                atomically = true,
                encoding = NSUTF8StringEncoding,
                error = errorPtr.ptr
            )
            if (!success) {
                val error = errorPtr.value
                println("Error saving file: ${error?.localizedDescription}")
                return null
            }
        }

        // Open the file using the iOS share sheet
        openCsvFile(filePath)

        filePath
    } catch (e: Exception) {
        println("Error saving file: ${e.message}")
        null
    }
}

fun openCsvFile(filePath: String) {
    FileSharing.shareFile(path = filePath)
}

actual fun shareFile(filePath: String) {
    FileSharing.shareFile(path = filePath)
}

actual suspend fun openCsvFile(path: String) {
    openCsvFile(path)
}