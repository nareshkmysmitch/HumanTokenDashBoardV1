package com.healthanalytics.android.utils

import co.touchlab.kermit.Logger
import com.healthanalytics.android.data.models.ApiResult
import com.healthanalytics.android.data.network.decrypt
import com.healthanalytics.android.data.network.encrypt
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

object EncryptionUtils {

    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true  // Add this to handle null values
        explicitNulls = false     // Add this to handle missing fields
    }

    /**
     * Generic function to decrypt encrypted API response data
     * This handles the pattern where API returns encrypted data in response.data field
     */
    fun decryptApiResponse(encryptedData: String): String {
        return try {
            // Your existing decryption logic adapted for common use
            decryptString(encryptedData)
        } catch (e: Exception) {
            println("Decryption failed: ${e.message}")
            ""
        }
    }

    /**
     * Generic decryption function that can be used across platforms
     */
    private fun decryptString(encryptedText: String): String {
        return decrypt(encryptedText)
    }

    /**
     * Handles encrypted API responses generically
     * Takes raw API response and returns decrypted data if needed
     */
    inline fun <reified T> handleDecryptionResponse(
        responseBody: String,
    ): T? {
        return try {
            // Parse the encrypted response structure
            val encryptedResponse = json.decodeFromString<ApiResult>(responseBody)
            // Decrypt the data field
            val decryptedData = encryptedResponse.data?.let { decryptApiResponse(it) }

            Logger.e("decryptedData..$decryptedData")

            // Parse the decrypted data as the expected type
            if (decryptedData?.isNotEmpty() == true) {

                Logger.e { "Decoded String ${json.decodeFromString<T>(decryptedData)}" }
                json.decodeFromString<T>(decryptedData)
            } else {
                Logger.e { "Else Block.. " }
                null
            }
        } catch (e: Exception) {
            Logger.e("Failed to handle encrypted response: ${e.message}")
            null
        }
    }

    inline fun <reified T> T.toEncryptedRequestBody(): JsonObject {
        val jsonString = json.encodeToString(this)
        Logger.e("toEncryptedRequestBody..${this.toString()}")

        val encrypted = encrypt(jsonString)
        return buildJsonObject {
            put("data", JsonPrimitive(encrypted))
        }
    }
}

