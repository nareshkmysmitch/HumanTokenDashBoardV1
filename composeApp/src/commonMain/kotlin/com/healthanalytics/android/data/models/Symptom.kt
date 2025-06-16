package com.healthanalytics.android.data.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class SymptomResponse(
    val status: String? = null,
    val message: String? = null,
    val data: SymptomData? = null
)

@Serializable
data class SymptomData(
    val symptoms: List<Symptom> = emptyList()
)

@Serializable
data class SymptomsWrapper(
    val symptoms: List<Symptom>
)

@Serializable
data class Symptom(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val category: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

@Serializable
data class SubmitSymptomsResponse(
    val status: String? = null,
    val message: String? = null,
    val data: SubmitSymptomsData? = null
)

@Serializable
data class SubmitSymptomsData(
    val isAdded: Boolean? = null
) 