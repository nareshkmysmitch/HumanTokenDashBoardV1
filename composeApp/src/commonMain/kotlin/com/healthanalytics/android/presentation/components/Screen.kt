package com.healthanalytics.android.presentation.components

import com.healthanalytics.android.data.api.Product

sealed class Screen {
    data object HOME : Screen()
    data object PROFILE : Screen()
    data object CONVERSATION_LIST : Screen()
    data class CHAT(val conversationId: String) : Screen()
    data class MARKETPLACE_DETAIL(val product: Product) : Screen()
    data object CART : Screen()
    data object TEST_BOOKING : Screen()
    data object SCHEDULE_TEST_BOOKING : Screen()
}