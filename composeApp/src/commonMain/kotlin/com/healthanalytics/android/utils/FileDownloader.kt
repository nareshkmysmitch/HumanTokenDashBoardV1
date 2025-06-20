package com.healthanalytics.android.utils

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*

private const val BUFFER_SIZE = 8192

suspend fun downloadFile(client: HttpClient, url: String): ByteArray {
    return client.get(url).bodyAsChannel().toByteArray()
}

suspend fun ByteReadChannel.toByteArray(): ByteArray {
    val buffer = BytePacketBuilder()
    while (!isClosedForRead) {
        val packet = readRemaining(BUFFER_SIZE.toLong())
        buffer.writePacket(packet)
    }
    return buffer.build().readBytes()
}

//expect fun saveFile(fileName: String, bytes: ByteArray, context: Any? = null): String