package com.healthanalytics.android.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector
import com.healthanalytics.android.utils.AppConstants
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_sleep
import humantokendashboardv1.composeapp.generated.resources.ic_stress
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource

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

    val user_recommendation_actions: List<UserRecommendationAction?> = listOf(),
)

@Serializable
data class EventConfig(
    val type: String,
    val hours: String,
    val sub_type: String,
    val frequency: String,
    val scheduled_time: String,
    val shape: String? = null,
    val color: String? = null,
    val duration: Int? = null,
    val days_of_the_week: List<Int>,
)

@Serializable
data class UserRecommendationAction(
    val action_id: String? = null,
    val created_at: String? = null,
    val event_id: String? = null,
    val id: String? = null,
    val is_completed: Boolean? = null,
    val medicine_id: String? = null,
    val recommendation_id: String? = null,
    val updated_at: String? = null,
    val user_id: String? = null,
)

sealed class RecommendationIcon {
    data class Vector(val imageVector: ImageVector) : RecommendationIcon()
    data class Painter(val resource: DrawableResource) : RecommendationIcon()
}

sealed class RecommendationCategoryes {
    abstract val icon: RecommendationIcon

    data object Activity : RecommendationCategoryes() {
        override val icon = RecommendationIcon.Vector(Icons.Filled.DirectionsRun)
    }

    data object Nutrition : RecommendationCategoryes() {
        override val icon = RecommendationIcon.Vector(Icons.Outlined.Restaurant)
    }

    data object Sleep : RecommendationCategoryes() {
        override val icon = RecommendationIcon.Painter(Res.drawable.ic_sleep)
    }

    data object Stress : RecommendationCategoryes() {
        override val icon = RecommendationIcon.Painter(Res.drawable.ic_stress)
    }

    data object Supplements : RecommendationCategoryes() {
        override val icon = RecommendationIcon.Vector(Icons.Filled.Medication)
    }

    data object Recovery : RecommendationCategoryes() {
        override val icon = RecommendationIcon.Vector(Icons.Outlined.Help)
    }

    companion object {
        fun fromString(value: String?): RecommendationCategoryes = when (value?.lowercase()) {
            AppConstants.ACTIVITY -> Activity
            AppConstants.NUTRITION -> Nutrition
            AppConstants.SLEEP -> Sleep
            AppConstants.STRESS -> Stress
            AppConstants.SUPPLEMENTS -> Supplements
            AppConstants.RECOVERY -> Recovery
            else -> Recovery
        }

    }
}











