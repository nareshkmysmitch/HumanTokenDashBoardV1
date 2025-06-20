package com.healthanalytics.android.utils

import platform.Foundation.*
import kotlinx.cinterop.*

//actual fun saveFile(fileName: String, bytes: ByteArray, context: Any?): String {
//    val filePath = "${NSHomeDirectory()}/Documents/$fileName"
//    val nsData = bytes.toNSData()
//    nsData.writeToFile(filePath, true)
//    return filePath
//}
//
//@OptIn(ExperimentalForeignApi::class)
//fun ByteArray.toNSData(): NSData = usePinned { pinned ->
//    NSData.dataWithBytes(pinned.addressOf(0), size.toULong())
//}