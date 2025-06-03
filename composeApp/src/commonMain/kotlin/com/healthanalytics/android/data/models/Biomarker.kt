package com.healthanalytics.android.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Biomarker(
    val id: String,
    val name: String,
    val value: Double? = null,
    val unit: String? = null,
    val status: String = "unknown",
    val category: String,
    val description: String? = null,
    val referenceMin: Double? = null,
    val referenceMax: Double? = null,
    val trend: String = "unknown"
)

@Serializable
data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val price: Double,
    val image: String,
    val type: String,
    val description: String? = null
)

@Serializable
data class CartItem(
    val id: String,
    val productId: String,
    val name: String,
    val brand: String,
    val price: Double,
    val image: String,
    val quantity: Int,
    val type: String
)

@Serializable
data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T? = null
)


@Serializable
data class ApiResult(
    val status: String,
    val message: String,
    val data: String? = null
)