package com.healthanalytics.android.data.models.questionnaire

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
data class HumanToken(
    val ht_categories: List<HtCategory>? = null,
    val is_getting_started: Boolean? = null,
    val level: Level? = null,
    val streak: Int? = null,
    val has_past_record: Boolean? = null,
    val last_sleep_at: String? = null,
    var suggested_foods: List<SuggestedFoods>? = null,
    var user_meta: List<String>? = null,
    val daily_check: DailyCheck? = null,
    var user_session: UserSession? = null,
    var cgm_expiry: CGMExpiry? = null,
    val claim_token: ClaimToken? = null,
    val device_setup: SetupDevice? = null,
    val assessment_info: AssessmentInfo? = null
)


@Serializable
data class LastMeasurementSyncAt(
    val sleep: String? = "",
    val heart_rate: String? = "",
    val heart_rate_variability: String? = "",
    val activity: String? = "",
    val blood_oxygen: String? = "",
    val body_temperature: String? = "",
    val stress: String? = ""
)


@Serializable
data class AssessmentInfo(
    val assessment_list: List<Assessment>? = null
)

@Serializable
data class Assessment(
    val assessment_id: String? = null,
    val assessment_name: String? = null,
    val total_questions: String? = null,
    val answered_questions: String? = null,
    val estimated_completion_time: String? = null,
    val status: String? = null,
    val next_question_id: String? = null,
    val last_updated_at: String? = null,
)

data class AssessmentState(
    val total: String? = null,
    val completed: String? = null,
    val name: String? = null,
    val remainingTime: String? = null,
    val progress: Float? = null,
    val state: String? = null,
    val assessmentId: String? = null,
    val nextQuestionId: String? = null
)

@Serializable
data class ClaimToken(
    val estimated_token_delivery_at: String? = null,
    val feedback_questionnaire: FeedbackQuestionnaire? = null,
    val ht_delivery_status: List<HtDeliveryStatus>? = null,
    val post_token_assessment: PostTokenAssessment? = null,
)


@Serializable
data class PostTokenAssessment(
    val answered_questions: Int? = null,
    val assessment_id: String? = null,
    val assessment_name: String? = null,
    val estimated_completion_time: Int? = null,
    val last_updated_at: String? = null,
    val next_question_id: String? = null,
    val status: String? = null,
    val total_questions: Int? = null,
)

@Serializable
data class FeedbackQuestionnaire(
    val assessment_id: String? = null,
    val assessment_name: String? = null,
    val completed_at: String? = null,
    val is_enabled: Boolean? = null,
    val next_question_id: String? = null
)


@Serializable
data class HtDeliveryStatus(
    val ht_user_delivery_statuses: List<HtUserDeliveryStatuse>? = null,
    val id: String? = null,
    val is_optional: Boolean? = null,
    val display_name_complete: String? = null,
    val display_name_incomplete: String? = null,
    val name: String? = null,
    val type: String? = null
)

@Serializable
data class HtUserDeliveryStatuse(
    val delivery_status_id: String? = null,
    val end_time: String? = null,
    val id: String? = null,
    val meta: Meta? = null,
    val start_time: String? = null,
    val user_id: String? = null,
    val status: String? = null,
    val reason: String? = null,
)

data class LocalDeliveryStatus(
    val position: Int, val localStatus: HtDeliveryStatus? = null
)


@Serializable
data class Meta(
    val tracking_id: String? = null, val tracking_link: String? = null
)

@Serializable
data class CGMExpiry(
    var user_action: String? = null,
    var tracking_status: String? = null,
    val tracking_link: String? = null,
    val threshold_met: Boolean? = null,
)

@Serializable
data class DailyCheck(
    val alcohol: Alcohol? = null, val questionnaire: DailyQuestionnaire? = null
)


@Serializable
data class DailyQuestionnaire(
    val is_enabled: Boolean? = null,
    val assessment_id: String? = null,
    val completed_at: String? = null,
    val next_question_id: String? = null,
)


@Serializable
data class Alcohol(
    val is_enabled: Boolean? = null,
    val assessment_id: String? = null,
)

@Serializable
data class UserSession(
    val access_token: String? = null,
    val app_update_type: String? = null,
    val app_version: String? = null,
    val app_version_header: String? = null,
    val created_at: String? = null,
    val fcm_device_token: String? = null,
    val refresh_token: String? = null,
    val updated_at: String? = null
)

@Serializable
data class SuggestedFoodData(
    var ht_food_suggestions: List<SuggestedFoods>? = null,
)

@Immutable

data class SuggestedFoodsGroup(
    val group: String? = null,
    val category: String? = null,
    val suggestedFoods: List<SuggestedFoods> = listOf(),
)


@Serializable
data class SuggestedFoods(
    val id: String? = null,
    val group: String? = null,
    val category: String? = null,
    val name: String? = null,
    val number_of_units: String? = null,
    val measurement_description: String? = null,
    val metric_serving_amount: String? = null,
    val metric_serving_unit: String? = null,
    val n_intake_units: String? = null,
    val description: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val is_consumable: Boolean? = null,
    val allergy_contraindication: List<String>? = null,
    val medical_contraindication: List<String>? = null,
    val preparation_method: List<String>? = null,
    val food_preference_contraindication: List<String>? = null,
    val intolerance_contraindication: List<String>? = null,
)

data class HaAddFoodImages(
    val img_url: String? = null,
    var isAdd: Boolean = false, // local
)



@Serializable
data class TrackingShipment(
    val device_kit: DeviceKit? = null
)

@Serializable
data class DeviceKit(
    val id: String? = null, val link: String? = null, val status: String? = null
)

@Serializable
data class Level(
    val description: String? = null, val id: String? = null, val name: String? = null
)

@Serializable
data class HtCategory(
    val display_name: String? = null,
    val ht_milestones: MutableList<HtMilestone>? = null,
    val n_milestone: Int? = null,
    val n_task: Int? = null,
    val id: String? = null,
    val sub_type: String? = null,
    val type: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
)

@Serializable
data class SetupDevice(
    var is_device_setup_completed: Boolean? = null
)

@Serializable
data class HtTask(
    val milestone_id: String? = null,
    val assessment_id: String? = null,
    val description: String? = null,
    val id: String? = null,
    val meal_id: String? = null,
    val name: String? = null,
    val sequence: Int? = null,
    var type: String? = null,
    var ht_user_tasks: MutableList<HtUserTask>? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
)

@Serializable
data class HtUserTask(
    var completed_at: String? = null,
    val id: String? = null,
    var is_cgm_data_present: Boolean? = null,
    val next_question_id: String? = null,
    val task_id: String? = null,
    val user_id: String? = null,
    val activity_id: String? = null,
    val is_skipped: Boolean? = null,
    var created_at: String? = null,
    var scheduled_at: String? = null,
    val updated_at: String? = null,
    val recorded_at: String? = null
)

@Serializable
data class HtMilestone(
    val category_id: String? = null,
    val ht_tasks: MutableList<HtTask>? = null,
    val ht_user_milestones: MutableList<UserMilestone>? = null,
    val id: String? = null,
    val level_id: String? = null,
    val sequence: Int? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
)


@Serializable
data class UserMilestone(
    val id: String? = null,
    val user_id: String? = null,
    val milestone_id: String? = null,
    val completed_at: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

