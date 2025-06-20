package com.healthanalytics.android.presentation.screens.questionnaire

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.components.PrimaryButton
import com.healthanalytics.android.components.SecondaryButton
import com.healthanalytics.android.constants.QuestionnaireConstants
import com.healthanalytics.android.constants.ViewConstants
import com.healthanalytics.android.data.models.questionnaire.Question
import com.healthanalytics.android.data.models.questionnaire.Questionnaire
import com.healthanalytics.android.dialog.CommonAlertDialog
import com.healthanalytics.android.presentation.components.ScreenContainer
import com.healthanalytics.android.presentation.screens.questionnaire.types.ApplicableQuestion
import com.healthanalytics.android.presentation.screens.questionnaire.types.DropDownQuestion
import com.healthanalytics.android.presentation.screens.questionnaire.types.ImageTypeQuestion
import com.healthanalytics.android.presentation.screens.questionnaire.types.MultiSelectionQuestion
import com.healthanalytics.android.presentation.screens.questionnaire.types.RatingBarQuestion
import com.healthanalytics.android.presentation.screens.questionnaire.types.SelectBoxQuestion
import com.healthanalytics.android.presentation.screens.questionnaire.types.SingleSelectionQuestion
import com.healthanalytics.android.presentation.screens.questionnaire.types.TextAreaQuestion
import com.healthanalytics.android.presentation.screens.questionnaire.types.TextBoxQuestion
import com.healthanalytics.android.presentation.screens.questionnaire.viewmodel.QuestionnaireViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppStrings
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import com.healthanalytics.android.utils.Resource

@Composable
fun QuestionnaireScreen(
    viewModel: QuestionnaireViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToContinuation: () -> Unit,
    onNavigateToCompleted: () -> Unit,
) {
    ScreenContainer {
        LaunchedEffect(key1 = Unit) {
            viewModel.getQuestionnaireTag()
        }

        GetQuestionnaireTags(
            viewModel = viewModel
        )

        GetQuestionnaire(
            viewModel = viewModel,
            onNavigateToHome = onNavigateToHome,
            onNavigateToContinuation = onNavigateToContinuation,
            onNavigateToCompleted = onNavigateToCompleted
        )
    }

    BackHandler {
        onNavigateToHome()
    }
}

@Composable
private fun GetQuestionnaireTags(
    viewModel: QuestionnaireViewModel,
) {
    val response by viewModel.questionnaireTagsFlow.collectAsStateWithLifecycle(
        Resource.Loading()
    )

    when (response) {
        is Resource.Loading -> {
//            ProgressBar()
        }

        is Resource.Success -> {
            LaunchedEffect(key1 = Unit) {
                val result = response.data
                result?.tags?.let { viewModel.saveTagList(it.toMutableList()) }
                viewModel.getQuestionnaires()
            }
        }

        is Resource.Error -> {
            response.data?.let {
                LaunchedEffect(Unit) {
//                    displayErrorMessage(apiResult = it, isNavBack = true)
                }
            }
        }
    }


//    if (activity.checkInternetAvailable()) {
//    } else {
//        LaunchedEffect(key1 = Unit) {
//            SnackBarHelper.sendEvent(SnackBarEvent(message = CommonConstants.PLEASE_CHECK_INTERNET_CONNECTION))
//        }
//    }
}

