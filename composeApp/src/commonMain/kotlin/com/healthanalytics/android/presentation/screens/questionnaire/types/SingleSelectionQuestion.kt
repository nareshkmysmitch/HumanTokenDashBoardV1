package com.healthanalytics.android.presentation.screens.questionnaire.types

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.healthanalytics.android.constants.QuestionnaireConstants
import com.healthanalytics.android.constants.ViewConstants
import com.healthanalytics.android.data.models.questionnaire.Answers
import com.healthanalytics.android.data.models.questionnaire.Question
import com.healthanalytics.android.modifier.onColumnClick
import com.healthanalytics.android.presentation.screens.questionnaire.OptionDescription
import com.healthanalytics.android.presentation.screens.questionnaire.OptionName
import com.healthanalytics.android.presentation.screens.questionnaire.QuestionSection
import com.healthanalytics.android.presentation.screens.questionnaire.questionCardBackground
import com.healthanalytics.android.presentation.screens.questionnaire.questionCardPadding
import com.healthanalytics.android.presentation.screens.questionnaire.setBorder
import com.healthanalytics.android.presentation.screens.questionnaire.viewmodel.QuestionnaireViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions

@Composable
fun SingleSelectionQuestion(
    questionnaire: Question,
    viewModel: QuestionnaireViewModel,
) {
    val gridCount = if (questionnaire.data_type == ViewConstants.DATA_TYPE_GRID_2) 2 else 1

    SingleSelection(
        answers = questionnaire.answers,
        gridCount = gridCount,
        questionnaire = questionnaire,
        onOptionSelected = { selectedAnswer ->
            viewModel.saveNextQuestionIdAndAnswer(
                selectedId = selectedAnswer.next_question_id,
                selectedAnswer = selectedAnswer.id ?: ""
            )
            viewModel.updateRadioButtonQuestion(selectedAnswer.id ?: "")
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
fun SingleSelection(
    modifier: Modifier = Modifier,
    answers: List<Answers>?,
    gridCount: Int,
    onOptionSelected: (Answers) -> Unit,
    questionnaire: Question
) {
    val options = answers?.filter { it.show_option }?.sortedBy { it.sequence } ?: listOf()
    var selectedIndex by remember(answers) { mutableIntStateOf(-1) }

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = Dimensions.size80dp),
        columns = GridCells.Fixed(count = gridCount),
    ) {
        item(
            key = questionnaire.value, span = { GridItemSpan(currentLineSpan = gridCount) }) {
            QuestionSection(question = questionnaire)
        }

        itemsIndexed(items = options) { index, answer ->
            if (selectedIndex == -1 && answer.is_selected == true) {
                selectedIndex = index
                onOptionSelected(answer)
            }
            val isSelected = selectedIndex == index
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = modifier
                    .questionCardPadding()
                    .setBorder(isSelected)
                    .onColumnClick(
                        onClick = {
                            selectedIndex = index
                            onOptionSelected(answer)
                        }
                    )
                    .questionCardBackground()
            ) {
                val textAlign = if (gridCount == 1) TextAlign.Start else TextAlign.Center

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        OptionName(
                            name = answer.value,
                            textAlign = textAlign
                        )
                        OptionDescription(
                            description = answer.description,
                            textAlign = textAlign
                        )
                    }

                    Spacer(modifier = Modifier.width(Dimensions.size14dp))

                    RadioButton(
                        modifier = Modifier.size(Dimensions.size20dp),
                        selected = isSelected,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = AppColors.primaryTextColor,
                            unselectedColor = AppColors.gray,
                            disabledSelectedColor = AppColors.gray,
                            disabledUnselectedColor = AppColors.gray
                        ),
                        onClick = {
                            selectedIndex = index
                            onOptionSelected(answer)
                        }
                    )
                }
            }
        }
    }
}