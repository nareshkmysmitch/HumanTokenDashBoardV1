package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthanalytics.android.data.models.onboard.Slot
import com.healthanalytics.android.data.models.onboard.SlotsAvailability
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppTextStyles
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.utils.DateUtils
import com.healthanalytics.android.utils.Resource
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource


@Composable
fun ScheduleBloodTestContainer(
    onboardViewModel: OnboardViewModel,
    onBackClick: () -> Unit,
    navigateToPayment: () -> Unit,
) {
    val slotState by onboardViewModel.slotAvailability.collectAsStateWithLifecycle()
    val slotUpdateState by onboardViewModel.updateSlot.collectAsStateWithLifecycle(null)

    LaunchedEffect(Unit) {
        onboardViewModel.getSlotAvailability(
            selectedDate = DateUtils.getCurrentUtcTime()
        )
    }

    ScheduleBloodTestScreen(
        slotState = slotState,
        onBackClick = onBackClick,
        slotUpdateState = slotUpdateState,
        navigateToPayment = navigateToPayment,
        onContinueClick = {
            onboardViewModel.updateSlot(it)
        },
        onDateChanged = {
            onboardViewModel.getSlotAvailability(
                selectedDate = DateUtils.getIso(it)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleBloodTestScreen(
    onBackClick: () -> Unit = {},
    onContinueClick: (Slot) -> Unit = {},
    onDateChanged: (LocalDate) -> Unit = {},
    slotState: Resource<SlotsAvailability?>,
    navigateToPayment: () -> Unit,
    slotUpdateState: Resource<SlotsAvailability?>?,
) {
    var selectedTimeSlot by remember { mutableStateOf<Slot?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember {
        mutableStateOf(
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        )
    }

    LaunchedEffect(slotState.data?.slots) {
        if (slotState.data?.slots?.isEmpty() == true) {
            selectedTimeSlot = null
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.toEpochDays().toLong() * 24 * 60 * 60 * 1000
    )

    // Format selected date for display
    val formattedDate = remember(selectedDate) {
        val dayOfWeek = selectedDate.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        val month = selectedDate.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        "$dayOfWeek, ${selectedDate.dayOfMonth} $month, ${selectedDate.year}"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.backgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimensions.cardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top section with back button and logo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimensions.spacingMedium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                TextButton(
                    onClick = onBackClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = AppColors.textPrimary
                    )
                ) {
                    Text(
                        text = "← Back",
                        style = AppTextStyles.bodyMedium,
                        color = AppColors.textPrimary
                    )
                }

                // Logo and title
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_calendar_icon),
                        contentDescription = "Logo",
                        modifier = Modifier.size(Dimensions.iconSize)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                    Text(
                        text = "Deep Holistics",
                        style = AppTextStyles.headingSmall,
                        color = AppColors.textPrimary
                    )
                }

                // Empty space for balance
                Spacer(modifier = Modifier.width(Dimensions.spacingXXLarge))
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingXXLarge))

            // Main content
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                // Title
                Text(
                    text = "Schedule your blood test",
                    style = AppTextStyles.headingLarge.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = AppColors.textPrimary,
                    modifier = Modifier.padding(bottom = Dimensions.spacingMedium)
                )

                // Description
                Text(
                    text = "Get started with 100+ advanced biomarkers measuring everything from energy and mood-related markers to cancers, heart diseases and more. We connect the dots across your entire health profile.",
                    style = AppTextStyles.bodyMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    ),
                    color = AppColors.textSecondary,
                    modifier = Modifier.padding(bottom = Dimensions.spacingXXLarge)
                )

                // Date section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimensions.spacingLarge),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = formattedDate,
                            style = AppTextStyles.headingMedium.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = AppColors.textPrimary
                        )
                        Text(
                            text = "Fasting test",
                            style = AppTextStyles.bodyMedium,
                            color = AppColors.textSecondary
                        )
                    }

                    // Calendar icon
                    Icon(
                        painter = painterResource(Res.drawable.ic_calendar_icon),
                        contentDescription = "Calendar",
                        tint = AppColors.textSecondary,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { showDatePicker = true }
                    )
                }

                // Time slots grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = Dimensions.spacingXXLarge)
                ) {
                    items(slotState.data?.slots ?: listOf()) { timeSlot ->
                        val startLocalDateTime = timeSlot.start_time?.let {
                            DateUtils.fromIsoFormat(it)
                        }
                        val startDateFormat = DateUtils.formatForDisplay(startLocalDateTime)

                        TimeSlotCard(
                            time = startDateFormat,
                            isSelected = selectedTimeSlot == timeSlot,
                            onClick = {
                                selectedTimeSlot = timeSlot
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue Button
            Button(
                onClick = {
                    selectedTimeSlot?.let { onContinueClick(it) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.buttonHeight),
                enabled = selectedTimeSlot != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.buttonBackground,
                    contentColor = AppColors.buttonText,
                    disabledContainerColor = AppColors.inputBackground,
                    disabledContentColor = AppColors.textSecondary
                ),
                shape = RoundedCornerShape(Dimensions.cornerRadiusLarge)
            ) {
                Text(
                    text = "Continue",
                    style = AppTextStyles.buttonText
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))
        }

        getSlotUpdatedResponse(
            slotUpdateState = slotUpdateState,
            navigateToPayment = navigateToPayment
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate =
                            kotlinx.datetime.Instant.fromEpochMilliseconds(millis).toLocalDateTime(
                                TimeZone.currentSystemDefault()
                            ).date

                        onDateChanged(selectedDate)
                    }
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun TimeSlotCard(
    time: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() }
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = AppColors.buttonBackground,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                AppColors.buttonBackground.copy(alpha = 0.1f)
            else
                Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = time,
                style = AppTextStyles.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (isSelected) AppColors.buttonBackground else AppColors.textPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun getSlotUpdatedResponse(
    slotUpdateState: Resource<SlotsAvailability?>?,
    navigateToPayment: () -> Unit
) {
    when (slotUpdateState) {
        is Resource.Loading<*> -> {}
        is Resource.Error<*> -> {}
        is Resource.Success<*> -> {
            LaunchedEffect(slotUpdateState) {
                navigateToPayment()
            }
        }
        else -> {}
    }
}