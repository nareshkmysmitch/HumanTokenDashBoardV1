package com.healthanalytics.android.utils

expect suspend fun saveTextFile(filename: String, content: String): String?
expect fun shareFile(filePath: String) 