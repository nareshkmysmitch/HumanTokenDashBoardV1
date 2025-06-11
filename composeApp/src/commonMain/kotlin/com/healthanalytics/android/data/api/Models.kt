package com.healthanalytics.android.data.api

import kotlinx.serialization.Serializable


@Serializable
data class ProductData(
    val pagination: Pagination? = null, val products: List<Product?>? = null,
)

@Serializable
data class Pagination(
    val currentPage: Int? = null,
    val limit: Int? = null,
    val total: Int? = null,
    val totalPages: Int? = null,
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
    val vendor_product_id: String? = null,
    val variants: List<Variant>? = null
)

@Serializable
data class Variant(
    val id: String? = null,
    val product_id: String? = null,
    val variant_id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val attributes: Map<String, String>? = null,
    val price: String? = null,
    val mrp: String? = null,
    val sku: String? = null,
    val stock: Int? = null,
    val img_urls: List<String>? = null,
    val is_active: Boolean? = null,
    val vendor_variant_id: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

@Serializable
data class Vendor(
    val name: String? = null,
)

//    parameter("conversation_id", conversationId)
//            parameter("message", content)


@Serializable
data class SendMessage(val conversation_id: String, val message: String)



