package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppTextStyles
import com.healthanalytics.android.presentation.theme.Dimensions
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleCollectionAddressScreen(
    onBackClick: () -> Unit = {},
    onScheduleClick: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    var streetAddress by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }

    val streetAddressFocusRequester = remember { FocusRequester() }
    val cityFocusRequester = remember { FocusRequester() }
    val stateFocusRequester = remember { FocusRequester() }
    val zipCodeFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

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

            Spacer(modifier = Modifier.height(Dimensions.spacingXXLarge + Dimensions.spacingSmall))

            // Title
            Text(
                text = "Sample Collection Address",
                style = AppTextStyles.headingLarge.copy(fontSize = 28.sp),
                color = AppColors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = Dimensions.spacingXXLarge)
            )

            // Street Address Field
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "STREET ADDRESS",
                    style = AppTextStyles.labelMedium,
                    color = AppColors.textSecondary,
                    modifier = Modifier.padding(bottom = Dimensions.spacingSmall)
                )
                OutlinedTextField(
                    value = streetAddress,
                    onValueChange = { streetAddress = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Enter your full street address",
                            color = AppColors.inputHint,
                            style = AppTextStyles.bodyMedium
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.inputBorder,
                        unfocusedBorderColor = AppColors.outline,
                        focusedTextColor = AppColors.inputText,
                        unfocusedTextColor = AppColors.inputText,
                        cursorColor = AppColors.inputText
                    ),
                    shape = RoundedCornerShape(Dimensions.cornerRadiusSmall)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // City Field
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "CITY",
                    style = AppTextStyles.labelMedium,
                    color = AppColors.textSecondary,
                    modifier = Modifier.padding(bottom = Dimensions.spacingSmall)
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "City",
                            color = AppColors.inputHint,
                            style = AppTextStyles.bodyMedium
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.inputBorder,
                        unfocusedBorderColor = AppColors.outline,
                        focusedTextColor = AppColors.inputText,
                        unfocusedTextColor = AppColors.inputText,
                        cursorColor = AppColors.inputText
                    ),
                    shape = RoundedCornerShape(Dimensions.cornerRadiusSmall)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // State Field
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "STATE",
                    style = AppTextStyles.labelMedium,
                    color = AppColors.textSecondary,
                    modifier = Modifier.padding(bottom = Dimensions.spacingSmall)
                )
                OutlinedTextField(
                    value = state,
                    onValueChange = { state = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "State",
                            color = AppColors.inputHint,
                            style = AppTextStyles.bodyMedium
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.inputBorder,
                        unfocusedBorderColor = AppColors.outline,
                        focusedTextColor = AppColors.inputText,
                        unfocusedTextColor = AppColors.inputText,
                        cursorColor = AppColors.inputText
                    ),
                    shape = RoundedCornerShape(Dimensions.cornerRadiusSmall)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Zip Code Field
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "ZIP CODE",
                    style = AppTextStyles.labelMedium,
                    color = AppColors.textSecondary,
                    modifier = Modifier.padding(bottom = Dimensions.spacingSmall)
                )
                OutlinedTextField(
                    value = zipCode,
                    onValueChange = { zipCode = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Zip code",
                            color = AppColors.inputHint,
                            style = AppTextStyles.bodyMedium
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.inputBorder,
                        unfocusedBorderColor = AppColors.outline,
                        focusedTextColor = AppColors.inputText,
                        unfocusedTextColor = AppColors.inputText,
                        cursorColor = AppColors.inputText
                    ),
                    shape = RoundedCornerShape(Dimensions.cornerRadiusSmall)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Spacer(modifier = Modifier.weight(1f))

            // Schedule Button
            Button(
                onClick = {
                    if (streetAddress.isNotEmpty() && city.isNotEmpty() && 
                        state.isNotEmpty() && zipCode.isNotEmpty()) {
                        onScheduleClick(streetAddress, city, state, zipCode)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.buttonHeight),
                enabled = streetAddress.isNotEmpty() && city.isNotEmpty() && 
                         state.isNotEmpty() && zipCode.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.buttonBackground,
                    contentColor = AppColors.buttonText,
                    disabledContainerColor = AppColors.inputBackground,
                    disabledContentColor = AppColors.textSecondary
                ),
                shape = RoundedCornerShape(Dimensions.cornerRadiusLarge)
            ) {
                Text(
                    text = "Schedule",
                    style = AppTextStyles.buttonText
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))
        }
    }
}