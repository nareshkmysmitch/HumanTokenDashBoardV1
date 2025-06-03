package com.healthanalytics.android.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//
//@Serializable
//data class ProductResponse(
//    val data: ProductData, val message: String, val status: String
//)
//
//@Serializable
//data class ProductData(
//    val products: List<Product>
//)
//
//@Serializable
//data class Product(
//    val id: String,
//    val title: String,
//    val subtitle: String?,
//    val brand: String,
//    @SerialName("image_url") val imageUrl: String,
//    val score: Double,
//    val rating: Double,
//    @SerialName("reviews_count") val reviewsCount: Int,
//    @SerialName("current_price") val currentPrice: Double,
//    @SerialName("original_price") val originalPrice: Double?,
//    val tags: List<String>,
//    @SerialName("is_favorite") val isFavorite: Boolean = false
//)
//
//


@Serializable
data class ProductResponse(
    val data: ProductData? = null, val message: String? = null, val status: String? = null
)

@Serializable
data class ProductData(
    val pagination: Pagination? = null, val products: List<Product?>? = null
)

@Serializable
data class Pagination(
    val currentPage: Int? = null,
    val limit: Int? = null,
    val total: Int? = null,
    val totalPages: Int? = null
)

@Serializable
data class Product(
    val category: List<String?>? = null,
    val created_at: String? = null,
    val description: String? = null,
    val id: String? = null,
    val img_urls: List<String?>? = null,
    val is_active: Boolean? = null,
    val mrp: String? = null,
    val n_rating: Int? = null,
    val name: String? = null,
    val price: String? = null,
    val product_id: String? = null,
    val rating: String? = null,
    val sku: String? = null,
    val stock: Int? = null,
    val tags: List<String?>? = null,
    val type: String? = null,
    val updated_at: String? = null,
    val vendor: Vendor? = null,
    val vendor_id: String? = null,
    val vendor_name: String? = null,
    val vendor_product_id: String? = null
)

@Serializable
data class Vendor(
    val name: String? = null
)