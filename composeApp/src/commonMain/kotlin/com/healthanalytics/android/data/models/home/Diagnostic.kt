package com.healthanalytics.android.data.models.home


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Diagnostic(
    @SerialName("blood_product_list")
    val bloodProductList: BloodProductList? = BloodProductList(),
)

@Serializable
data class BloodProductList(
    @SerialName("packages")
    val packages: List<Package>? = listOf(),
    @SerialName("test_profiles")
    val testProfiles: List<String?>? = listOf(),
    @SerialName("tests")
    val tests: List<Test>? = listOf(),
)

@Serializable
data class Content(
    @SerialName("why")
    val why: String? = null,
)

@Serializable
data class DiOrder(
    @SerialName("address_id")
    val addressId: String? = null,
    @SerialName("appointment_date")
    val appointmentDate: String? = null,
    @SerialName("barcode")
    val barcode: String? = null,
    @SerialName("cancellation_reason")
    val cancellationReason: String? = null,
    @SerialName("cancelled_at")
    val cancelledAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("is_manual")
    val isManual: Boolean? = null,
    @SerialName("meta_data")
    val metaData: MetaData? = null,
    @SerialName("order_id")
    val orderId: String? = null,
    @SerialName("product_id")
    val productId: String? = null,
    @SerialName("ref_order_id")
    val refOrderId: String? = null,
    @SerialName("rescheduled_at")
    val rescheduledAt: String? = null,
    @SerialName("source")
    val source: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("tracking_id")
    val trackingId: String? = null,
    @SerialName("tracking_link")
    val trackingLink: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("whatsapp_msg_sent_at")
    val whatsappMsgSentAt: String? = null,
)

@Serializable
class MetaData

@Serializable
data class Package(
    @SerialName("code")
    val code: String? = null,
    @SerialName("content")
    val content: Content? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("di_order")
    val diOrder: DiOrder? = null,
    @SerialName("fasting_duration_hr")
    val fastingDurationHr: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("img_url")
    val imgUrl: String? = null,
    @SerialName("is_fasting_required")
    val isFastingRequired: Boolean? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("packageProduct")
    val packageProduct: PackageProduct? = null,
    @SerialName("report_generation_hr")
    val reportGenerationHr: String? = null,
    @SerialName("sample_type")
    val sampleType: String? = null,
    @SerialName("tags")
    val tags: List<String?>? = null,
    @SerialName("tests")
    val tests: List<PackageTest?>? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
)

@Serializable
data class PackageProduct(
    @SerialName("category")
    val category: List<String?>? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("img_urls")
    val imgUrls: List<String?>? = null,
    @SerialName("is_active")
    val isActive: Boolean? = null,
    @SerialName("meta_data")
    val metaData: String? = null,
    @SerialName("mrp")
    val mrp: String? = null,
    @SerialName("n_rating")
    val nRating: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("price")
    val price: String? = null,
    @SerialName("product_id")
    val productId: String? = null,
    @SerialName("rating")
    val rating: String? = null,
    @SerialName("sku")
    val sku: String? = null,
    @SerialName("stock")
    val stock: Int? = null,
    @SerialName("tags")
    val tags: List<String?>? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("vendor_id")
    val vendorId: String? = null,
    @SerialName("vendor_product_id")
    val vendorProductId: String? = null,
)

@Serializable
data class TestProduct(
    @SerialName("category")
    val category: List<String?>? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("img_urls")
    val imgUrls: List<String?>? = null,
    @SerialName("is_active")
    val isActive: Boolean? = null,
    @SerialName("meta_data")
    val metaData: String? = null,
    @SerialName("mrp")
    val mrp: String? = null,
    @SerialName("n_rating")
    val nRating: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("price")
    val price: String? = null,
    @SerialName("product_id")
    val productId: String? = null,
    @SerialName("rating")
    val rating: String? = null,
    @SerialName("sku")
    val sku: String? = null,
    @SerialName("stock")
    val stock: Int? = null,
    @SerialName("tags")
    val tags: List<String?>? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("vendor_id")
    val vendorId: String? = null,
    @SerialName("vendor_product_id")
    val vendorProductId: String? = null,
)

@Serializable
data class PackageTest(
    @SerialName("code")
    val code: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
)

@Serializable
data class Test(
    @SerialName("code")
    val code: String? = "",
    @SerialName("content")
    val content: String? = null,
    @SerialName("created_at")
    val createdAt: String? = "",
    @SerialName("description")
    val description: String? = null,
    @SerialName("di_order")
    val diOrder: String? = null,
    @SerialName("fasting_duration_hr")
    val fastingDurationHr: String? = null,
    @SerialName("id")
    val id: String? = "",
    @SerialName("is_fasting_required")
    val isFastingRequired: Boolean? = false,
    @SerialName("name")
    val name: String? = "",
    @SerialName("packageProduct")
    val product: TestProduct? = TestProduct(),
    @SerialName("report_generation_hr")
    val reportGenerationHr: String? = null,
    @SerialName("sample_type")
    val sampleType: String? = null,
    @SerialName("tags")
    val tags: String? = null,
    @SerialName("tests")
    val tests: List<String?>? = listOf(),
    @SerialName("type")
    val type: String? = "",
    @SerialName("updated_at")
    val updatedAt: String? = "",
)