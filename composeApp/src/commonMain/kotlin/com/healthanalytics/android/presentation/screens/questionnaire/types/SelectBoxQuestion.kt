package com.healthanalytics.android.presentation.screens.questionnaire.types

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.data.models.questionnaire.Answers
import com.healthanalytics.android.data.models.questionnaire.Question
import com.healthanalytics.android.modifier.onColumnClick
import com.healthanalytics.android.presentation.screens.questionnaire.questionCardBackground
import com.healthanalytics.android.presentation.screens.questionnaire.setBorder
import com.healthanalytics.android.presentation.screens.questionnaire.viewmodel.QuestionnaireViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize

@Composable
fun SelectBoxQuestion(
    modifier: Modifier = Modifier,
    question: Question,
    viewModel: QuestionnaireViewModel,
    showAsDescription: Boolean = false,
    index: Int
) {
    LaunchedEffect(key1 = question) {
        viewModel.saveSelectBoxQuestion(question)
    }

    var selectedQuestion: Question? by remember(question) {
        mutableStateOf(null)
    }

    if (selectedQuestion == null) {
        selectedQuestion = question
    }

    val fontSize = if (showAsDescription) {
        FontSize.textSize14sp
    } else {
        FontSize.textSize18sp
    }

    val fontColor = if (showAsDescription) {
        AppColors.textSecondary
    } else {
        AppColors.textPrimary
    }

    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(
                 if (showAsDescription) {
                    if (index > 0) {
                        Dimensions.size24dp
                    } else {
                        Dimensions.size16dp
                    }
                } else {
                    if (index > 0) {
                        Dimensions.size32dp
                    } else {
                        Dimensions.size26dp
                    }
                }
            )
    )

    Text(
        text = question.value ?: "",
        fontSize = fontSize,
        color = fontColor,
        fontFamily = FontFamily.medium(),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.size16dp)
    )

    Spacer(modifier = modifier.padding(top = Dimensions.size8dp))
    SelectBox(
        question = question,
        onOptionSelected = { (question, selectedAnswer) ->
            if (question.showError == true) {
                selectedQuestion = question.copy(showError = false)
            }
            question.id?.let { viewModel.updateSelectBoxQuestion(it, selectedAnswer.id.toString()) }
        }
    )

    if (selectedQuestion?.showError == true) {
        Spacer(modifier = modifier.padding(top = Dimensions.size8dp))
        Text(
            text = "Please select an option",
            fontSize = FontSize.textSize12sp,
            color = AppColors.error,
            fontFamily = FontFamily.semiBold(),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.size16dp)
        )
    }
}

@Composable
fun SelectBox(
    modifier: Modifier = Modifier,
    question: Question,
    onOptionSelected: (Pair<Question, Answers>) -> Unit
) {
    var selectedIndex by remember(question) { mutableIntStateOf(-1) }

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.size16dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val answers = question.answers?.filter { it.show_option }?.sortedBy { it.sequence }?: listOf()
        answers.forEachIndexed { index, answer ->
            if (selectedIndex == -1 && answer.is_selected == true) {
                selectedIndex = index
                onOptionSelected(Pair(question, answer))
            }
            Column(
                modifier = modifier
                    .width(Dimensions.size100dp)
                    .setBorder(selectedIndex == index)
                    .onColumnClick(
                        onClick = {
                            selectedIndex = index
                            onOptionSelected(Pair(question, answer))
                        }
                    )
                    .questionCardBackground(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = answer.value ?: "",
                    color = AppColors.textPrimary,
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.medium(),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}