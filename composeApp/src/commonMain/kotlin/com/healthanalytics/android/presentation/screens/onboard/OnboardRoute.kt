package com.healthanalytics.android.presentation.screens.onboard

import kotlinx.serialization.Serializable

sealed class OnboardRoute {

    @Serializable
    data object GetStarted : OnboardRoute()

    @Serializable
    data object Login : OnboardRoute()

    @Serializable
    data object OTPVerification : OnboardRoute()

    @Serializable
    data object CreateAccount : OnboardRoute()

    @Serializable
    data object ScheduleBloodTest : OnboardRoute()

    @Serializable
    data object Payment : OnboardRoute()

}