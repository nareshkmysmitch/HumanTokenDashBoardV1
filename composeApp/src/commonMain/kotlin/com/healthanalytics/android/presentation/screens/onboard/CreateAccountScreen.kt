
package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppTextStyles
import com.healthanalytics.android.presentation.theme.Dimensions
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    onBackClick: () -> Unit = {},
    onContinueClick: (String, String, String) -> Unit = { _, _, _ -> }
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val firstNameFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100) // Small delay to ensure the UI is fully composed
        firstNameFocusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.backgroundDark)
            .padding(Dimensions.screenPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Back Button and Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_calendar_icon),
                        contentDescription = "Back",
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "Deep Holistics",
                    style = AppTextStyles.headingSmall.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = AppColors.textPrimary
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Empty space to balance the layout
                Spacer(modifier = Modifier.size(40.dp))
            }
            
            Spacer(modifier = Modifier.height(80.dp))
            
            // Title
            Text(
                text = "Create your account",
                style = AppTextStyles.headingSmall.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = AppColors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 48.dp)
            )
            
            // First Name and Last Name Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "FIRST NAME",
                        style = AppTextStyles.labelMedium,
                        color = AppColors.textSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(firstNameFocusRequester),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.textSecondary,
                            unfocusedBorderColor = AppColors.textSecondary.copy(alpha = 0.5f),
                            focusedTextColor = AppColors.textPrimary,
                            unfocusedTextColor = AppColors.textPrimary
                        ),
                        singleLine = true
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LAST NAME",
                        style = AppTextStyles.labelMedium,
                        color = AppColors.textSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.textSecondary,
                            unfocusedBorderColor = AppColors.textSecondary.copy(alpha = 0.5f),
                            focusedTextColor = AppColors.textPrimary,
                            unfocusedTextColor = AppColors.textPrimary
                        ),
                        singleLine = true
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Email Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "EMAIL",
                    style = AppTextStyles.labelMedium,
                    color = AppColors.textSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text(
                            text = "Enter your email address",
                            color = AppColors.textSecondary.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.textSecondary,
                        unfocusedBorderColor = AppColors.textSecondary.copy(alpha = 0.5f),
                        focusedTextColor = AppColors.textPrimary,
                        unfocusedTextColor = AppColors.textPrimary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Continue Button
            Button(
                onClick = {
                    if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()) {
                        onContinueClick(firstName, lastName, email)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.textSecondary,
                    contentColor = AppColors.backgroundDark,
                    disabledContainerColor = AppColors.textSecondary.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = "Continue",
                    style = AppTextStyles.buttonText,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
