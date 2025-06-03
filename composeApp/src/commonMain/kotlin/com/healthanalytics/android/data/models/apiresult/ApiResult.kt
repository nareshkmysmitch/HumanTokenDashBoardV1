package com.deepholistics.android.data.model.apiresult

import kotlinx.serialization.Serializable

@Serializable
data class ApiResult(
    val token: String = "",
    var message: String = "",
    var data: String? = "",
    var httpCode: Int = -1,
    val status: String = "",
    var errorMessage: String? = "",
    val type: String = "",
    var isSuccessful: Boolean = true,
    var hasTokenExpired: Boolean = false,
    var display_mode: String = "",
)
