package com.healthanalytics.android.data.network

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val algorithm = "AES"
private const val tokenKey = "4xlmO9YC71qv28qfWHJ4LVEYm6gNNuYi" //staging
private const val padding = "AES/CBC/PKCS5Padding" //staging
private const val initVector = "lTayxxaZZfGhRs3j"
private const val ivSize = 16
private const val keySize = 32

actual fun encrypt(data: String): String {
    val len = tokenKey.toByteArray(Charsets.UTF_8).size
    val cipher = Cipher.getInstance(padding)

    val keyByteArray = ByteArray(keySize)
    val ivByteArray = ByteArray(ivSize)

    System.arraycopy(tokenKey.toByteArray(Charsets.UTF_8), 0, keyByteArray, 0, len)
    System.arraycopy(initVector.toByteArray(Charsets.UTF_8), 0, ivByteArray, 0, ivSize)

    val secretKeySpec = SecretKeySpec(keyByteArray, algorithm)
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, IvParameterSpec(ivByteArray))

    val result = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
    val encryptedString = Base64.encodeToString(result, Base64.NO_WRAP)

    return encryptedString
}

actual fun decrypt(data: String): String {
    try {
        val len = tokenKey.toByteArray(Charsets.UTF_8).size
        val cipher = Cipher.getInstance(padding)

        val keyByteArray = ByteArray(keySize)
        val ivByteArray = ByteArray(ivSize)

        System.arraycopy(tokenKey.toByteArray(Charsets.UTF_8), 0, keyByteArray, 0, len)
        System.arraycopy(initVector.toByteArray(Charsets.UTF_8), 0, ivByteArray, 0, ivSize)

        val secretKeySpec = SecretKeySpec(keyByteArray, algorithm)
        val ivSpec = IvParameterSpec(ivByteArray)

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec)
        val decryptedResult = Base64.decode(data, Base64.NO_WRAP)
        val decrypted = cipher.doFinal(decryptedResult)

        val decryptedText = String(decrypted)
        return decryptedText
    } catch (e: Exception) {
//        Timber.e("Decryption Exception ==> $e")
        return ""
    }
}