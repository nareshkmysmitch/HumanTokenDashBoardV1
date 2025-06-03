package com.healthanalytics.android

interface IOSNativeBridge {
    fun nativeEncrypt(data: String): String
    fun nativeDecrypt(data: String): String
}

