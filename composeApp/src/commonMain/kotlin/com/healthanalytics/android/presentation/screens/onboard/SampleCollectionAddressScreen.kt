package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthanalytics.android.components.DHToolBar
import com.healthanalytics.android.components.PrimaryButton
import com.healthanalytics.android.data.models.onboard.AccountCreationResponse
import com.healthanalytics.android.data.models.onboard.CommunicationAddress
import com.healthanalytics.android.presentation.screens.onboard.viewmodel.OnboardViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppStrings
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.utils.Resource
import kotlinx.coroutines.flow.SharedFlow
import cafe.adriel.voyager.core.screen.Screen

@Composable
fun SampleCollectionAddressContainer(
    onboardViewModel: OnboardViewModel,
    onBackClick: () -> Unit,
    navigateToBloodTest: () -> Unit,
) {
    SampleCollectionAddressScreen(
        addressDetails = onboardViewModel.getAddressDetails(),
        accountCreationState = onboardViewModel.accountCreationState,
        onBackClick = onBackClick,
        navigateToBloodTest = navigateToBloodTest,
        onScheduleClick = { streetAddress, city, state, zipCode ->
            val communicationAddress = CommunicationAddress(
                address_line_1 = streetAddress,
                address_line_2 = city,
                city = state,
                pincode = zipCode
            )
            onboardViewModel.createAccount(communicationAddress)
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleCollectionAddressScreen(
    onBackClick: () -> Unit = {},
    onScheduleClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    accountCreationState: SharedFlow<Resource<AccountCreationResponse?>>,
    navigateToBloodTest: () -> Unit,
    addressDetails: CommunicationAddress?
) {
    var streetAddress by remember { mutableStateOf(addressDetails?.address_line_1 ?: "") }
    var city by remember { mutableStateOf(addressDetails?.address_line_2 ?: "") }
    var state by remember { mutableStateOf(addressDetails?.state ?: "" ) }
    var zipCode by remember { mutableStateOf(addressDetails?.pincode ?: "") }

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

            DHToolBar(
                title = AppStrings.SAMPLE_COLLECTION_ADDRESS,
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(Dimensions.size50dp))

            Column(
                modifier = Modifier.fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = Dimensions.size60dp)
            ) {
                // Street Address Field
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FieldNameText(
                        name = AppStrings.STREET_ADDRESS
                    )
                    OutlinedTextField(
                        maxLines = 2,
                        value = streetAddress,
                        onValueChange = { streetAddress = it },
                        modifier = Modifier.fillMaxWidth(),
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
                    FieldNameText(
                        name = AppStrings.CITY
                    )
                    OutlinedTextField(
                        maxLines = 1,
                        value = city,
                        onValueChange = { city = it },
                        modifier = Modifier.fillMaxWidth(),
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
                    FieldNameText(
                        name = AppStrings.STATE
                    )
                    OutlinedTextField(
                        maxLines = 1,
                        value = state,
                        onValueChange = { state = it },
                        modifier = Modifier.fillMaxWidth(),
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
                    FieldNameText(
                        name = AppStrings.PIN_CODE
                    )
                    OutlinedTextField(
                        maxLines = 1,
                        value = zipCode,
                        onValueChange = { zipCode = it },
                        modifier = Modifier.fillMaxWidth(),
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
            }

            Spacer(modifier = Modifier.height(Dimensions.size16dp))
        }

        Box(
            modifier = Modifier.fillMaxWidth()
                .background(AppColors.backgroundDark)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            PrimaryButton(
                modifier = Modifier.padding(Dimensions.size16dp),
                buttonName = AppStrings.SCHEDULE,
                isEnable = streetAddress.isNotEmpty() && city.isNotEmpty() &&
                        state.isNotEmpty() && zipCode.isNotEmpty(),
                onclick = {
                    if (streetAddress.isNotEmpty() && city.isNotEmpty() &&
                        state.isNotEmpty() && zipCode.isNotEmpty()
                    ) {
                        onScheduleClick(
                            streetAddress.trim(),
                            city.trim(),
                            state.trim(),
                            zipCode.trim()
                        )
                    }
                }
            )
        }

        GetAccountCreationResponse(
            accountCreationState = accountCreationState,
            navigateToBloodTest = navigateToBloodTest
        )
    }
}

@Composable
fun GetAccountCreationResponse(
    accountCreationState: SharedFlow<Resource<AccountCreationResponse?>>,
    navigateToBloodTest: () -> Unit
) {
    val response by accountCreationState.collectAsStateWithLifecycle(null)
    when (response) {
        is Resource.Error<*> -> {}

        is Resource.Loading<*> -> {}

        is Resource.Success<*> -> {
            LaunchedEffect(response) {
                navigateToBloodTest()
            }
        }

        else -> {}
    }
}

class SampleCollectionAddressScreenNav(
    private val onboardViewModel: OnboardViewModel,
    private val onBackClick: () -> Unit,
    private val navigateToBloodTest: () -> Unit
) : Screen {
    @Composable
    override fun Content() {
        SampleCollectionAddressContainer(
            onboardViewModel = onboardViewModel,
            onBackClick = onBackClick,
            navigateToBloodTest = navigateToBloodTest
        )
    }
}