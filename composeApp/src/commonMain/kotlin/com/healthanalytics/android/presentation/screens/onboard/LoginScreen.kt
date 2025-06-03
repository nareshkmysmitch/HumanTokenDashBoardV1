
package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onContinueClick: (String) -> Unit = {},
    onCountryCodeClick: () -> Unit = {}
) {
    var phoneNumber by remember { mutableStateOf("") }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.backgroundDark)
            .padding(Dimensions.screenPadding),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.spacingMedium),
            shape = RoundedCornerShape(Dimensions.cornerRadiusXLarge),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.surfaceDark
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.cardPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(Dimensions.spacingXLarge))
                
                // App Logo/Name
                Text(
                    text = "dh ${AppStrings.appName}",
                    style = AppTextStyles.headingMedium,
                    color = AppColors.textPrimary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(Dimensions.spacingXXLarge))
                
                // Login Title
                Text(
                    text = AppStrings.loginTitle,
                    style = AppTextStyles.headingSmall,
                    color = AppColors.textPrimary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(Dimensions.spacingXSmall))
                
                // Login Subtitle
                Text(
                    text = AppStrings.loginSubtitle,
                    style = AppTextStyles.headingSmall,
                    color = AppColors.textPrimary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(Dimensions.spacingXXLarge))
                
                // Phone Number Label
                Text(
                    text = AppStrings.phoneNumberLabel,
                    style = AppTextStyles.labelMedium,
                    color = AppColors.textSecondary,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(Dimensions.spacingSmall))
                
                // Phone Number Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country Code Button
                    OutlinedButton(
                        onClick = onCountryCodeClick,
                        modifier = Modifier
                            .height(Dimensions.inputFieldHeight),
                        shape = RoundedCornerShape(
                            topStart = Dimensions.cornerRadiusMedium,
                            bottomStart = Dimensions.cornerRadiusMedium
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = AppColors.inputBackground,
                            contentColor = AppColors.textPrimary
                        ),
                        border = null
                    ) {
                        Text(
                            text = "${AppStrings.countryCode} ▼",
                            style = AppTextStyles.bodyMedium,
                            color = AppColors.textPrimary
                        )
                    }
                    
                    // Phone Number TextField
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        placeholder = {
                            Text(
                                text = AppStrings.phoneNumberPlaceholder,
                                style = AppTextStyles.bodyMedium,
                                color = AppColors.inputHint
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimensions.inputFieldHeight),
                        textStyle = AppTextStyles.bodyMedium.copy(
                            color = AppColors.inputText
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(
                            topEnd = Dimensions.cornerRadiusMedium,
                            bottomEnd = Dimensions.cornerRadiusMedium
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = AppColors.inputBackground,
                            unfocusedContainerColor = AppColors.inputBackground,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = AppColors.primary
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(Dimensions.spacingXLarge))
                
                // Continue Button
                Button(
                    onClick = { onContinueClick(phoneNumber) },
                    modifier = Modifier
                        .width(200.dp)
                        .height(Dimensions.buttonHeight),
                    shape = RoundedCornerShape(Dimensions.cornerRadiusLarge),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.buttonBackground,
                        contentColor = AppColors.buttonText
                    ),
                    enabled = phoneNumber.isNotBlank()
                ) {
                    Text(
                        text = AppStrings.continueButton,
                        style = AppTextStyles.buttonText
                    )
                }
                
                Spacer(modifier = Modifier.height(Dimensions.spacingXLarge))
            }
        }
    }
}
