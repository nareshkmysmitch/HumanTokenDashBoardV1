package com.healthanalytics.android.presentation.screens.testbooking

import androidx.compose.runtime.Composable
import com.healthanalytics.android.BackHandler


@Composable
fun ScheduleTestBookingScreen(
    onNavigateBack: () -> Unit,
) {
    BackHandler(enabled = true, onBack = onNavigateBack)
}