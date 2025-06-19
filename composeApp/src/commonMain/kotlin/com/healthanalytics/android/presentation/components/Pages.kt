package com.healthanalytics.android.presentation.components

import com.healthanalytics.android.data.api.Product

sealed class Pages {
    data object HOME : Pages()
    data object PROFILE : Pages()
    data object CONVERSATION_LIST : Pages()
    data class CHAT(val conversationId: String) : Pages()
    data class MARKETPLACE_DETAIL(val product: Product) : Pages()
    data object CART : Pages()
    data object TEST_BOOKING : Pages()
    data object SCHEDULE_TEST_BOOKING : Pages()

    data object BIOMARKERS_DETAIL : Pages()
    data object BIOMARKER_FULL_REPORT : Pages()
    data object SYMPTOMS : Pages()
}