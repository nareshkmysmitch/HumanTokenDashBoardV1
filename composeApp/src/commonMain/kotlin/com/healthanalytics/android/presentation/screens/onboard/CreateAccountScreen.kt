package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthanalytics.android.components.DHToolBar
import com.healthanalytics.android.components.PrimaryButton
import com.healthanalytics.android.data.models.onboard.AccountCreationResponse
import com.healthanalytics.android.data.models.onboard.AccountDetails
import com.healthanalytics.android.data.models.onboard.CommunicationAddress
import com.healthanalytics.android.presentation.components.ShowDatePicker
import com.healthanalytics.android.presentation.screens.onboard.viewmodel.OnboardViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppStrings
import com.healthanalytics.android.presentation.theme.AppTextStyles
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import com.healthanalytics.android.utils.Resource
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CreateAccountContainer(
    onBackClick: () -> Unit = {},
    navigateToBloodTest: () -> Unit,
    onboardViewModel: OnboardViewModel
) {
    val accountDetails = onboardViewModel.accountDetailsState.collectAsStateWithLifecycle().value
    CreateAccountScreen(
        accountDetails = accountDetails,
        onBackClick = onBackClick,
        onContinueClick = { accountDetails ->
            onboardViewModel.saveAccountDetails(
                accountDetails = accountDetails
            )
            val communicationAddress = CommunicationAddress(
                address_line_1 = accountDetails.streetAddress,
                address_line_2 = accountDetails.city,
                city = accountDetails.state,
                pincode = accountDetails.zipCode
            )
            onboardViewModel.createAccount(communicationAddress)
        },
        onAccountDetailsChange = { onboardViewModel.updateAccountDetails(it) },
        isAccountDetailsValid = { onboardViewModel.isAccountDetailsValid(it) }
    )

    GetAccountCreationResponse(
        accountCreationState = onboardViewModel.accountCreationState,
        navigateToBloodTest = navigateToBloodTest
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    onBackClick: () -> Unit = {},
    onContinueClick: (AccountDetails) -> Unit = { },
    accountDetails: AccountDetails,
    onAccountDetailsChange: (AccountDetails) -> Unit,
    isAccountDetailsValid: (AccountDetails) -> Boolean
) {


    val firstNameFocusRequester = remember { FocusRequester() }
    val weightFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        firstNameFocusRequester.requestFocus()
        keyboardController?.show()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimensions.cardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            DHToolBar(
                title = AppStrings.CREATE_ACCOUNT,
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
                    .padding(bottom = Dimensions.size60dp)
            ) {

                BasicInformation(
                    accountDetails = accountDetails,
                    onAccountDetailsChange = onAccountDetailsChange,
                    firstNameFocusRequester = firstNameFocusRequester
                )

                HealthInformation(
                    accountDetails = accountDetails,
                    onAccountDetailsChange = onAccountDetailsChange,
                    weightFocusRequester = weightFocusRequester,
                )

                AddressDetails(
                    accountDetails = accountDetails,
                    onAccountDetailsChange = onAccountDetailsChange,
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth()
                .background(AppColors.backgroundDark)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            PrimaryButton(
                modifier = Modifier.padding(Dimensions.size16dp),
                enable = false,
                buttonName = AppStrings.CONTINUE,
                onClick = {
                    if (isAccountDetailsValid(accountDetails)) {
                        onContinueClick(accountDetails)
                    }
                }
            )
        }
    }
}

@Composable
fun BasicInformation(
    accountDetails: AccountDetails,
    onAccountDetailsChange: (AccountDetails) -> Unit,
    firstNameFocusRequester: FocusRequester
) {

    Spacer(modifier = Modifier.height(Dimensions.size20dp))

    CategoryTitleText(
        name = AppStrings.BASIC_INFORMATION
    )

    Spacer(modifier = Modifier.height(Dimensions.size20dp))

    var emailError by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val emailRegex = remember {
        Regex("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")
    }

    FirstNameField(
        value = accountDetails.firstName,
        onValueChange = {
            onAccountDetailsChange(
                accountDetails.copy(
                    firstName = it
                )
            )
        },
        focusRequester = firstNameFocusRequester
    )

    Spacer(modifier = Modifier.height(24.dp))

    LastNameField(
        value = accountDetails.lastName,
        onValueChange = { onAccountDetailsChange(accountDetails.copy(lastName = it)) }
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Email Field
    EmailField(
        value = accountDetails.email,
        onValueChange = { newValue ->
            onAccountDetailsChange(accountDetails.copy(email = newValue))
            emailError =
                if (newValue.isNotEmpty() && newValue.contains("@") && !emailRegex.matches(
                        newValue
                    )
                ) {
                    "Please enter a valid email address"
                } else {
                    ""
                }
        },
        error = emailError
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Date of Birth Field
    DobField(
        value = accountDetails.dob,
        onClick = { showDatePicker = true }
    )

    // Date Picker Dialog
    if (showDatePicker) {
        ShowDatePicker(
            selectedDate = accountDetails.dob ?: Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).date,
            onDismiss = {
                showDatePicker = false
            },
            onCancel = {
                showDatePicker = false
            },
            onConfirm = {
                onAccountDetailsChange(accountDetails.copy(dob = it))
                showDatePicker = false
            }
        )
    }

    Spacer(modifier = Modifier.height(Dimensions.size50dp))
}


@Composable
fun HealthInformation(
    accountDetails: AccountDetails,
    weightFocusRequester: FocusRequester,
    onAccountDetailsChange: (AccountDetails) -> Unit,
) {
    var showGenderDropdown by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female")

    CategoryTitleText(
        name = AppStrings.HEALTH_INFORMATION
    )

    Spacer(modifier = Modifier.height(Dimensions.size20dp))

    // Gender Field
    GenderField(
        value = accountDetails.gender,
        onValueChange = { onAccountDetailsChange(accountDetails.copy(gender = it)) },
        expanded = showGenderDropdown,
        onExpandedChange = { showGenderDropdown = !showGenderDropdown },
        genderOptions = genderOptions,
        onDismiss = { showGenderDropdown = false }
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Weight Field
    WeightField(
        value = accountDetails.weight,
        onValueChange = { onAccountDetailsChange(accountDetails.copy(weight = it)) },
        focusRequester = weightFocusRequester
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Height Field
    HeightField(
        value = accountDetails.height,
        onValueChange = { onAccountDetailsChange(accountDetails.copy(height = it)) }
    )

    Spacer(modifier = Modifier.height(Dimensions.size50dp))
}

@Composable
fun AddressDetails(
    accountDetails: AccountDetails,
    onAccountDetailsChange: (AccountDetails) -> Unit,
) {

    CategoryTitleText(
        name = AppStrings.ADDRESS_DETAILS
    )

    Spacer(modifier = Modifier.height(Dimensions.size20dp))

    StreetAddressField(
        value = accountDetails.streetAddress,
        onValueChange = { onAccountDetailsChange(accountDetails.copy(streetAddress = it)) }
    )

    Spacer(modifier = Modifier.height(24.dp))

    CityField(
        value = accountDetails.city,
        onValueChange = { onAccountDetailsChange(accountDetails.copy(city = it)) }
    )

    Spacer(modifier = Modifier.height(24.dp))

    StateField(
        value = accountDetails.state,
        onValueChange = { onAccountDetailsChange(accountDetails.copy(state = it)) }
    )

    Spacer(modifier = Modifier.height(24.dp))

    ZipCodeField(
        value = accountDetails.zipCode,
        onValueChange = { onAccountDetailsChange(accountDetails.copy(zipCode = it)) }
    )

    Spacer(modifier = Modifier.height(50.dp))
}

private fun fieldModifier(modifier: Modifier = Modifier): Modifier =
    modifier
        .height(Dimensions.size56dp)
        .fillMaxWidth()
        .background(
            color = AppColors.gray_100,
            shape = RoundedCornerShape(Dimensions.size12dp)
        )


@Composable
fun FirstNameField(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FieldNameText(name = AppStrings.FIRST_NAME)
        CommonTextField(
            value = value,
            onValueChange = onValueChange,
            focusRequester = focusRequester,
            placeholder = AppStrings.ENTER_YOUR_FIRST_NAME
        )
    }
}

@Composable
fun LastNameField(value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FieldNameText(name = AppStrings.LAST_NAME)
        CommonTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = AppStrings.ENTER_LAST_NAME
        )
    }
}

@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FieldNameText(name = AppStrings.EMAIL)
        CommonTextField(
            value = value,
            onValueChange = onValueChange,
            error = error,
            placeholder = AppStrings.ENTER_EMAIL
        )
    }
}

