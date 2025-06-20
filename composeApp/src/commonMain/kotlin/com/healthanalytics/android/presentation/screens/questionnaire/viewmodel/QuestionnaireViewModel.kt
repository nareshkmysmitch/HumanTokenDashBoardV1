package com.healthanalytics.android.presentation.screens.questionnaire.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.constants.HumanTokenConstants
import com.healthanalytics.android.constants.QuestionnaireConstants
import com.healthanalytics.android.constants.UnitConstants
import com.healthanalytics.android.constants.ViewConstants
import com.healthanalytics.android.data.models.questionnaire.Answers
import com.healthanalytics.android.data.models.questionnaire.BottomSheetQuestionState
import com.healthanalytics.android.data.models.questionnaire.HtCategory
import com.healthanalytics.android.data.models.questionnaire.HumanToken
import com.healthanalytics.android.data.models.questionnaire.Question
import com.healthanalytics.android.data.models.questionnaire.Questionnaire
import com.healthanalytics.android.data.models.questionnaire.QuestionnaireContinuation
import com.healthanalytics.android.data.models.questionnaire.QuestionnaireNextQuestionData
import com.healthanalytics.android.data.models.questionnaire.QuestionnaireUpdatedResponse
import com.healthanalytics.android.data.models.questionnaire.SubmittedTags
import com.healthanalytics.android.data.models.questionnaire.Tags
import com.healthanalytics.android.data.repositories.PreferencesRepository
import com.healthanalytics.android.extension.inchToCM
import com.healthanalytics.android.presentation.screens.questionnaire.QuestionnaireApiService
import com.healthanalytics.android.utils.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class QuestionnaireViewModel(
    private val questionnaireApiService: QuestionnaireApiService,
    private val preferencesRepository: PreferencesRepository,
    val json: Json,
) : ViewModel() {

    private var questionnaireIndex = 0
    private var assessmentId = ""
    private var nextQuestionId = 0
    private var questionId = ""
    private var defaultQuestionId: String? = ""
    private var questionnaireName = ""
    private var currentAssessmentId = ""
    private var tagList: MutableList<Tags> = mutableListOf()
    private var questionnaireContinuation: QuestionnaireContinuation? = null
    private val _questionnaireIndexLiveData = MutableSharedFlow<Int>(1)
    private var questionsList: MutableList<Question> = mutableListOf()

    var previousDisplayName = ""
    var partName = ""
    var totalPartCount = 0
    var accessToken: String? = null
    var userGender: String? = null
    var selectedAnswersList: MutableList<Answers> = mutableListOf()
    var questionnaireContinuationList: MutableList<QuestionnaireContinuation> = mutableListOf()

    init {
        viewModelScope.launch {
            preferencesRepository.accessToken.collect { token ->
                accessToken = token
            }

            preferencesRepository.gender.collect {
                this@QuestionnaireViewModel.userGender = it
            }
        }

//        saveQuestionnaireDetails(
//            assessmentId = "105",
//            nextQuestionId = 0,
//            displayName = "LifeStyle"
//        )
    }

    fun getQuestionnaireName() = questionnaireName

    fun saveTagList(submittedTags: MutableList<String>) {
        tagList = mutableListOf()

        if (submittedTags.isNotEmpty()) {
            val duplicateTags = mutableListOf(
                Tags(
                    selectedQuestionTags = submittedTags,
                    questionId = HumanTokenConstants.DUPLICATE_QUESTION_TAG_ID
                )
            )
            this.tagList.addAll(duplicateTags)
        }
    }

    fun saveQuestionList(
        questions: MutableList<Question>,
        prefsNextQuestionId: Int,
    ) {
        questionsList = questions
        questionId = prefsNextQuestionId.toString()

        setFirstQuestion()
        setAlreadyUploadedQuestionAsAnswered()

        viewModelScope.launch {
            _questionnaireIndexLiveData.emit(questionnaireIndex)
        }
    }

    fun getDefaultNextQuestionId() = defaultQuestionId

    fun resetDefaultNextQuestionId() {
        defaultQuestionId = null
    }


    //it will help to filter the uploaded question while submit
    private fun setAlreadyUploadedQuestionAsAnswered() {
        val uploadedQuestion = questionsList.filter {
            it.answers?.any { answers ->
                answers.is_selected == true
            } == true || it.text_answer != null
        }

        uploadedQuestion.forEach { upload ->
            if (upload.input_element == ViewConstants.TEXT_BOX) {
                upload.answers?.forEach { answer ->
                    answer.is_selected = true
                }
            }
            upload.is_answered = true
            upload.is_selected = true
            upload.is_uploaded = true
        }
    }

    /* find the first question and set firstQuestion == true
      to maintain next and previous button state*/
    private fun setFirstQuestion() {
        val question = questionsList.find {
            it.is_first_question == true
        } ?: questionsList.firstOrNull()

        val nextQuestion = when {
            question?.tag?.isNotEmpty() == true && tagList.isNotEmpty() -> {
                val questionTags = question.tag ?: listOf()
                val selectedTags =
                    tagList.flatMap { it.selectedQuestionTags ?: mutableListOf() }.distinct()
                val hasAnyCommonTag = questionTags.any { tag -> selectedTags.contains(tag) }
                if (hasAnyCommonTag) {
                    question
                } else {
                    question.default_next_question_id?.let {
                        findFirstQuestion(it)
                    }
                }
            }

            else -> {
                question
            }
        }

        nextQuestion?.firstQuestion = true
    }

    fun getFirstQuestion(): Question? {
        val question = questionsList.find {
            it.is_first_question == true
        } ?: questionsList.firstOrNull()

        val nextQuestion = when {
            question?.tag?.isNotEmpty() == true && tagList.isNotEmpty() -> {
                val questionTags = question.tag ?: listOf()
                val selectedTags =
                    tagList.flatMap { it.selectedQuestionTags ?: mutableListOf() }.distinct()
                val hasAnyCommonTag = questionTags.any { tag -> selectedTags.contains(tag) }
                if (hasAnyCommonTag) {
                    question
                } else {
                    question.default_next_question_id?.let {
                        findFirstQuestion(it)
                    }
                }
            }

            else -> {
                question
            }
        }
        val answers = hideOptionsBasedOnTag(nextQuestion)
        if (answers.isNotEmpty()) {
            nextQuestion?.answers = answers
        }

        questionId = nextQuestion?.id.toString()
        defaultQuestionId = nextQuestion?.default_next_question_id.toString()
        nextQuestion?.firstQuestion = true
        return if (nextQuestion?.data_type == ViewConstants.DATA_TYPE_NO_ANSWER) {
            createListForNoAnswerQuestionCategory(nextQuestion)
        } else {
            nextQuestion
        }
    }

    private fun findFirstQuestion(id: String): Question? {
        val question = questionsList.find {
            it.id == id
        }

        val nextQuestion = when {

            question?.tag?.isNotEmpty() == true && tagList.isNotEmpty() -> {
                val questionTags = question.tag ?: listOf()
                val selectedTags =
                    tagList.flatMap { it.selectedQuestionTags ?: mutableListOf() }.distinct()
                val hasAnyCommonTag = questionTags.any { tag -> selectedTags.contains(tag) }
                if (hasAnyCommonTag) {
                    question
                } else {
                    question.default_next_question_id?.let {
                        findFirstQuestion(it)
                    }
                }
            }

            else -> {
                question
            }
        }

        return nextQuestion
    }

    fun getCurrentQuestion(id: String): Question? {
        val question = questionsList.find {
            it.id == id
        }
        questionId = question?.id.toString()
        defaultQuestionId = question?.default_next_question_id.toString()

        val ques = if (question?.data_type == ViewConstants.DATA_TYPE_NO_ANSWER) {
            createListForNoAnswerQuestionCategory(question)
        } else {
            question
        }

        val answers = hideOptionsBasedOnTag(ques)
        if (answers.isNotEmpty()) {
            ques?.answers = answers
        }

        return ques
    }

    fun checkLastQuestion(question: Question): Boolean {
        val isNextQuestionIdNull = (if (question.answers?.isNotEmpty() == true) {
            question.answers?.all { it.next_question_id == null }
        } else {
            false
        }) == true

        return (question.default_next_question_id == null && isNextQuestionIdNull)
    }

    fun getNextQuestion(id: String): Question? {
        val question = questionsList.find {
            it.id == id
        }

        val nextQuestion = when {
            question?.primary_question_id != null -> {
                questionsList.find {
                    it.id == question.default_next_question_id
                }
            }

            question?.tag?.isNotEmpty() == true && tagList.isNotEmpty() -> {

                val questionTags = question.tag ?: listOf()
                val selectedTags =
                    tagList.flatMap { it.selectedQuestionTags ?: mutableListOf() }.distinct()

                val hasAnyCommonTag = questionTags.any { tag -> selectedTags.contains(tag) }
                if (hasAnyCommonTag) {
                    question
                } else {
                    question.default_next_question_id?.let {
                        getNextQuestion(it)
                    }
                }
            }

            else -> {
                question
            }
        }

        val answeredQuestion = questionsList.find {
            it.id == questionId
        }
        answeredQuestion?.is_selected = true

        if (nextQuestion != null) {
            if (questionId != nextQuestion.id.toString()) {
                val answers = hideOptionsBasedOnTag(nextQuestion)
                if (answers.isNotEmpty()) {
                    nextQuestion.answers = answers
                }
                nextQuestion.previous_question_id = questionId
                questionId = nextQuestion.id.toString()
                defaultQuestionId = nextQuestion.default_next_question_id.toString()
            }
        }

        val ques = if (nextQuestion?.data_type == ViewConstants.DATA_TYPE_NO_ANSWER) {
            createListForNoAnswerQuestionCategory(nextQuestion)
        } else {
            nextQuestion
        }
        if (ques != null) {
            viewModelScope.launch {
                _questionnaireIndexLiveData.emit(questionnaireIndex)
            }
        }

        return ques
    }

    private fun createListForNoAnswerQuestionCategory(question: Question?): Question? {

        val catList: MutableList<Question> = mutableListOf()

        if (question?.data_type == ViewConstants.DATA_TYPE_NO_ANSWER) {
            val categoryList = questionsList.filter {
                it.id != question.id && it.category == question.category
            }
            if (categoryList.isNotEmpty()) {
                categoryList.forEach { category ->
                    val subQuestion = questionsList.filter {
                        it.id != category.id && it.sub_type != null && category.sub_type != null && it.sub_type == category.sub_type
                    }.sortedBy { it.id }

                    val categoryCopy = category.copy()
                    categoryCopy.sub_questions = mutableListOf()

                    if (subQuestion.isNotEmpty()) {
                        categoryCopy.apply {
                            id = category.id
                            primary_question_id = category.primary_question_id
                            previous_question_id = category.previous_question_id
                            type = category.type
                            data_type = category.data_type
                            input_element = category.input_element
                            value = category.value
                            default_next_question_id = category.default_next_question_id
                            text_answer = category.text_answer
                            answers = category.answers
                            is_first_question = category.is_first_question
                            tag = category.tag
                            required = category.required
                            is_selected = category.is_selected
                            is_answered = category.is_answered
                            firstQuestion = category.firstQuestion
                            this.category = category.category
                            description = category.description
                            sub_type = category.sub_type
                            sub_questions?.addAll(subQuestion.toMutableList())
                        }
                    }
                    catList.add(categoryCopy)
                }
            }
        }

        val copy = question?.copy()?.apply {
            id = question.id
            primary_question_id = question.primary_question_id
            previous_question_id = question.previous_question_id
            type = question.type
            data_type = question.data_type
            input_element = question.input_element
            value = question.value
            default_next_question_id = question.default_next_question_id
            text_answer = question.text_answer
            answers = question.answers
            is_first_question = question.is_first_question
            tag = question.tag
            required = question.required
            is_selected = question.is_selected
            is_answered = question.is_answered
            firstQuestion = question.firstQuestion
            category = question.category
            description = question.description
            sub_type = question.sub_type
            sub_questions = catList
        }

        return copy
    }

    fun getLastQuestion(id: String): Question? {
        val question = questionsList.find {
            it.id == id
        }

        val nextQuestion = when {
            question?.primary_question_id != null -> {
                questionsList.find {
                    it.id == question.default_next_question_id
                }
            }

            question?.tag?.isNotEmpty() == true && tagList.isNotEmpty() -> {
                val questionTags = question.tag ?: listOf()
                val selectedTags =
                    tagList.flatMap { it.selectedQuestionTags ?: mutableListOf() }.distinct()

                val hasAnyCommonTag = questionTags.any { tag -> selectedTags.contains(tag) }
                if (hasAnyCommonTag) {
                    question
                } else {
                    question.default_next_question_id?.let {
                        getLastQuestion(it)
                    }
                }
            }

            else -> {
                question
            }
        }

        return nextQuestion
    }

    private fun hideOptionsBasedOnTag(nextQuestion: Question?): List<Answers> {
        val answerList: MutableList<Answers> = mutableListOf()
        return if (nextQuestion?.answers?.isNotEmpty() == true) {
            nextQuestion.answers?.forEach { answer ->
                answer.show_option = true
                val optionTags = answer.tag_render ?: listOf()
                val selectedTags =
                    tagList.flatMap { it.selectedQuestionTags ?: mutableListOf() }.distinct()

                if (optionTags.isNotEmpty() && selectedTags.isNotEmpty()) {
                    val hasAnyCommonTag = optionTags.any { tag -> selectedTags.contains(tag) }
                    if (!hasAnyCommonTag) {
                        answer.show_option = false
                    }
                }
                answerList.add(answer)
            }
            answerList
        } else {
            mutableListOf()
        }
    }

    fun getPreviousQuestion(): Question? {
        val currentQuestion = questionsList.find {
            it.id == questionId
        }
        resetSelectedAnswer(currentQuestion)
        val previousQuestion = findPreviousQuestion(questionId)

        currentQuestion?.is_selected = false
        val answers = hideOptionsBasedOnTag(previousQuestion)
        if (answers.isNotEmpty()) {
            previousQuestion?.answers = answers
        }
        questionId = previousQuestion?.id ?: ""
        defaultQuestionId = previousQuestion?.default_next_question_id
        viewModelScope.launch {
            _questionnaireIndexLiveData.emit(questionnaireIndex)
        }

        return if (previousQuestion?.data_type == ViewConstants.DATA_TYPE_NO_ANSWER) {
            createListForNoAnswerQuestionCategory(previousQuestion)
        } else {
            previousQuestion
        }
    }

    private fun resetSelectedAnswer(currentQuestion: Question?) {
        currentQuestion?.is_answered = false
    }

    private fun findPreviousQuestion(questionId: String?): Question? {
        val currentQuestion = questionsList.find {
            it.id == questionId
        }

        var currentIndex = -1

        if (currentQuestion != null) {
            val currentQuestionId = currentQuestion.id

            val firstConditionIndex = questionsList.indexOfFirst { question ->
                question.answers?.any {
                    (it.is_selected == true) && it.next_question_id == currentQuestionId
                } == true && question.input_element != ViewConstants.CHECK_BOX
            }

            if (firstConditionIndex >= 0) {
                currentIndex = firstConditionIndex
            } else {
                val secondConditionIndex = questionsList.indexOfFirst { question ->
                    question.answers?.any { it.is_selected == true && it.next_question_id == null } == true && (question.default_next_question_id == currentQuestionId)
                }

                if (secondConditionIndex >= 0) {
                    currentIndex = secondConditionIndex
                } else {
                    val thirdConditionIndex = questionsList.indexOfFirst { question ->
                        question.default_next_question_id == currentQuestionId
                    }
                    if (thirdConditionIndex >= 0) {
                        currentIndex = thirdConditionIndex
                    }
                }
            }
        }


        val currentCategory = if (currentIndex >= 0) {
            questionsList.getOrNull(currentIndex)?.category
        } else {
            null
        }
        currentCategory?.let { category ->
            val noAnswerQuestionIndex = questionsList.indexOfFirst { question ->
                question.category == category && question.data_type == ViewConstants.DATA_TYPE_NO_ANSWER
            }

            if (noAnswerQuestionIndex != -1) {
                currentIndex = noAnswerQuestionIndex
            }
        }

        val findQuestion = if (currentIndex >= 0) {
            questionsList.getOrNull(currentIndex)
        } else {
            null
        }

        return if (findQuestion?.tag?.isNotEmpty() == true && tagList.isNotEmpty()) {
            val questionTags = findQuestion.tag ?: listOf()
            val selectedTags = tagList.flatMap { it.selectedQuestionTags ?: listOf() }.distinct()
            val hasAnyCommonTag = selectedTags.any { tag -> questionTags.contains(tag) }
            if (hasAnyCommonTag) {
                findQuestion
            } else {
                findPreviousQuestion(
                    findQuestion.id
                )
            }
        } else {
            findQuestion
        }
    }

    fun updateRadioButtonQuestion(answerId: String) {
        val question = questionsList.find {
            it.id == questionId
        }
        question?.is_answered = true
        val existingTags = tagList.find { it.questionId == questionId }

        if (existingTags != null) {
            removeSubmittedTagsIfTheOptionUnSelected(existingTags)
        }

        if (question?.answers?.isNotEmpty() == true) {
            question.answers?.forEach { answer ->
                if (answer.id == answerId) {
                    answer.is_selected = true
                    if (answer.tag?.isNotEmpty() == true) {
                        val newTags = Tags(
                            selectedQuestionTags = answer.tag.orEmpty().toMutableList(),
                            questionId = questionId
                        )

                        tagList.add(newTags)

                    }
                } else {
                    answer.is_selected = false
                }
            }
        }
    }

    private fun removeSubmittedTagsIfTheOptionUnSelected(existingTags: Tags) {
        val submittedTags =
            tagList.find { it.questionId == HumanTokenConstants.DUPLICATE_QUESTION_TAG_ID }
        if (submittedTags != null) {
            tagList.remove(existingTags)
        }

        val removeSubmittedTags = existingTags.selectedQuestionTags?.filter {
            submittedTags?.selectedQuestionTags?.contains(it) == true
        } ?: listOf()

        removeSubmittedTags.forEach { submitTag ->
            submittedTags?.selectedQuestionTags?.remove(submitTag)
        }
    }

    fun updateSelectBoxQuestion(questionId: String, answerId: String) {
        val question = questionsList.find {
            it.id == questionId
        }
        question?.is_answered = true
        val existingTags = tagList.find { it.questionId == questionId }

        if (existingTags != null) {
            removeSubmittedTagsIfTheOptionUnSelected(existingTags)
        }

        if (question?.answers?.isNotEmpty() == true) {
            question.answers?.forEach { answer ->
                if (answer.id == answerId) {
                    answer.is_selected = true
                    if (answer.tag?.isNotEmpty() == true) {
                        val newTags = Tags(
                            selectedQuestionTags = answer.tag.orEmpty().toMutableList(),
                            questionId = questionId
                        )

                        tagList.add(newTags)

                    }
                } else {
                    answer.is_selected = false
                }
            }
        }
    }

    fun updateApplicableQuestion(questionId: String?, isApplicable: Boolean) {
        val question = questionsList.find {
            it.id == questionId
        }
        question?.is_answered = isApplicable
        val existingTags = tagList.find { it.questionId == questionId }

        if (existingTags != null) {
            removeSubmittedTagsIfTheOptionUnSelected(existingTags)
        }

        if (question?.answers?.isNotEmpty() == true) {
            question.answers?.forEach { answer ->
                if (isApplicable) {
                    answer.is_selected = true
                    if (answer.tag?.isNotEmpty() == true) {
                        val newTags = questionId?.let {
                            Tags(
                                selectedQuestionTags = answer.tag.orEmpty().toMutableList(),
                                questionId = it
                            )
                        }

                        if (newTags != null) {
                            tagList.add(newTags)
                        }
                    }
                } else {
                    answer.is_selected = false
                }
            }
        }
    }

    fun getApplicableQuestionAnswer(currentQuestion: Question?): List<Answers>? {
        val question = questionsList.find {
            it.id == currentQuestion?.id
        }
        val selectedOptions = question?.answers?.filter { it.is_selected == true }
        if (selectedOptions?.isNotEmpty() == true) {
            question.is_answered = true
        }
        return selectedOptions
    }

    private fun getSelectedOptions(currentQuestion: Question?): String {
        val question = questionsList.find {
            it.id == currentQuestion?.id
        }
        val selectedOptions = question?.answers?.filter { it.is_selected == true }
        val options = selectedOptions?.joinToString(", ") { it.value.toString() } ?: ""
        if (options.isNotEmpty()) {
            question?.is_answered = true
        }
        return options
    }

    fun addSelectedAnswer(answers: Answers) {
        if (!selectedAnswersList.contains(answers)) {
            selectedAnswersList.add(answers)
        }
        if (selectedAnswersList.isNotEmpty()) {
            saveNextButtonState(true)
        }
    }

    fun removeSelectedAnswer(answers: Answers) {
        if (selectedAnswersList.isNotEmpty()) {
            val findAnswer = selectedAnswersList.find {
                answers.id == it.id
            }
            if (findAnswer != null) {
                selectedAnswersList.remove(answers)
            }
        }
        if (selectedAnswersList.isEmpty()) {
            saveNextButtonState(false)
        }
    }

    fun updateCheckboxQuestion(
        answerId: String,
        isSelected: Boolean,
    ) {
        val question = questionsList.find {
            it.id == questionId
        }

        val existingTags = tagList.find { it.questionId == answerId }
        val submittedTags =
            tagList.find { it.questionId == HumanTokenConstants.DUPLICATE_QUESTION_TAG_ID }

        if (question?.answers?.isNotEmpty() == true) {
            question.answers?.forEach { answer ->
                if (answer.id == answerId) {
                    answer.is_selected = isSelected
                    if (answer.tag?.isNotEmpty() == true) {
                        if (existingTags != null) {
                            if (isSelected) {
                                existingTags.selectedQuestionTags?.addAll(
                                    answer.tag.orEmpty().toMutableList()
                                )
                                existingTags.selectedQuestionTags =
                                    existingTags.selectedQuestionTags?.distinctBy { tagName ->
                                        tagName
                                    }?.toMutableList()

                            } else {
                                existingTags.selectedQuestionTags?.removeAll {
                                    answer.tag.orEmpty().contains(it)
                                }

                                submittedTags?.selectedQuestionTags?.removeAll {
                                    answer.tag.orEmpty().contains(it)
                                }
                            }
                        } else {
                            if (isSelected) {
                                val newTags = Tags(
                                    selectedQuestionTags = answer.tag.orEmpty().toMutableList(),
                                    questionId = answerId
                                )
                                tagList.add(newTags)
                            }
                        }
                    }
                }
            }

            question.is_answered = question.answers?.any { any ->
                any.is_selected == true
            } == true
        }
    }

    fun updateTextViewQuestion(answer: String) {
        val question = questionsList.find {
            it.id == questionId
        }
        question?.is_answered = true
        question?.text_answer = answer
        question?.answers?.forEach {
            it.is_selected = true
        }
    }

    fun updateDropDownQuestion(answer: String) {
        val question = questionsList.find {
            it.id == questionId
        }
        question?.is_answered = true
        question?.text_answer = answer
        question?.answers?.forEach {
            it.is_selected = true
        }
    }

    private fun getInCompleteQuestionnaire(): Questionnaire {
        val selectedAndAnswered = questionsList.filter {
            it.is_answered
        }.toMutableList()

        return Questionnaire(
            assessment_id = getAssessmentId(),
            is_completed = false,
            ha_questions = selectedAndAnswered,
            next_question_id = if (questionId.isNotEmpty()) questionId.toInt() else 0
        )
    }

    fun getCompletedQuestionnaire(): Questionnaire {
        val selectedAndAnswered = questionsList.filter {
            it.is_answered
        }.toMutableList()

        return Questionnaire(
            assessment_id = getAssessmentId(),
            is_completed = true,
            ha_questions = selectedAndAnswered,
        )
    }

    fun saveQuestionnaireDetails(
        assessmentId: String,
        nextQuestionId: Int,
        displayName: String,
        continuationList: MutableList<QuestionnaireContinuation> = mutableListOf()
    ) {
        viewModelScope.launch {
            val nextQuestionList = try {
                getNextQuestionnairesData().toMutableList()
            } catch (e: Exception) {
                emptyList()
            }
            val currentQuestionnaire = nextQuestionList.find { it.assessmentId == assessmentId }
            this@QuestionnaireViewModel.assessmentId = assessmentId
            this@QuestionnaireViewModel.nextQuestionId =
                currentQuestionnaire?.nextQuestionId ?: nextQuestionId
            questionnaireName = displayName
            questionnaireContinuationList = continuationList
        }
    }

    fun getAssessmentId(): String {
        return if (questionnaireContinuationList.isNotEmpty()) {
            questionnaireContinuation = questionnaireContinuationList.first()
            questionnaireName = questionnaireContinuation?.displayName ?: ""
            currentAssessmentId = questionnaireContinuation?.assessmentId ?: assessmentId
            currentAssessmentId
        } else {
            assessmentId
        }
    }

    fun removeCompletedAssessmentId() {
        val completedQuestionnaire = questionnaireContinuationList.find {
            it.assessmentId == currentAssessmentId
        }

        previousDisplayName = completedQuestionnaire?.displayName ?: ""
        partName = completedQuestionnaire?.partName ?: ""
        totalPartCount = completedQuestionnaire?.totalParts ?: 0

        if (questionnaireContinuationList.isNotEmpty()) {
            completedQuestionnaire?.let {
                questionnaireContinuationList.remove(it)
            }
        }
    }

    fun getNextQuestionId() = nextQuestionId

    private var _questionnaireTagsFlow = MutableSharedFlow<Resource<SubmittedTags?>>()
    val questionnaireTagsFlow: SharedFlow<Resource<SubmittedTags?>> = _questionnaireTagsFlow

    fun getQuestionnaireTag() {
        viewModelScope.launch {
            try {
                val response = accessToken?.let { questionnaireApiService.getQuestionnaireTags(it) }
                _questionnaireTagsFlow.emit(Resource.Success(data = response))
            } catch (exception: Exception) {
//                _questionnaireTagsFlow.emit(
//                    Resource.Error(data = defaultApiErrorMsg())
//                )
            }
        }
    }

    fun getQuestionnaireContinuationList(data: HumanToken?): MutableList<QuestionnaireContinuation> {
        val continuationList: MutableList<QuestionnaireContinuation> = mutableListOf()

        val filteredArray = mutableListOf<HtCategory?>()
        data?.ht_categories?.forEach { category ->
            if (category.type == QuestionnaireConstants.QUESTIONNAIRE) {
                filteredArray.add(category)
            }
        }
        filteredArray.forEach { htCategory ->
            htCategory?.let { category ->
                category.ht_milestones?.forEach { htMilestone ->
                    htMilestone.ht_tasks?.forEach { htTask ->
                        val isMilestoneCompleted =
                            htTask.ht_user_tasks?.firstOrNull()?.completed_at != null
                        if (!isMilestoneCompleted) {
                            htTask.assessment_id?.let { assessmentId ->
                                continuationList.add(
                                    QuestionnaireContinuation(
                                        assessmentId = assessmentId,
                                        displayName = htCategory.display_name ?: "",
                                        partName = (htTask.sequence ?: 0).toString(),
                                        totalParts = htCategory.n_task ?: 0
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        return continuationList
    }

    fun removeLastQuestion(lastQuestionId: String) {
        val question = questionsList.find {
            it.id == lastQuestionId
        }
        if (questionsList.contains(question)) {
            questionsList.remove(question)
        }
    }

    var radioButtonAnswer = ""
    var selectedQuestionId: String? = null
    fun saveNextQuestionIdAndAnswer(selectedId: String?, selectedAnswer: String = "") {
        selectedQuestionId = selectedId
        radioButtonAnswer = selectedAnswer
    }

    var multipleQuestionSelectedId: String? = null

    private fun saveMultipleQuestionNextQuestionId(selectedId: String?) {
        multipleQuestionSelectedId = selectedId
    }

    var currentQuestion: Question? = null
    fun saveCurrentQuestion(question: Question?) {
        currentQuestion = question
    }

    var textBoxAnswer = ""
    fun saveTextBoxAnswer(input: String, datatype: String?, unit: String) {
        textBoxAnswer = when (datatype) {
            ViewConstants.DATA_TYPE_NECK, ViewConstants.DATA_TYPE_WAIST, ViewConstants.DATA_TYPE_HIP -> {
                if (unit == UnitConstants.CM) {
                    input
                } else {
                    input.toDouble().inchToCM()
                }
            }

            else -> {
                input
            }
        }
    }

    fun clearTextBoxAnswer() {
        textBoxAnswer = ""
    }

    var textAreaAnswer = ""
    fun saveTextAreaAnswer(input: String) {
        textAreaAnswer = input
    }

    fun clearTextAreaAnswer() {
        textAreaAnswer = ""
    }

    var dropDownAnswer = ""
    fun saveDropDownAnswer(input: String) {
        dropDownAnswer = input
    }

    private var multipleQuestion: Question? = null
    private var selectBoxQuestionList: MutableList<Question> = mutableListOf()
    private var applicableBoxQuestionList: MutableList<Question> = mutableListOf()

    fun saveMultipleQuestionAndId(question: Question?) {
        multipleQuestion = question
        val lastQuestion =
            if (question?.sub_questions?.last()?.sub_questions?.isNotEmpty() == true) {
                question.sub_questions?.lastOrNull()?.sub_questions?.lastOrNull()
            } else {
                question?.sub_questions?.lastOrNull()
            }

        saveMultipleQuestionNextQuestionId(selectedId = lastQuestion?.default_next_question_id)
        question?.sub_questions?.filter { it.showError == true }?.forEach { it.showError = false }
    }

    fun saveSelectBoxQuestion(question: Question) {
        selectBoxQuestionList.add(question)
    }

    fun saveApplicableQuestion(question: Question) {
        applicableBoxQuestionList.add(question)
    }

    fun clearMultipleQuestionList() {
        selectBoxQuestionList = mutableListOf()
        applicableBoxQuestionList = mutableListOf()
    }

    fun removeQuestionFromSubQuestionList(subQuestion: Question) {
        if (selectBoxQuestionList.isNotEmpty()) {
            val remove = selectBoxQuestionList.filter { questions ->
                subQuestion.sub_questions?.any { answers ->
                    questions.id == answers.id
                } == true
            }
            removeAllSelectedSubQuestionAnswer(remove)
            selectBoxQuestionList.removeAll(remove)
        }
    }

    var allTheFieldsInAllTheCategoryAreNotEmpty = true

    /*using selectBoxQuestion.kt as parentQuestion also a subquestion
    if applicableBoxQuestionList is not empty validate using parentQuestionList
    else validate selectBoxQuestionList only
    and change the multipleQuestion subQuestionList based on empty in the validateSelectBoxQuestions() func*/
    fun validateMultiQuestions(): Pair<Boolean, Question?> {
        allTheFieldsInAllTheCategoryAreNotEmpty = true

        if (applicableBoxQuestionList.isNotEmpty()) {
            applicableBoxQuestionList.forEach { parentQuestionsData ->
                val isParentQuestionAnswered =
                    parentQuestionsData.answers?.any { it.is_selected == true }

                if (isParentQuestionAnswered == false) {
                    if (!validateMultiQuestionBasedOnCategory(parentQuestionsData)) {
                        allTheFieldsInAllTheCategoryAreNotEmpty = false
                    }
                }
            }
        }

        if (applicableBoxQuestionList.isEmpty()) {
            if (!validateSelectBoxQuestions().first) {
                allTheFieldsInAllTheCategoryAreNotEmpty = false
            }
        }

        return Pair(allTheFieldsInAllTheCategoryAreNotEmpty, multipleQuestion)
    }

    private fun validateMultiQuestionBasedOnCategory(parentQuestionsData: Question): Boolean {
        var allTheFieldsAreNotEmpty = true

        val subQuestions = selectBoxQuestionList.filter { subQuestionsData ->
            subQuestionsData.sub_type == parentQuestionsData.sub_type
        }

        subQuestions.forEach { subQuestionsData ->
            val selectedOptions = getSelectedOptions(subQuestionsData)
            subQuestionsData.showError = selectedOptions.isEmpty()
            if (selectedOptions.isEmpty()) {
                allTheFieldsAreNotEmpty = false
            }
        }

        multipleQuestion?.sub_questions = applicableBoxQuestionList

        return allTheFieldsAreNotEmpty
    }

    private fun validateSelectBoxQuestions(): Pair<Boolean, Question?> {
        var selectBoxQuestionsAreNotEmpty = true
        if (selectBoxQuestionList.isNotEmpty()) {
            selectBoxQuestionList.forEach { subQuestionsData ->
                val selectedOptions = getSelectedOptions(subQuestionsData)
                subQuestionsData.showError = selectedOptions.isEmpty()
                if (selectedOptions.isEmpty()) {
                    selectBoxQuestionsAreNotEmpty = false
                }
            }
        }

        multipleQuestion?.sub_questions = selectBoxQuestionList

        return Pair(selectBoxQuestionsAreNotEmpty, multipleQuestion)
    }

    private var _nextButtonStateFlow = MutableStateFlow(false)
    val nextButtonStateFlow: SharedFlow<Boolean> = _nextButtonStateFlow.asSharedFlow()

    fun saveNextButtonState(enable: Boolean) {
        _nextButtonStateFlow.value = enable
    }

    private var _nextButtonName = MutableStateFlow(QuestionnaireConstants.NEXT)
    val nextButtonName: SharedFlow<String> = _nextButtonName.asSharedFlow()

    fun saveNextButtonName(buttonName: String) {
        _nextButtonName.value = buttonName
    }

    var isLastQuestion = false
    fun saveIsLastQuestion(isLast: Boolean) {
        isLastQuestion = isLast
        calculateQuestionProgress()
    }

    var lastQuestionId: String = ""
    fun saveLastQuestionId(id: String) {
        lastQuestionId = id
    }

    private fun removeAllSelectedSubQuestionAnswer(questionToRemove: List<Question>) {
        val remove = questionsList.filter { questions ->
            questionToRemove.any { toRemove ->
                questions.id == toRemove.id
            }
        }

        remove.forEach { question ->
            question.is_answered = false
            question.showError = false
            tagList.removeAll { it.questionId == question.id }
        }
    }

    private var _questionnaireFlow = MutableSharedFlow<Resource<Questionnaire?>>()
    val questionnaireFlow: SharedFlow<Resource<Questionnaire?>> = _questionnaireFlow

    fun getQuestionnaires() {
        viewModelScope.launch {
            try {
                val response = accessToken?.let {
                    questionnaireApiService.getQuestionnaires(
                        it,
                        assessmentId
                    )
                }
                _questionnaireFlow.emit(Resource.Success(data = response))
            } catch (exception: Exception) {
//                _questionnaireFlow.emit(
//                    Resource.Error(data = defaultApiErrorMsg())
//                )
            }
        }
    }

    private var _inCompleteQuestionnaireFlow = MutableSharedFlow<Resource<QuestionnaireUpdatedResponse?>>()
    val inCompleteQuestionnaireFlow: SharedFlow<Resource<QuestionnaireUpdatedResponse?>> = _inCompleteQuestionnaireFlow

    fun submitInCompleteQuestionnaires(questionnaire: Questionnaire) {
        viewModelScope.launch {
            try {
                _inCompleteQuestionnaireFlow.emit(Resource.Loading())
                val response =
                    accessToken?.let { questionnaireApiService.submitQuestionnaire(it, questionnaire) }
                _inCompleteQuestionnaireFlow.emit(Resource.Success(data = response))
            } catch (exception: Exception) {
//                _inCompleteQuestionnaireFlow.emit(
//                    Resource.Error(data = defaultApiErrorMsg())
//                )
            }
        }
    }

    private var _completeQuestionnaireFlow = MutableSharedFlow<Resource<QuestionnaireUpdatedResponse?>>()
    val completeQuestionnaireFlow: SharedFlow<Resource<QuestionnaireUpdatedResponse?>> = _completeQuestionnaireFlow

    fun submitCompleteQuestionnaires(questionnaire: Questionnaire) {
        viewModelScope.launch {
            try {
                _completeQuestionnaireFlow.emit(Resource.Loading())
                val response =
                    accessToken?.let { questionnaireApiService.submitQuestionnaire(it, questionnaire) }
                _completeQuestionnaireFlow.emit(Resource.Success(data = response))
            } catch (_: Exception) {
//                _completeQuestionnaireFlow.emit(
//                    Resource.Error(data = defaultApiErrorMsg())
//                )
            }
        }
    }

    private var _questionProgress = MutableSharedFlow<Float>()
    val questionProgress: SharedFlow<Float> = _questionProgress

    fun calculateQuestionProgress() {
        viewModelScope.launch {
            if (isLastQuestion) {
                _questionProgress.emit(1.0f)
            } else {
                val answeredList = questionsList.filter { question ->
                    question.is_selected
                }
                val answeredQuestion = answeredList.size.plus(1f)
                val totalQuestion = questionsList.size

                val progress = if (answeredQuestion > 0 && totalQuestion > 0) {
                    val value = totalQuestion.minus(answeredQuestion).div(totalQuestion)
                    if (value > 1) value else 1.minus(value)
                } else {
                    0.1f
                }
                _questionProgress.emit(progress)
            }
        }
    }

    private var _showAlertDialog = MutableStateFlow(false)
    val showAlertDialog: SharedFlow<Boolean> = _showAlertDialog

    fun saveDialogState(showAlertDialog: Boolean) {
        _showAlertDialog.value = showAlertDialog
    }

    private var viewType = ""

    fun saveViewType(type: String) {
        viewType = type
    }

    fun getViewType() = viewType

    fun updateQuestionsIfAnswered(): Questionnaire {

        return when (viewType) {
            ViewConstants.TEXT_BOX -> {
                if (textBoxAnswer.trim().isNotEmpty()) {
                    updateTextViewQuestion(textBoxAnswer)
                }
                getInCompleteQuestionnaire()
            }

            ViewConstants.TEXT_AREA -> {
                if (textAreaAnswer.trim().isNotEmpty()) {
                    updateTextViewQuestion(textAreaAnswer)
                }
                getInCompleteQuestionnaire()
            }

            ViewConstants.DROPDOWN -> {
                if (dropDownAnswer.trim().isNotEmpty()) {
                    updateDropDownQuestion(dropDownAnswer)
                }
                getInCompleteQuestionnaire()
            }

            else -> {
                getInCompleteQuestionnaire()
            }
        }
    }

    fun clearSavedAnswersAndIds() {
        radioButtonAnswer = ""
        selectedQuestionId = null
        textBoxAnswer = ""
        textAreaAnswer = ""
        dropDownAnswer = ""
        isLastQuestion = false
        lastQuestionId = ""
        selectedAnswersList = mutableListOf()
    }

    fun clearMultipleQuestionId() {
        multipleQuestionSelectedId = null
    }

    private fun getQuestionId(): Int? {
        return if (questionId != "null" && questionId.isNotEmpty()) {
            questionId.toInt()
        } else {
            null
        }
    }

    suspend fun removeCompletedQuestionnaireData(assessmentId: String) {
        val nextQuestionList = try {
            getNextQuestionnairesData().toMutableList()
        } catch (e: Exception) {
            mutableListOf()
        }

        val currentQuestionnaire = nextQuestionList.find { it.assessmentId == assessmentId }

        println("currentQuestionnaire --> $currentQuestionnaire")
        if (currentQuestionnaire != null) {
            nextQuestionList.remove(currentQuestionnaire)
            println("currentQuestionnaire --> 1$currentQuestionnaire")

            val updatedJson = json.encodeToString(nextQuestionList)
            preferencesRepository.saveNextQuestionnaireData(updatedJson)
        }
        println(
            "test -->${
                getNextQuestionnairesData().toMutableList()
            }"
        )
    }

    suspend fun saveNextQuestionnaireData() {
        val nextQuestionList = try {
            getNextQuestionnairesData().toMutableList()
        } catch (e: Exception) {
            mutableListOf()
        }

        val currentAssessmentId = getAssessmentId()
        val currentQuestionnaire = nextQuestionList.find { it.assessmentId == currentAssessmentId }
        if (currentQuestionnaire != null) {
            currentQuestionnaire.nextQuestionId = getQuestionId()
        } else {
            val nextQuestionData = QuestionnaireNextQuestionData(
                assessmentId = currentAssessmentId,
                nextQuestionId = getQuestionId()
            )
            nextQuestionList.add(nextQuestionData)
        }

        val updatedJson = json.encodeToString(nextQuestionList)

        preferencesRepository.saveNextQuestionnaireData(updatedJson)
    }

    suspend fun getNextQuestionnairesData(): List<QuestionnaireNextQuestionData> {
        val jsonData = preferencesRepository.nextQuestionnaire.first()
        return try {
            if (jsonData != null) {
                json.decodeFromString(jsonData)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private var _bottomSheetState = MutableSharedFlow<BottomSheetQuestionState>()
    val bottomSheetState: SharedFlow<BottomSheetQuestionState> = _bottomSheetState

    fun setBottomSheetState(bottomSheetQuestionState: BottomSheetQuestionState) {
        viewModelScope.launch {
            _bottomSheetState.emit(bottomSheetQuestionState)
        }
    }

    fun getGender(): String {
        return userGender ?: ""
    }

    inline fun <reified T> parseJson(data: String?): T? {
        return try {
            if (data != null) json.decodeFromString<T>(data) else null
        } catch (e: Exception) {
            null
        }
    }

}