package com.healthanalytics.android.presentation.screens.questionnaire.types

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.healthanalytics.android.data.models.questionnaire.Question
import com.healthanalytics.android.modifier.onRowClick
import com.healthanalytics.android.presentation.screens.questionnaire.viewmodel.QuestionnaireViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ApplicableQuestion(
    modifier: Modifier = Modifier, question: Question, viewModel: QuestionnaireViewModel, index: Int
) {
    var notApplicable by remember(question) { mutableStateOf(false) }
    val answeredList = viewModel.getApplicableQuestionAnswer(question)

    LaunchedEffect(key1 = question) {
        viewModel.saveApplicableQuestion(question)
        if (answeredList?.isNotEmpty() == true) {
            viewModel.updateApplicableQuestion(question.id, true)
        } else {
            viewModel.updateApplicableQuestion(question.id, false)
        }
    }

    if (!notApplicable) {
        notApplicable = answeredList?.isNotEmpty() == true
    }

    if (index > 0) {
        Spacer(
            modifier = modifier
                .fillMaxWidth()
                .height(Dimensions.size40dp)
        )
    } else {
        Spacer(
            modifier = modifier
                .fillMaxWidth()
                .height(Dimensions.size26dp)
        )
    }

    ApplicableCheckBox(question = question,
        notApplicable = notApplicable,
        onApplicableQuestion = { (isApplicableQuestion, selectedQuestion) ->
            notApplicable = isApplicableQuestion
            if (notApplicable) {
                viewModel.updateApplicableQuestion(selectedQuestion.id, true)
                viewModel.removeQuestionFromSubQuestionList(selectedQuestion)
            } else {
                viewModel.updateApplicableQuestion(selectedQuestion.id, false)
            }
        })

    if (!notApplicable) {
        if (answeredList?.isEmpty() == true) {
            SetSubQuestions(
                subQuestions = question.sub_questions, viewModel = viewModel
            )
        }
    }
}

@Composable
fun ApplicableCheckBox(
    modifier: Modifier = Modifier,
    question: Question,
    notApplicable: Boolean,
    onApplicableQuestion: (Pair<Boolean, Question>) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.size16dp)
    ) {
        Text(
            text = question.value ?: "",
            fontSize = FontSize.textSize18sp,
            color = AppColors.textPrimary,
            fontFamily = FontFamily.medium(),
            textAlign = TextAlign.Start,
            modifier = modifier.fillMaxWidth()
        )
        Spacer(modifier = modifier.padding(top = Dimensions.size12dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.size12dp),
            modifier = modifier.onRowClick(
                onClick = {
                    onApplicableQuestion(Pair(!notApplicable, question))
                }
            )
        ) {
            val image = if (notApplicable) {
                Icons.Rounded.Check
            } else {
                Icons.Rounded.RadioButtonUnchecked
            }
            Image(
                imageVector = image,
                contentDescription = null,
                modifier = modifier.size(Dimensions.size24dp)
            )
            Text(
                text = question.answers?.firstOrNull()?.value ?: "",
                fontSize = FontSize.textSize14sp,
                color = AppColors.textSecondary,
                fontFamily = FontFamily.medium(),
                textAlign = TextAlign.Start,
                modifier = modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SetSubQuestions(
    subQuestions: MutableList<Question>?,
    viewModel: QuestionnaireViewModel,
) {
    subQuestions?.forEachIndexed { index, question ->
        SelectBoxQuestion(
            question = question,
            viewModel = viewModel,
            showAsDescription = true,
            index = index
        )
    }
}

@Preview
@Composable
private fun PreviewApplicableCheckBox() {
    Box(
        modifier = Modifier.background(
            color = AppColors.white
        )
    ) {}
}