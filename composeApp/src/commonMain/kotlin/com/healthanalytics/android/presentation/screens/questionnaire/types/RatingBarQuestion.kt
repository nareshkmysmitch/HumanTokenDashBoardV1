package com.healthanalytics.android.presentation.screens.questionnaire.types

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.healthanalytics.android.data.models.questionnaire.Question
import com.healthanalytics.android.modifier.onIconClick
import com.healthanalytics.android.presentation.screens.questionnaire.questionCardPadding
import com.healthanalytics.android.presentation.screens.questionnaire.viewmodel.QuestionnaireViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RatingBarQuestion(
    questionnaire: Question,
    viewModel: QuestionnaireViewModel,
) {
    var rating by remember(questionnaire) { mutableIntStateOf(0) }

    val answersList =
        questionnaire.answers?.filter { it.show_option }?.sortedBy { it.sequence } ?: listOf()
    val maxStars = answersList.size

    val answeredAnswer = answersList.lastOrNull {
        it.is_selected == true
    }

    if (answeredAnswer != null && rating == 0) {
        rating = ((answeredAnswer.value?.toFloat()) ?: 0f).toInt()
        viewModel.updateRadioButtonQuestion(answeredAnswer.id.toString())
        viewModel.saveNextQuestionIdAndAnswer(
            selectedId = answeredAnswer.next_question_id,
            selectedAnswer = answeredAnswer.id ?: ""
        )
        viewModel.saveNextButtonState(true)
    }

    RatingBar(
        maxStars = maxStars,
        rating = rating,
        onRatingChanged = { input ->
            rating = input
            if (answersList.isNotEmpty()) {
                if (input > 0) {
                    val selectedOption = answersList[input - 1]
                    viewModel.saveNextQuestionIdAndAnswer(
                        selectedId = selectedOption.next_question_id,
                        selectedAnswer = selectedOption.id ?: ""
                    )
                    viewModel.updateRadioButtonQuestion(selectedOption.id.toString())
                    viewModel.saveNextButtonState(true)
                } else {
                    viewModel.saveNextButtonState(false)
                }
            }
        }
    )
}

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    maxStars: Int,
    rating: Int,
    onRatingChanged: (Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .questionCardPadding()
    ) {
        for (value in 1..maxStars) {
            val isSelected = value <= rating
            Image(
                imageVector = if (isSelected){
                    Icons.Rounded.Star
                }else{
                    Icons.Rounded.StarOutline
                },
                contentDescription = null,
                modifier = modifier
                    .size(
                        width = Dimensions.size40dp,
                        height = Dimensions.size40dp
                    )
                    .onIconClick(
                        onClick = {
                            onRatingChanged(value)
                        }
                    )
            )

            Spacer(
                modifier = modifier.padding(
                    start = Dimensions.size6dp
                )
            )
        }
    }
}

@Preview
@Composable
private fun RatingBarQuestionPreview() {
    RatingBar(
        modifier = Modifier.background(
            color = AppColors.white
        ),
        maxStars = 5,
        rating = 1,
        onRatingChanged = {},
    )
}

