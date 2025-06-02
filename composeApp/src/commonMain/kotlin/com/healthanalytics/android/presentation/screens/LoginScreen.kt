package com.healthanalytics.android.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.data.repositories.HealthRepository
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Preview()
@Composable
fun LoginScreenPreview() {
    // Provide a basic Material3 theme wrapper
    MaterialTheme {
        LoginScreen(
            onLoginSuccess = { token ->
                // Just log or handle the token for preview
                println("Login succeeded with token: $token")
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    repository: HealthRepository = HealthRepository()
) {
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var showOtpField by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Health Analytics",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Enter your phone number to get started",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            enabled = !showOtpField
        )
        
        if (showOtpField) {
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = otp,
                onValueChange = { otp = it },
                label = { Text("Enter OTP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = ""
                    
                    if (!showOtpField) {
                        // Send OTP
                        repository.sendOtp(phoneNumber).fold(
                            onSuccess = { response ->
                                if (response.status == "success") {
                                    showOtpField = true
                                } else {
                                    errorMessage = response.message
                                }
                            },
                            onFailure = { error ->
                                errorMessage = error.message ?: "Failed to send OTP"
                            }
                        )
                    } else {
                        // Verify OTP
                        repository.verifyOtp(phoneNumber, otp).fold(
                            onSuccess = { response ->
                                if (response.status == "success" && response.data?.access_token != null) {
                                    onLoginSuccess(response.data.access_token)
                                } else {
                                    errorMessage = response.message
                                }
                            },
                            onFailure = { error ->
                                errorMessage = error.message ?: "Failed to verify OTP"
                            }
                        )
                    }
                    
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && phoneNumber.isNotEmpty() && (!showOtpField || otp.isNotEmpty())
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (showOtpField) "Verify OTP" else "Send OTP")
            }
        }
        
        if (showOtpField) {
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = {
                    showOtpField = false
                    otp = ""
                    errorMessage = ""
                }
            ) {
                Text("Change Phone Number")
            }
        }
    }
}