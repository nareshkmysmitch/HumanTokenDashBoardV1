package com.healthanalytics.android.utils

import platform.Foundation.*

actual suspend fun saveTextFile(filename: String, content: String): String? {
    return try {
        val paths = NSSearchPathForDirectoriesInDomains(
            directory = NSDocumentDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true
        )
        val documentsDirectory = paths.firstOrNull() as? String ?: return null
        val filePath = documentsDirectory + "/" + filename
        val nsString = content as NSString
        nsString.writeToFile(filePath, atomically = true, encoding = NSUTF8StringEncoding)
        filePath
    } catch (e: Exception) {
        println("Error saving file: ${e.message}")
        null
    }
} 