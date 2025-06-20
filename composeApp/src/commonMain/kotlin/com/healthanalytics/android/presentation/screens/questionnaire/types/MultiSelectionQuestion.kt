package com.healthanalytics.android.presentation.screens.questionnaire.types

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.healthanalytics.android.constants.HumanTokenConstants
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
fun MultiSelectionQuestion(
    questionnaire: Question,
    viewModel: QuestionnaireViewModel,
) {
    val gridCount = if (questionnaire.data_type == ViewConstants.DATA_TYPE_GRID_2) 2 else 1

    MultiSelectionQuestion(
        answers = questionnaire.answers,
        gridCount = gridCount,
        questionnaire = questionnaire,
        onOptionSelected = { selected ->
            viewModel.addSelectedAnswer(selected)
            viewModel.updateCheckboxQuestion(selected.id.toString(), true)
            checkSelectedAnswersList(viewModel = viewModel)
        },
        onOptionUnSelected = { unSelected ->
            viewModel.removeSelectedAnswer(unSelected)
            viewModel.updateCheckboxQuestion(unSelected.id.toString(), false)
            checkSelectedAnswersList(viewModel = viewModel)
        }
    )
}

private fun checkSelectedAnswersList(viewModel: QuestionnaireViewModel) {
    val currentQuestion = viewModel.currentQuestion
    val defaultNextQuestion =
        currentQuestion?.default_next_question_id?.let { viewModel.getLastQuestion(it) }

    if (defaultNextQuestion == null) {
        viewModel.saveNextButtonName(QuestionnaireConstants.FINISH)
        viewModel.saveIsLastQuestion(true)
    } else {
        viewModel.saveNextButtonName(QuestionnaireConstants.NEXT)
        viewModel.saveIsLastQuestion(false)
    }
}

@Composable
fun MultiSelectionQuestion(
    modifier: Modifier = Modifier,
    answers: List<Answers>?,
    gridCount: Int,
    onOptionSelected: (Answers) -> Unit,
    onOptionUnSelected: (Answers) -> Unit,
    questionnaire: Question,
) {
    val selectedOptions = remember(answers) { mutableStateListOf(Answers()) }
    val spanCount = GridItemSpan(currentLineSpan = gridCount)
    val textAlign = if (gridCount == 1) TextAlign.Start else TextAlign.Center

    val options = answers?.filter { it.show_option }?.sortedBy { it.sequence } ?: arrayListOf()
    val answeredOptions = options.filter {
        it.is_selected == true
    }
    selectedOptions.addAll(answeredOptions)

    LaunchedEffect(key1 = questionnaire) {
        options.forEach {
            if (it.is_selected == true) {
                onOptionSelected(it)
            }
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(count = gridCount),
        modifier = modifier.padding(bottom = Dimensions.size80dp)
    ) {
        item(
            key = "question_section",
            span = { spanCount }
        ) {
            QuestionSection(question = questionnaire)
        }
        itemsIndexed(options) { _, option ->
            val isSelected = selectedOptions.contains(option)
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = modifier
                    .questionCardPadding()
                    .onColumnClick(
                        onClick = {
                            validateSelectedOption(
                                onOptionSelected = onOptionSelected,
                                onOptionUnSelected = onOptionUnSelected,
                                option = option,
                                selectedOptions = selectedOptions,
                                options = options
                            )
                        }
                    )
                    .setBorder(isSelected)
                    .questionCardBackground()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = modifier
                            .weight(1f)
                            .padding(end = Dimensions.size12dp)
                    ) {
                        OptionName(
                            name = option.value,
                            textAlign = textAlign
                        )
                        OptionDescription(
                            description = option.description,
                            textAlign = textAlign
                        )
                    }

                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = modifier.size(Dimensions.size24dp),
                        tint = if (isSelected) AppColors.primaryColor else AppColors.gray
                    )
                }
            }
        }
    }
}

fun validateSelectedOption(
    onOptionSelected: (Answers) -> Unit,
    onOptionUnSelected: (Answers) -> Unit,
    option: Answers,
    selectedOptions: SnapshotStateList<Answers>,
    options: List<Answers>
) {
    val value = option.value?.lowercase()?.trim()

    when (value) {
        HumanTokenConstants.NONE,
        HumanTokenConstants.NONE_OF_THE_ABOVE,
        HumanTokenConstants.ALL_OF_THE_ABOVE,
        HumanTokenConstants.I_DO_NOT_HAVE_ANY_TROUBLE_CONTROLLING_URINE,
        HumanTokenConstants.I_DO_NOT_EXPERIENCE_ANY_DISCOMFORT
            -> {
            if (selectedOptions.contains(option)) {
                onOptionUnSelected(option)
                selectedOptions.remove(option)
            } else {
                options.forEachIndexed { _, answer ->
                    if (answer.is_selected == true) {
                        if (answer.value?.lowercase()?.trim() != HumanTokenConstants.NONE ||
                            answer.value.lowercase()
                                .trim() != HumanTokenConstants.NONE_OF_THE_ABOVE ||
                            answer.value.lowercase()
                                .trim() != HumanTokenConstants.ALL_OF_THE_ABOVE ||
                            answer.value.lowercase()
                                .trim() != HumanTokenConstants.I_DO_NOT_HAVE_ANY_TROUBLE_CONTROLLING_URINE ||
                            answer.value.lowercase()
                                .trim() != HumanTokenConstants.I_DO_NOT_EXPERIENCE_ANY_DISCOMFORT
                        ) {
                            if (answer.is_selected == true) {
                                onOptionUnSelected(answer)
                                selectedOptions.remove(answer)
                            }
                        }
                    }
                }
                onOptionSelected(option)
                selectedOptions.add(option)
            }
        }

        else -> {
            if (selectedOptions.contains(option)) {
                onOptionUnSelected(option)
                selectedOptions.remove(option)
            } else {
                options.forEachIndexed { _, answer ->
                    if (answer.value?.lowercase()?.trim() == HumanTokenConstants.NONE ||
                        answer.value?.lowercase()
                            ?.trim() == HumanTokenConstants.NONE_OF_THE_ABOVE ||
                        answer.value?.lowercase()?.trim() == HumanTokenConstants.ALL_OF_THE_ABOVE ||
                        answer.value?.lowercase()
                            ?.trim() == HumanTokenConstants.I_DO_NOT_HAVE_ANY_TROUBLE_CONTROLLING_URINE ||
                        answer.value?.lowercase()
                            ?.trim() == HumanTokenConstants.I_DO_NOT_EXPERIENCE_ANY_DISCOMFORT
                    ) {
                        onOptionUnSelected(answer)
                        selectedOptions.remove(answer)
                    }
                }
                onOptionSelected(option)
                selectedOptions.add(option)
            }
        }
    }
}