@Composable
fun DobField(value: LocalDate?, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FieldNameText(name = AppStrings.DOB)

        val dob = if (value != null) {
            "${value.dayOfMonth}-${value.monthNumber}-{${value.year}}"
        } else {
            AppStrings.DOB_DATE_FORMAT
        }

        val textStyle = if (value != null) {
            TextStyle(
                fontFamily = FontFamily.medium(),
                fontSize = FontSize.textSize16sp,
                color = AppColors.primaryTextColor
            )
        } else {
            TextStyle(
                fontFamily = FontFamily.regular(),
                fontSize = FontSize.textSize16sp,
                color = AppColors.textLabelColor
            )
        }

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = fieldModifier(Modifier)
                .clickable { onClick() }
        ) {
            Text(
                text = dob,
                style = textStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(
                    horizontal = Dimensions.size18dp,
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderField(
    value: String,
    onValueChange: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    genderOptions: List<String>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FieldNameText(name = AppStrings.GENDER)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onExpandedChange() }
        ) {
            CommonTextField(
                value = value,
                onValueChange = {},
                modifier = fieldModifier(modifier).menuAnchor(type = MenuAnchorType.PrimaryEditable),
                readOnly = true,
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onDismiss() },
                modifier = Modifier.background(AppColors.inputBorder)
            ) {
                genderOptions.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = AppColors.inputText
                            )
                        },
                        onClick = {
                            onValueChange(option)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WeightField(value: String, onValueChange: (String) -> Unit, focusRequester: FocusRequester) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FieldNameText(name = AppStrings.WEIGHT)
        CommonTextField(
            value = value,
            onValueChange = onValueChange,
            focusRequester = focusRequester,
            placeholder = AppStrings.ENTER_WEIGHT,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
    }
}

@Composable
fun HeightField(value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FieldNameText(name = AppStrings.HEIGHT)
        CommonTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = AppStrings.ENTER_HEIGHT,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
    }
}

