package com.healthanalytics.android.data.network

import com.healthanalytics.android.IOSNativeBridge

lateinit var isoNativeBridge: IOSNativeBridge

actual fun encrypt(data: String): String {
    return isoNativeBridge.nativeEncrypt(data)
}

actual fun decrypt(data: String): String {
    return isoNativeBridge.nativeDecrypt(data)
}