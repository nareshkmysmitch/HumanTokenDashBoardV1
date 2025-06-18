package com.healthanalytics.android.presentation.screens.questionnaire

import com.healthanalytics.android.data.models.questionnaire.Questionnaire
import com.healthanalytics.android.data.models.questionnaire.QuestionnaireUpdatedResponse
import com.healthanalytics.android.data.models.questionnaire.SubmittedTags
import com.healthanalytics.android.utils.EncryptionUtils
import com.healthanalytics.android.utils.EncryptionUtils.toEncryptedRequestBody
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText

interface QuestionnaireApiService {
    suspend fun getQuestionnaireTags(accessToken: String): SubmittedTags?
    suspend fun getQuestionnaires(accessToken: String, assessmentId: String): Questionnaire?
    suspend fun submitQuestionnaire(
        accessToken: String,
        questionnaire: Questionnaire
    ): QuestionnaireUpdatedResponse?
}

class QuestionnaireApiServiceImpl(val httpClient: HttpClient) : QuestionnaireApiService {
    override suspend fun getQuestionnaireTags(accessToken: String): SubmittedTags? {
        val response = httpClient.get("v3/human-token/submitted-tags") {
            header("access_token", accessToken)
        }

        val responseBody = response.bodyAsText()
        val authResponse = EncryptionUtils.handleDecryptionResponse<SubmittedTags>(responseBody)

        return authResponse
    }

    override suspend fun getQuestionnaires(
        accessToken: String,
        assessmentId: String
    ): Questionnaire? {
        val response = httpClient.get("v3/human-token/questions") {
            header("access_token", accessToken)
            url {
                parameters.append("assessment_id", assessmentId)
            }
        }

        val responseBody = response.bodyAsText()
        val authResponse = EncryptionUtils.handleDecryptionResponse<Questionnaire>(responseBody)

        return authResponse
    }

    override suspend fun submitQuestionnaire(
        accessToken: String,
        questionnaire: Questionnaire
    ): QuestionnaireUpdatedResponse? {
        val response = httpClient.post("v3/human-token/submit-answer") {
            header("access_token", accessToken)
            setBody(questionnaire.toEncryptedRequestBody())
        }

        val responseBody = response.bodyAsText()
        val authResponse = EncryptionUtils.handleDecryptionResponse<QuestionnaireUpdatedResponse>(responseBody)

        return authResponse
    }
}