@Composable
fun StreetAddressField(value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FieldNameText(name = AppStrings.STREET_ADDRESS)
        CommonTextField(
            value = value,
            onValueChange = onValueChange,
            maxLines = 2,
            placeholder = AppStrings.ENTER_ADDRESS
        )
    }
}

@Composable
fun CityField(value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FieldNameText(name = AppStrings.CITY)
        CommonTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = AppStrings.ENTER_CITY
        )
    }
}

@Composable
fun StateField(value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FieldNameText(name = AppStrings.STATE)
        CommonTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = AppStrings.ENTER_STATE
        )
    }
}

@Composable
fun ZipCodeField(value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FieldNameText(name = AppStrings.PIN_CODE)
        CommonTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = AppStrings.ENTER_PINCODE,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            )
        )
    }
}

@Preview
@Composable
fun CreateAccountScreenPreview() {
    CreateAccountScreen(
        accountDetails = AccountDetails(
            firstName = "",
            lastName = "",
            email = "",
            dob = null,
            gender = "",
            weight = "",
            height = "",
            streetAddress = "",
            city = "",
            state = "",
            zipCode = ""
        ),
        onAccountDetailsChange = { },
        isAccountDetailsValid = { true }
    )
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

@Composable
fun CommonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    maxLines: Int = 1,
    shape: RoundedCornerShape = RoundedCornerShape(Dimensions.cornerRadiusSmall),
    focusRequester: FocusRequester? = null,
    error: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = fieldModifier(modifier).let {
            if (focusRequester != null) it.focusRequester(
                focusRequester
            ) else it
        },
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        readOnly = readOnly,
        enabled = enabled,
        placeholder = {
            if (placeholder != null) {
                Text(
                    text = placeholder,
                    fontFamily = FontFamily.regular(),
                    fontSize = FontSize.textSize16sp,
                    color = AppColors.textLabelColor
                )
            }
        },
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.Transparent,
            unfocusedBorderColor = AppColors.Transparent,
            focusedTextColor = AppColors.primaryTextColor,
            unfocusedTextColor = AppColors.primaryTextColor,
            cursorColor = AppColors.inputText
        ),
        shape = shape,
        textStyle = TextStyle(
            fontFamily = FontFamily.medium(),
            fontSize = FontSize.textSize16sp,
            color = AppColors.primaryTextColor
        )
    )
    if (error.isNotEmpty()) {
        Text(
            text = error,
            style = AppTextStyles.caption,
            color = AppColors.error,
            modifier = Modifier.padding(top = Dimensions.size4dp)
        )
    }
}
