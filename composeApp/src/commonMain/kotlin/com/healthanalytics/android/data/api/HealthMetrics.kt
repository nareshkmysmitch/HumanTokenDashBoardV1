package com.healthanalytics.android.data.api


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HealthMetrics(
    @SerialName("blood")
    val blood: Blood? = Blood(),
    @SerialName("gene")
    val gene: Gene? = Gene(),
    @SerialName("gut")
    val gut: Gut? = Gut(),
    @SerialName("supplement_ct")
    val supplementCt: SupplementCt? = SupplementCt()
)

@Serializable
data class Blood(
    @SerialName("data")
    val `data`: List<BloodData?>? = null
)

@Serializable
data class BloodData(
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("display_description")
    val displayDescription: String? = null,
    @SerialName("display_name")
    val displayName: String? = null,
    @SerialName("display_rating")
    val displayRating: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("identifier")
    val identifier: String? = null,
    @SerialName("metric_id")
    val metricId: String? = null,
    @SerialName("range")
    val range: String? = null,
    @SerialName("released_at")
    val releasedAt: String? = null,
    @SerialName("unit")
    val unit: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("value")
    val value: Double? = null
)

@Serializable
data class Gene(
    @SerialName("data")
    val `data`: List<GeneData>? = listOf()
)

@Serializable
data class GeneData(
    @SerialName("display_name")
    val displayName: String? = null,
    @SerialName("display_rating")
    val displayRating: String? = null,
    @SerialName("identifier")
    val identifier: String? = null,
    @SerialName("primary_display_description")
    val primaryDisplayDescription: String? = null,
    @SerialName("secondary_display_description")
    val secondaryDisplayDescription: String? = null
)

@Serializable
data class Gut(
    @SerialName("data")
    val `data`: List<GutData>? = listOf()
)

@Serializable
data class GutData(
    @SerialName("activity")
    val activity: String? = null,
    @SerialName("display_name")
    val displayName: String? = null,
    @SerialName("identifier")
    val identifier: String? = null,
    @SerialName("outcome")
    val outcome: String? = null,
    @SerialName("primary_display_description")
    val primaryDisplayDescription: String? = null,
    @SerialName("secondary_display_description")
    val secondaryDisplayDescription: String? = null
)

@Serializable
data class SupplementCt(
    @SerialName("data")
    val `data`: List<SupplementData>? = listOf()
)

@Serializable
data class SupplementData(
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("dosage")
    val dosage: String? = null,
    @SerialName("dosage_type")
    val dosageType: String? = null,
    @SerialName("duration")
    val duration: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("intraday_frequency")
    val intradayFrequency: Int? = null,
    @SerialName("supplement")
    val supplement: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("user_id")
    val userId: String? = null
)