@Composable
private fun GetQuestionnaire(
    viewModel: QuestionnaireViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToContinuation: () -> Unit,
    onNavigateToCompleted: () -> Unit,
) {
    var currentQuestion by remember { mutableStateOf<Question?>(null) }

    val response by viewModel.questionnaireFlow.collectAsStateWithLifecycle(
        Resource.Loading()
    )

    when (response) {
        is Resource.Loading -> {
//            ProgressBar()
        }

        is Resource.Success -> {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                LaunchedEffect(key1 = response) {
                    saveQuestionList(
                        result = response.data,
                        viewModel = viewModel
                    )
                    viewModel.clearMultipleQuestionId()
                    if (currentQuestion == null) {
                        val nextQuestionId = viewModel.getNextQuestionId()

                        currentQuestion = if (nextQuestionId != 0) {
                            viewModel.getCurrentQuestion(nextQuestionId.toString())
                        } else {
                            viewModel.getFirstQuestion()
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    QuestionProgressBar(
                        viewModel = viewModel,
                        onNavigateBack = {

                        }
                    )
                    QuestionnaireName(
                        questionnaireName = viewModel.getQuestionnaireName()
                    )
                    ShowQuestionBasedOnType(
                        currentQuestion = currentQuestion,
                        viewModel = viewModel
                    )
                }

                Row(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    val isFirstQuestion = currentQuestion?.firstQuestion == true

                    if (!isFirstQuestion) {
                        GetPreviousQuestion(
                            modifier = Modifier.weight(1f),
                            viewModel = viewModel,
                            onPreviousQuestionSelected = { previousQuestion ->
                                viewModel.calculateQuestionProgress()
                                currentQuestion = previousQuestion
                            }
                        )
                    }

                    GetNextQuestion(
                        modifier = Modifier.weight(1f),
                        viewModel = viewModel,
                        onNextQuestionSelected = { nextQuestion ->
                            viewModel.calculateQuestionProgress()
                            if (viewModel.isLastQuestion) {
                                when {
                                    viewModel.getViewType() != ViewConstants.DATA_TYPE_NO_ANSWER -> {
                                        viewModel.saveDialogState(true)
                                    }

                                    viewModel.allTheFieldsInAllTheCategoryAreNotEmpty -> {
                                        viewModel.saveDialogState(true)
                                    }

                                    else -> {
                                        if (nextQuestion?.data_type == ViewConstants.DATA_TYPE_NO_ANSWER) {
                                            currentQuestion = null
                                        }
                                        currentQuestion = nextQuestion
                                    }
                                }
                            } else {
                                if (nextQuestion?.data_type == ViewConstants.DATA_TYPE_NO_ANSWER) {
                                    currentQuestion = null
                                }
                                currentQuestion = nextQuestion
                            }
                        }
                    )
                }

                ShowSubmitAlertDialog(viewModel = viewModel)
                GetCompleteQuestionnaireState(
                    viewModel = viewModel,
                    onNavigateToHome = onNavigateToHome,
                    onNavigateToCompleted = onNavigateToCompleted,
                    onNavigateToContinuation = onNavigateToContinuation
                )
                GetInCompleteQuestionnaireState(
                    viewModel = viewModel,
                    onNavigateToHome = onNavigateToHome,
                )
            }
        }

        is Resource.Error -> {
            response.data?.let {
                LaunchedEffect(Unit) {
//                    displayErrorMessage(apiResult = it, isNavBack = true)
                }
            }
        }
    }
}

@Composable
fun ShowQuestionBasedOnType(currentQuestion: Question?, viewModel: QuestionnaireViewModel) {
    currentQuestion?.let {

        if (currentQuestion.data_type == ViewConstants.DATA_TYPE_NO_ANSWER) {
            viewModel.saveViewType(ViewConstants.DATA_TYPE_NO_ANSWER)
            Column(
                modifier = Modifier
                    .padding(bottom = Dimensions.size80dp)
                    .verticalScroll(rememberScrollState())
            ) {
                ViewMultipleQuestions(
                    question = it,
                    viewModel = viewModel
                )
                setNextQuestionButtonStateOfMultipleQuestion(
                    question = it,
                    viewModel = viewModel
                )
            }
        } else {

            LaunchedEffect(key1 = it.id) {
                setOptionalQuestionButtonState(question = it, viewModel = viewModel)
                viewModel.saveCurrentQuestion(currentQuestion)
                setNextQuestionButtonText(
                    nextQuestion = it,
                    viewModel = viewModel
                )
            }

            ViewQuestions(
                questionnaire = it,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun GetInCompleteQuestionnaireState(
    viewModel: QuestionnaireViewModel,
    onNavigateToHome: () -> Unit,
) {
    val response by viewModel.inCompleteQuestionnaireFlow.collectAsStateWithLifecycle(initialValue = null)
    val progressSaved = "progress_saved"
    when (response) {
        is Resource.Loading -> {
//            ProgressBar()
        }

        is Resource.Success -> {
            LaunchedEffect(key1 = Unit) {
                viewModel.saveNextQuestionnaireData()
//                shortToast(progressSaved)
                onNavigateToHome()
            }
        }

        is Resource.Error -> {
            response?.data?.let {
                LaunchedEffect(Unit) {
//                    displayErrorMessage(apiResult = it, isNavBack = true)
                }
            }
        }

        null -> {}
    }
}

@Composable
fun GetCompleteQuestionnaireState(
    viewModel: QuestionnaireViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToContinuation: () -> Unit,
    onNavigateToCompleted: () -> Unit,
) {
    val response by viewModel.completeQuestionnaireFlow.collectAsStateWithLifecycle(initialValue = null)

    when (response) {
        is Resource.Loading -> {
//            ProgressBar()
        }

        is Resource.Success -> {
            LaunchedEffect(Unit) {
                viewModel.removeCompletedQuestionnaireData(viewModel.getAssessmentId())
                viewModel.removeCompletedAssessmentId()
                when {
                    viewModel.getQuestionnaireName()
                        .lowercase() == QuestionnaireConstants.FEEDBACK.lowercase() -> {
                        onNavigateToCompleted()
                    }

                    else -> {
                        if (viewModel.questionnaireContinuationList.isNotEmpty()) {
                            onNavigateToContinuation()
                        } else {
                            onNavigateToHome()
                        }
                    }
                }
            }
        }

        is Resource.Error -> {
            response?.data?.let {
                LaunchedEffect(Unit) {
//                    displayErrorMessage(apiResult = it, isNavBack = true)
                }
            }
        }

        null -> {}
    }
}


@Composable
private fun ShowSubmitAlertDialog(viewModel: QuestionnaireViewModel) {
    val showAlertDialog =
        viewModel.showAlertDialog.collectAsStateWithLifecycle(initialValue = false).value
    if (showAlertDialog) {
        CommonAlertDialog(
            title = "",
            description = AppStrings.ARE_YOU_SURE_YOU_WANT_TO_SUBMIT_THE_QUESTIONNAIRE,
            descriptionTextSize = FontSize.textSize16sp,

            confirmButtonTextSize = FontSize.textSize16sp,
            dismissButtonTextSize = FontSize.textSize16sp,

            confirmButtonName = AppStrings.SUBMIT,
            dismissButtonName = AppStrings.GO_BACK,

            onDialogDismiss = {},
            onConfirmClick = {
                viewModel.saveDialogState(false)
                val questionnaire = viewModel.getCompletedQuestionnaire()
                viewModel.submitCompleteQuestionnaires(questionnaire = questionnaire)
            },
            onDismissClick = {
                viewModel.saveDialogState(false)
            }
        )
    }
}

private fun setOptionalQuestionButtonState(question: Question, viewModel: QuestionnaireViewModel) {
    if (question.is_answered) {
        viewModel.saveNextButtonState(true)
    } else {
        if (question.required == true) {
            viewModel.clearSavedAnswersAndIds()
            viewModel.saveNextButtonState(false)
        } else {
            viewModel.saveNextButtonState(true)
        }
    }
}

private fun setNextQuestionButtonStateOfMultipleQuestion(
    question: Question,
    viewModel: QuestionnaireViewModel,
) {
    val lastQuestion = if (question.sub_questions?.last()?.sub_questions?.isNotEmpty() == true) {
        question.sub_questions?.lastOrNull()?.sub_questions?.lastOrNull()
    } else {
        question.sub_questions?.lastOrNull()
    }

    setNextQuestionButtonText(lastQuestion, viewModel)
}

private fun setNextQuestionButtonText(nextQuestion: Question?, viewModel: QuestionnaireViewModel) {
    val question = nextQuestion?.id?.let { viewModel.getLastQuestion(it) }
    viewModel.saveLastQuestionId(question?.id ?: "")

    val isLast = if (nextQuestion?.default_next_question_id != null) {
        val value = nextQuestion.default_next_question_id?.let { viewModel.getLastQuestion(it) }
        value == null
    } else {
        false
    }

    when {
        question == null -> setLastQuestion(viewModel)
        viewModel.checkLastQuestion(question) -> setLastQuestion(viewModel)
        isLast -> setLastQuestion(
            viewModel = viewModel
        )

        else -> setNextQuestion(
            viewModel = viewModel
        )
    }
}

private fun setNextQuestion(viewModel: QuestionnaireViewModel) {
    viewModel.saveNextButtonName(QuestionnaireConstants.NEXT)
    viewModel.saveIsLastQuestion(false)
}

private fun setLastQuestion(viewModel: QuestionnaireViewModel) {
    viewModel.saveNextButtonName(QuestionnaireConstants.FINISH)
    viewModel.saveIsLastQuestion(true)
    if (viewModel.getViewType() == ViewConstants.DATA_TYPE_NO_ANSWER) {
        viewModel.resetDefaultNextQuestionId()
    }
}

@Composable
private fun GetNextQuestion(
    modifier: Modifier = Modifier,
    viewModel: QuestionnaireViewModel,
    onNextQuestionSelected: (Question?) -> Unit,
) {
    val buttonState = viewModel.nextButtonStateFlow.collectAsStateWithLifecycle(false).value
    val buttonName =
        viewModel.nextButtonName.collectAsStateWithLifecycle(QuestionnaireConstants.NEXT).value

    PrimaryButton(
        modifier = modifier.padding(Dimensions.size16dp),
        buttonName = buttonName,
        enable = buttonState,
        onClick = {

            when (viewModel.getViewType()) {
                ViewConstants.DATA_TYPE_NO_ANSWER -> {
                    validateMultipleQuestion(
                        onNextQuestionSelected = onNextQuestionSelected,
                        viewModel = viewModel
                    )
                }

                ViewConstants.RADIO_BUTTON -> {
                    validateAndSaveSingleSelectionQuestion(
                        onNextQuestionSelected = onNextQuestionSelected,
                        viewModel = viewModel
                    )
                }

                ViewConstants.CHECK_BOX -> {
                    validateAndSaveMultiSelectionQuestion(
                        onNextQuestionSelected = onNextQuestionSelected,
                        viewModel = viewModel
                    )
                }

                ViewConstants.TEXT_BOX -> {
                    validateAndSaveTextBoxQuestion(
                        onNextQuestionSelected = onNextQuestionSelected,
                        viewModel = viewModel
                    )
                }

                ViewConstants.DROPDOWN -> {
                    validateAndSaveDropDownAnswer(
                        onNextQuestionSelected = onNextQuestionSelected,
                        viewModel = viewModel
                    )
                }

                ViewConstants.TEXT_AREA -> {
                    validateAndSaveTextAreaAnswer(
                        onNextQuestionSelected = onNextQuestionSelected,
                        viewModel = viewModel
                    )
                }
            }
        })
}

private fun validateAndSaveSingleSelectionQuestion(
    onNextQuestionSelected: (Question?) -> Unit,
    viewModel: QuestionnaireViewModel,
) {
    val selectedAnswer = viewModel.radioButtonAnswer

    when {
        viewModel.currentQuestion?.required == false && selectedAnswer.isEmpty() -> {
            getNext(
                nextQuestionId = viewModel.selectedQuestionId,
                onNextQuestionSelected = onNextQuestionSelected,
                viewModel = viewModel
            )
        }

        selectedAnswer.isNotEmpty() -> {
            getNext(
                nextQuestionId = viewModel.selectedQuestionId,
                onNextQuestionSelected = onNextQuestionSelected,
                viewModel = viewModel
            )
        }
    }
}

private fun validateAndSaveMultiSelectionQuestion(
    onNextQuestionSelected: (Question?) -> Unit,
    viewModel: QuestionnaireViewModel,
) {
    val selectedAnswersList = viewModel.selectedAnswersList
    when {
        viewModel.currentQuestion?.required == false && selectedAnswersList.isEmpty() -> {
            getNext(
                nextQuestionId = null, onNextQuestionSelected = onNextQuestionSelected,
                viewModel = viewModel
            )
        }

        selectedAnswersList.isNotEmpty() -> {
            getNext(
                nextQuestionId = null, onNextQuestionSelected = onNextQuestionSelected,
                viewModel = viewModel
            )
        }
    }
}

private fun validateAndSaveTextBoxQuestion(
    onNextQuestionSelected: (Question?) -> Unit,
    viewModel: QuestionnaireViewModel,
) {
    val textBoxAnswer = viewModel.textBoxAnswer.trim()
    when {
        viewModel.currentQuestion?.required == false && textBoxAnswer.isEmpty() -> {
            getNext(
                nextQuestionId = null, onNextQuestionSelected = onNextQuestionSelected,
                viewModel = viewModel
            )
        }

        textBoxAnswer.isNotEmpty() -> {
            viewModel.updateTextViewQuestion(textBoxAnswer)
            getNext(
                nextQuestionId = null, onNextQuestionSelected = onNextQuestionSelected,
                viewModel = viewModel
            )
        }
    }
}

private fun validateAndSaveTextAreaAnswer(
    onNextQuestionSelected: (Question?) -> Unit,
    viewModel: QuestionnaireViewModel,
) {
    val textValue = viewModel.textAreaAnswer.trim()
    when {
        viewModel.currentQuestion?.required == false && textValue.isEmpty() -> {
            getNext(
                nextQuestionId = null, onNextQuestionSelected = onNextQuestionSelected,
                viewModel = viewModel
            )
        }

        textValue.isNotEmpty() -> {
            viewModel.updateTextViewQuestion(textValue)
            getNext(
                nextQuestionId = null, onNextQuestionSelected = onNextQuestionSelected,
                viewModel = viewModel
            )
        }
    }
}

private fun validateAndSaveDropDownAnswer(
    onNextQuestionSelected: (Question?) -> Unit,
    viewModel: QuestionnaireViewModel,
) {
    val textValue = viewModel.dropDownAnswer
    when {
        viewModel.currentQuestion?.required == false && textValue.isEmpty() -> {
            getNext(
                nextQuestionId = null, onNextQuestionSelected = onNextQuestionSelected,
                viewModel = viewModel
            )
        }

        textValue.isNotEmpty() -> {
            viewModel.updateDropDownQuestion(textValue)
            getNext(
                nextQuestionId = null, onNextQuestionSelected = onNextQuestionSelected,
                viewModel = viewModel
            )
        }
    }
}

private fun validateMultipleQuestion(
    onNextQuestionSelected: (Question?) -> Unit,
    viewModel: QuestionnaireViewModel,
) {
    val (allQuestionsAreAnswered, multipleQuestion) = viewModel.validateMultiQuestions()
    if (allQuestionsAreAnswered) {
        getNext(
            nextQuestionId = viewModel.multipleQuestionSelectedId,
            onNextQuestionSelected = onNextQuestionSelected,
            viewModel = viewModel
        )
    } else {
        onNextQuestionSelected(multipleQuestion)
    }
}

private fun getNext(
    nextQuestionId: String?,
    onNextQuestionSelected: (Question?) -> Unit,
    viewModel: QuestionnaireViewModel,
) {
    val nextQuestion = if (nextQuestionId != null) {
        viewModel.getNextQuestion(nextQuestionId)
    } else {
        viewModel.getDefaultNextQuestionId()?.let { viewModel.getNextQuestion(it) }
    }
    onNextQuestionSelected(nextQuestion)
}

@Composable
fun GetPreviousQuestion(
    modifier: Modifier = Modifier, onPreviousQuestionSelected: (Question?) -> Unit,
    viewModel: QuestionnaireViewModel,
) {
    SecondaryButton(
        enable = true,
        modifier = modifier.padding(Dimensions.size16dp),
        buttonName = "Previous",
        onClick = {
            onPreviousQuestionSelected(viewModel.getPreviousQuestion())
        }
    )
}

private fun saveQuestionList(
    viewModel: QuestionnaireViewModel,
    result: Questionnaire?,
) {
    if (result != null) {
        val data = result
        val nextQuestionId = viewModel.getNextQuestionId()
        val list = data.ha_questions?.toMutableList()

        if (list != null) {
            if (list.isNotEmpty()) {
                viewModel.saveQuestionList(list, nextQuestionId)
            }
        }
    }
}

@Composable
private fun ViewQuestions(questionnaire: Question, viewModel: QuestionnaireViewModel) {

    viewModel.saveViewType(questionnaire.input_element ?: "")
    val viewType = questionnaire.input_element ?: ""

    Column(modifier = Modifier.fillMaxSize()) {
        when (viewType) {
            ViewConstants.RADIO_BUTTON -> {
                when (questionnaire.data_type) {

                    ViewConstants.IMAGE, ViewConstants.DATA_TYPE_GRID_3 -> {
                        ImageTypeQuestion(
                            questionnaire = questionnaire,
                            viewModel = viewModel
                        )
                    }

                    ViewConstants.RATING -> {
                        QuestionSection(question = questionnaire)
                        RatingBarQuestion(
                            questionnaire = questionnaire,
                            viewModel = viewModel
                        )
                    }

                    else -> {
                        SingleSelectionQuestion(
                            questionnaire = questionnaire,
                            viewModel = viewModel
                        )
                    }
                }
            }

            ViewConstants.CHECK_BOX -> {
                MultiSelectionQuestion(
                    questionnaire = questionnaire,
                    viewModel = viewModel
                )
            }

            ViewConstants.TEXT_BOX -> {
                TextBoxQuestion(
                    questionnaire = questionnaire,
                    viewModel = viewModel
                )
            }

            ViewConstants.DROPDOWN -> {
                DropDownQuestion(
                    questionnaire = questionnaire, viewModel = viewModel
                )
            }

            ViewConstants.TEXT_AREA -> {
                TextAreaQuestion(
                    questionnaire = questionnaire, viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun ViewMultipleQuestions(question: Question, viewModel: QuestionnaireViewModel) {

    LaunchedEffect(key1 = question) {
        viewModel.saveMultipleQuestionAndId(question = question)
        viewModel.clearMultipleQuestionList()
    }

    QuestionSection(question = question)
    viewModel.saveNextButtonState(true)

    question.sub_questions?.forEachIndexed { index, subQuestion ->
        when (subQuestion.data_type) {
            ViewConstants.DATA_TYPE_APPLICABLE_TICK -> {
                ApplicableQuestion(
                    question = subQuestion,
                    viewModel = viewModel,
                    index = index
                )
            }

            ViewConstants.DATA_TYPE_SELECT_BOX -> {
                SelectBoxQuestion(
                    question = subQuestion,
                    viewModel = viewModel,
                    index = index
                )
            }
        }
    }
}

@Composable
fun QuestionnaireName(modifier: Modifier = Modifier, questionnaireName: String) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.size16dp)
    ) {
        Spacer(modifier = Modifier.height(Dimensions.size12dp))

        Text(
            text = questionnaireName.uppercase(),
            letterSpacing = with(LocalDensity.current) {
                Dimensions.size1dp.toSp()
            },
            color = AppColors.primaryTextColor,
            fontFamily = FontFamily.semiBold(),
            fontSize = FontSize.textSize12sp
        )
    }
    Spacer(modifier = Modifier.padding(top = Dimensions.size44dp))
}

@Composable
fun QuestionProgressBar(
    modifier: Modifier = Modifier,
    viewModel: QuestionnaireViewModel,
    onNavigateBack: () -> Unit,
) {
    val progress = viewModel.questionProgress.collectAsStateWithLifecycle(initialValue = 0.1f).value

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.size16dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.background(
                    color = AppColors.gray_100,
                    shape = CircleShape
                ).size(Dimensions.size30dp)
                    .clip(CircleShape),
                onClick = {
                    saveInCompleteQuestionnaires(
                        viewModel = viewModel,
                        onNavigateBack = onNavigateBack
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = AppColors.textPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(Dimensions.size20dp))
        LinearProgressIndicator(
            progress = { 0.5f },
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.size8dp)
                .clip(RoundedCornerShape(Dimensions.cornerRadiusSmall)),
            color = AppColors.primaryColor,
            trackColor = AppColors.gray,
            strokeCap = StrokeCap.Round
        )
    }
}

private fun saveInCompleteQuestionnaires(
    viewModel: QuestionnaireViewModel,
    onNavigateBack: () -> Unit,
) {
    if (viewModel.isLastQuestion) {
        viewModel.removeLastQuestion(viewModel.lastQuestionId)
    }
    val answer = viewModel.updateQuestionsIfAnswered()

    if (answer.ha_questions?.isNotEmpty() == true) {
        viewModel.submitInCompleteQuestionnaires(questionnaire = answer)
    } else {
        onNavigateBack()
    }
}

@Composable
fun QuestionSection(
    modifier: Modifier = Modifier,
    question: Question,
    horizontalPadding: Dp = Dimensions.size16dp,
    bottomPadding: Dp = Dimensions.size32dp,
) {
    Column(
        modifier = modifier.padding(
            start = horizontalPadding,
            end = horizontalPadding,
            bottom = bottomPadding
        )
    ) {
        Text(
            text = question.value ?: "",
            fontSize = FontSize.textSize20sp,
            fontFamily = FontFamily.semiBold(),
            color = AppColors.primaryTextColor,
            textAlign = TextAlign.Start,
        )

        if (question.description != null) {
            Spacer(modifier = modifier.padding(top = Dimensions.size8dp))
            Text(
                text = question.description ?: "",
                fontSize = FontSize.textSize16sp,
                fontFamily = FontFamily.regular(),
                color = AppColors.secondaryTextColor,
                textAlign = TextAlign.Start,
            )
        }

        if (question.type == ViewConstants.CHECK_BOX) {
            Spacer(modifier = modifier.padding(top = Dimensions.size8dp))
            Text(
                text = "selectAllThatApplies",
                fontSize = FontSize.textSize16sp,
                fontFamily = FontFamily.regular(),
                color = AppColors.secondaryTextColor,
                textAlign = TextAlign.Start,
                modifier = modifier.padding(horizontal = horizontalPadding)
            )
        }
    }
}









