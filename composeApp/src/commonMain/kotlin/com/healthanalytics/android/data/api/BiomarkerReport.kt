package com.healthanalytics.android.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BiomarkerReportData(
    @SerialName("name") val name: String? = null,
    @SerialName("display_rating") val displayRating: String? = null,
    @SerialName("ranges") val ranges: List<Range>? = null,
    @SerialName("released_at") val releasedAt: String? = null,
    @SerialName("value") val value: Double? = null,
    @SerialName("unit") val unit: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("metric_id") val metricId: String? = null,
    @SerialName("metric_data") val metricData: List<MetricData>? = null,
    @SerialName("wellness_categories") val wellnessCategories: List<WellnessCategory>? = null,
    @SerialName("reported_symptoms") val reportedSymptoms: List<ReportedSymptom>? = null
)

@Serializable
data class MetricData(
    @SerialName("id") val id: String? = null,
    @SerialName("metric_id") val metricId: String? = null,
    @SerialName("category") val category: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("content") val content: String? = null,
    @SerialName("key_points") val keyPoints: List<String?> = listOf(),
    @SerialName("subgroups") val subgroups: Subgroups? = null,
    @SerialName("notes") val notes: String? = null,
    @SerialName("content_type") val contentType: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("causes") val causes: List<Cause>? = null
)

@Serializable
data class Subgroups(
    @SerialName("decrease") val decrease: List<String>? = null,
    @SerialName("increase") val increase: List<String>? = null
)

@Serializable
data class Cause(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("tag") val tag: List<String>? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("description") val description: String? = null
)

@Serializable
data class ReportedSymptom(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("category") val category: String? = null,
    @SerialName("metric_inferences") val metricInferences: List<String>? = null,
    @SerialName("count") val count: Int? = null,
    @SerialName("reported_at") val reportedAt: String? = null
)

@Serializable
data class WellnessCategory(
    @SerialName("name") val name: String? = null,
    @SerialName("impact") val impact: String? = null,
    @SerialName("description") val description: String? = null
)