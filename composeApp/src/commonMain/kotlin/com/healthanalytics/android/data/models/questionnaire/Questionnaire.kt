package com.healthanalytics.android.data.models.questionnaire

import androidx.compose.ui.text.input.KeyboardType
import kotlinx.serialization.Serializable

@Serializable
data class Questionnaire(
    var ha_questions: List<Question>? = null,
    var assessment_id: String? = null,
    val is_completed: Boolean? = null,
    var tags: List<Tags>? = null,
    var nextQuestionId: Int? = null,
    val category: String? = null,
    val next_question_id: Int? = null
) 


@Serializable
data class Question(
    var id: String? = null,
    var primary_question_id: String? = null,
    var previous_question_id: String? = null,
    var type: String? = null,
    var data_type: String? = null,
    var input_element: String? = null,
    var value: String? = null,
    var default_next_question_id: String? = null,
    var text_answer: String? = null,
    var answers: List<Answers>? = null,
    var is_first_question: Boolean? = null,
    var tag: List<String>? = null,
    var required: Boolean? = null,
    var is_selected: Boolean = false,
    var is_answered: Boolean = false,
    var is_uploaded: Boolean = false,
    var firstQuestion: Boolean = false,
    var category: String? = null,
    var description: String? = null,
    var sub_type: String? = null,
    var sub_questions: MutableList<Question>? = null,
    var code: String? = null,
    var showError: Boolean? = null,
)  {
    override fun toString(): String {
        return "Question(id=$id, primary_question_id=$primary_question_id, previous_question_id=$previous_question_id, type=$type, data_type=$data_type, input_element=$input_element, value=$value, default_next_question_id=$default_next_question_id, text_answer=$text_answer, answers=$answers, is_first_question=$is_first_question, tag=$tag, required=$required, is_selected=$is_selected, is_answered=$is_answered, firstQuestion=$firstQuestion, category=$category, " +
                "description=$description, sub_type=$sub_type, showError=$showError)"
    }
}

@Serializable
data class Answers(
    val id: String? = null,
    val value: String? = null,
    val next_question_id: String? = null,
    val description: String? = null,
    var is_selected: Boolean? = null,
    var show_option: Boolean = true,
    var image_url: String? = null,
    var validation_unit: String? = null,
    var tag: List<String>? = null,
    var tag_render: List<String>? = null,
    var ha_questions: List<Question>? = null,
    var sub_questions: MutableList<Question>? = null,
    val sequence: Int? = null,
) 

@Serializable
data class Tags(
    var selectedQuestionTags: MutableList<String>? = null,
    val questionId: String? = null,
) 

data class QuestionnaireContinuation(
    val assessmentId: String,
    val displayName: String,
    val partName: String,
    val totalParts: Int,
)

@Serializable
data class SubmittedTags(
    val tags: List<String>? = null,
)

data class TextBoxDefaultValues(
    val keyboardType: KeyboardType,
    val maxLength: Int,
    val showUnit: Boolean,
    val unit: String,
    val placeHolder: String
)

@Serializable
data class QuestionnaireNextQuestionData(
    val assessmentId: String? = null,
    var nextQuestionId: Int? = null
)

data class OptionsUiState(
    var yes: Boolean = false,
    var no: Boolean = false
)

data class ContainerData(
    val category: String? = null,
    val question: List<Question?> = listOf(),
    var showError: Boolean? = null
)

data class SubQuestionData(
    val category: String? = null,
    val parentQuestion: Question? = null,
    val subQuestion: Question? = null,
)

data class BottomSheetQuestionState(
    val question: Question? = null,
    val parentQuestion: Question? = null,
    val showTextFieldBottomSheet: Boolean = false,
    var selectedValue: String = "",
    val selectedAnswer: Answers? = null,
    val showDropDownBottomSheet: Boolean = false,
    val showHowToMeasureBottomSheet: Boolean = false,
)

@Serializable
data class QuestionnaireUpdatedResponse(
   val isUpdated: Boolean?=null
)


