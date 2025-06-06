package com.healthanalytics.android.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Recommendations(
    val recommendations: List<Recommendation>? = null,
)

@Serializable
data class Recommendation(
    val id: String,
    val category: String?,
    val name: String,
    val description: String?,
    val difficulty: String?,
    val is_generic: Boolean,
    val created_at: String,
    val updated_at: String,
    val metric_recommendations: List<MetricRecommendation>? = listOf(),
    //val recommendation_assessments: List<Any>?,
    val actions: List<Action>?,
    //val personalized_recommendations: List<Any>?
)

@Serializable
data class MetricRecommendation(
    val metric_id: String,
    val recommendation_id: String,
    val metric: Metric,
)

@Serializable
data class Metric(
    val metric_id: String,
    val metric: String,
)

@Serializable
data class Action(
    val id: String,
    val type: String,
    val product_id: String?,
    val event_config: EventConfig,
    val test_id: String?,
    val recommendation_id: String,
    val is_generic: Boolean,
    val created_at: String,
    val updated_at: String,
    // val user_recommendation_actions: List<Any>?
)

@Serializable
data class EventConfig(
    val type: String,
    val hours: String,
    val sub_type: String,
    val frequency: String,
    val scheduled_time: String,
    val days_of_the_week: List<Int>,
)

enum class RecommendationCategory(val icon: String) {
    ACTIVITY("🏃"),
    NUTRITION("🍽️"),
    SLEEP("🛌"),
    STRESS("😰"),
    SUPPLEMENTS("💊"),
    RECOVERY("♻️");

    companion object {
        fun fromString(value: String?): RecommendationCategory {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: ACTIVITY
        }
    }
}