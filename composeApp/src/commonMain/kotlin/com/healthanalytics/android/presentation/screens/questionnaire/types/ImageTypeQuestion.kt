package com.healthanalytics.android.presentation.screens.questionnaire.types

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.healthanalytics.android.constants.QuestionnaireConstants
import com.healthanalytics.android.constants.ViewConstants
import com.healthanalytics.android.data.models.questionnaire.Answers
import com.healthanalytics.android.data.models.questionnaire.Question
import com.healthanalytics.android.modifier.onColumnClick
import com.healthanalytics.android.presentation.screens.questionnaire.QuestionSection
import com.healthanalytics.android.presentation.screens.questionnaire.setBorder
import com.healthanalytics.android.presentation.screens.questionnaire.viewmodel.QuestionnaireViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions

@Composable
fun ImageTypeQuestion(
    modifier: Modifier = Modifier,
    questionnaire: Question,
    viewModel: QuestionnaireViewModel
) {
    val gridCount = if (questionnaire.data_type == ViewConstants.DATA_TYPE_GRID_3) 3 else 2

    SingleSelectionImage(
        modifier = modifier,
        answers = questionnaire.answers,
        gridCount = gridCount,
        questionnaire = questionnaire,
        onImageSelected = { selectedAnswer ->
            viewModel.saveNextQuestionIdAndAnswer(
                selectedId = selectedAnswer.next_question_id,
                selectedAnswer = selectedAnswer.id ?: ""
            )
            viewModel.updateRadioButtonQuestion(
                answerId = selectedAnswer.id ?: ""
            )
            checkLastQuestion(
                viewModel = viewModel,
                nextQuestionId = selectedAnswer.next_question_id
            )
            viewModel.saveNextButtonState(enable = true)
        }
    )
}

private fun checkLastQuestion(viewModel: QuestionnaireViewModel, nextQuestionId: String?) {
    val currentQuestion = viewModel.currentQuestion

    val defaultNextQuestion =
        currentQuestion?.default_next_question_id?.let { viewModel.getLastQuestion(it) }
    val nextQuestion = nextQuestionId?.let { viewModel.getLastQuestion(it) }

    if (defaultNextQuestion == null && nextQuestion == null) {
        viewModel.saveNextButtonName(QuestionnaireConstants.FINISH)
        viewModel.saveIsLastQuestion(true)
    } else {
        viewModel.saveNextButtonName(QuestionnaireConstants.NEXT)
        viewModel.saveIsLastQuestion(false)
    }
}

@Composable
fun SingleSelectionImage(
    modifier: Modifier = Modifier,
    answers: List<Answers>?,
    gridCount: Int,
    onImageSelected: (Answers) -> Unit,
    questionnaire: Question
) {
    var selectedIndex by remember(answers) { mutableIntStateOf(-1) }
    val options = answers?.filter { it.show_option }?.sortedBy { it.sequence }?: listOf()
    val spanCount = GridItemSpan(gridCount)

    LazyVerticalGrid(
        columns = GridCells.Fixed(count = gridCount),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = Dimensions.size6dp,
                end = Dimensions.size6dp,
                bottom = Dimensions.size80dp
            ),
    ) {
        item(
            key = "question_section",
            span = { spanCount }
        ) {
            QuestionSection(
                question = questionnaire,
                horizontalPadding = Dimensions.size10dp
            )
        }

        itemsIndexed(options) { index, answer ->
            if (selectedIndex == -1 && answer.is_selected == true) {
                selectedIndex = index
                onImageSelected(answer)
            }
            Column(
                modifier = modifier
                    .padding(
                        vertical = Dimensions.size10dp,
                        horizontal = Dimensions.size10dp,
                    )
                    .setBorder(selectedIndex == index)
                    .onColumnClick(
                        onClick = {
                            selectedIndex = index
                            onImageSelected(answer)
                        }
                    )
                    .background(
                        color = AppColors.white,
                        shape = RoundedCornerShape(Dimensions.size12dp)
                    )
            ) {
//                AsyncImage(
//                    model = answer.value,
//                    contentDescription = null,
//                    modifier = modifier.height(Dimensions.size100dp)
//                )
            }
        }
    }
}


