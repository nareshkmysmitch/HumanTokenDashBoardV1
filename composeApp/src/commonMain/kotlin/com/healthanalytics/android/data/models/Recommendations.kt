package com.healthanalytics.android.data.models

data class Recommendations(
    val recommendations: List<Recommendation>? = null
)

data class Recommendation(
    val actions: List<Action?>? = null,
    val category: String? = null,
    val created_at: String? = null,
    val description: String? = null,
    val difficulty: String? = null,
    val id: String? = null,
    val is_generic: Boolean? = null,
    val metric_recommendations: List<MetricRecommendation?>? = null,
    val name: String? = null,
    val personalized_recommendations: List<PersonalizedRecommendation?>? = null,
    val recommendation_assessments: List<Any?>? = null,
    val updated_at: String? = null
)

data class Action(
    val created_at: String? = null,
    val event_config: EventConfig? = null,
    val id: String? = null,
    val is_generic: Boolean? = null,
    val product_id: Any? = null,
    val recommendation_id: String? = null,
    val test_id: Any? = null,
    val type: String? = null,
    val updated_at: String? = null,
    val user_recommendation_actions: List<UserRecommendationAction?>? = null
)

data class MetricRecommendation(
    val metric: Metric? = null,
    val metric_id: String? = null,
    val recommendation_id: String? = null
)

data class EventConfig(
    val color: String? = null,
    val days_of_the_week: List<Int?>? = null,
    val duration: Int? = null,
    val frequency: String? = null,
    val hours: String? = null,
    val scheduled_time: String? = null,
    val shape: String? = null,
    val sub_type: String? = null,
    val type: String? = null
)

data class Metric(
    val metric: String? = null,
    val metric_id: String? = null
)

data class PersonalizedRecommendation(
    val created_at: String? = null,
    val id: String? = null,
    val parent_recommendation_id: String? = null,
    val recommendation_id: String? = null,
    val updated_at: String? = null,
    val user_id: String? = null
)

data class UserRecommendationAction(
    val action_id: String? = null,
    val created_at: String? = null,
    val event_id: String? = null,
    val id: String? = null,
    val is_completed: Boolean? = null,
    val medicine_id: Any? = null,
    val recommendation_id: String? = null,
    val updated_at: String? = null,
    val user_id: String? = null
)