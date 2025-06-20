package com.healthanalytics.android.presentation.screens.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Man
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.models.UpdateAddressListResponse
import com.healthanalytics.android.data.models.questionnaire.Questionnaire
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.screens.questionnaire.viewmodel.QuestionnaireViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import com.healthanalytics.android.ui.ShowAlertDialog
import com.healthanalytics.android.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: MarketPlaceViewModel,
    onNavigateToTestBooking: () -> Unit,
    questionnaireViewModel: QuestionnaireViewModel,
    onQuestionnaireNavigate: () -> Unit = {}
) {
    var showAlertDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    // Get values from ViewModel states
    val userName by viewModel.userName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val userPhone by viewModel.userPhone.collectAsState()
    val addressList by viewModel.addressList.collectAsState()
    val accessToken by viewModel.accessToken.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.clearLogoutState()
    }

    var name by remember(userName) { mutableStateOf(userName ?: "") }
    var email by remember(userEmail) { mutableStateOf(userEmail ?: "") }
    var phone by remember(userPhone) { mutableStateOf(userPhone ?: "") }
    var dateOfBirth by remember { mutableStateOf("December 20, 1998") }

    val selectedAddress by viewModel.selectedAddress.collectAsState()

    // Initialize address fields from selectedAddress
    var address1 by remember(selectedAddress) {
        mutableStateOf(selectedAddress?.address?.address_line_1 ?: "")
    }
    var address2 by remember(selectedAddress) {
        mutableStateOf(selectedAddress?.address?.address_line_2 ?: "")
    }
    var city by remember(selectedAddress) {
        mutableStateOf(selectedAddress?.address?.city ?: "")
    }
    var state by remember(selectedAddress) {
        mutableStateOf(selectedAddress?.address?.state ?: "")
    }
    var pincode by remember(selectedAddress) {
        mutableStateOf(selectedAddress?.address?.pincode ?: "")
    }
    var country by remember(selectedAddress) {
        mutableStateOf(selectedAddress?.address?.country ?: "")
    }
    var addressId by remember(selectedAddress) {
        mutableStateOf(selectedAddress?.address_id ?: "")
    }

    val response by questionnaireViewModel.questionnaireFlow.collectAsStateWithLifecycle(
        Resource.Loading()
    )

    // Load addresses when access token becomes available
    LaunchedEffect(accessToken) {
        questionnaireViewModel.saveQuestionnaireDetails(
            assessmentId = "105", nextQuestionId = 0, displayName = "LifeStyle"
        )
        if (accessToken != null) {
            println("Loading addresses with available token")
            viewModel.loadAddresses()
            questionnaireViewModel.getQuestionnaires()
        }
    }

    BackHandler(enabled = true, onBack = {
        if (!isEditing) {
            onNavigateBack()
        } else {
            isEditing = false
        }
    })
    Scaffold(
        topBar = {
            TopAppBar(

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Black,
                    navigationIconContentColor = AppColors.White,
                    titleContentColor = AppColors.White
                ),

                title = {
                    Text(
                        text = if (isEditing) "Edit Profile" else "Your Profile",
                        color = AppColors.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!isEditing) {
                            onNavigateBack()
                        } else {
                            isEditing = false
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back arrow",
                            tint = AppColors.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
            )
        }, containerColor = Color.Black
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(paddingValues), color = Color.Black
        ) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                if (!isEditing) {
                    when (response) {
                        is Resource.Loading -> {
                        }

                        is Resource.Success -> {
                            val (totalQuestions, answeredQuestion) = getQuestionnaireCount(result = response.data)

                            val completionPercent = if (totalQuestions > 0) {
                                (answeredQuestion.toFloat() / totalQuestions.toFloat()) * 100f
                            } else {
                                0f
                            }

                            val totalProgress =
                                0.5f + (completionPercent / 2f / 100f) // Scales from 0.5 to 1.0
                            Logger.v("totalProgress: $totalProgress, $completionPercent")
                            ProfileCompletionCard(
                                progress = totalProgress,
                                answered = answeredQuestion,
                                total = totalQuestions,
                                onNavigateToQuestionnaire = {
                                    onQuestionnaireNavigate()
                                })

                        }

                        is Resource.Error -> {
                            response.data?.let {
                                LaunchedEffect(Unit) {
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1C1C1E)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Account Information",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Manage your personal information",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                Row(
                                    modifier = Modifier.padding(bottom = 24.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.size(64.dp).clip(CircleShape)
                                            .background(Color(0xFF8B5CF6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Man,
                                            contentDescription = "Profile",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = name,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White
                                        )
                                        Text(
                                            text = email, fontSize = 14.sp, color = Color.Gray
                                        )
                                    }
                                }

                                ProfileInfoItem("Phone Number", phone)
                                ProfileInfoItem("Date of Birth", dateOfBirth)

                                Text(
                                    text = "Address",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                Text(text = address1, color = Color.Gray)
                                if (address2.isNotEmpty()) Text(text = address2, color = Color.Gray)
                                Text(text = "$city, $state $pincode", color = Color.Gray)
                                Text(text = country, color = Color.Gray)

                                Button(
                                    onClick = { isEditing = true },
                                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF8B5CF6)
                                    )
                                ) {
                                    Text("Edit Profile", color = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(Dimensions.size24dp))

                        CommunicationPreference(viewModel = viewModel, onStyleSelected = {
                            viewModel.setCommunicationPreference(it)
                        }, onSaveClicked = { communication ->
                            if (!accessToken.isNullOrEmpty() && communication != null) {
                                viewModel.saveCommunicationPreference(
                                    accessToken!!, communication
                                )
                            }
                        })

                        Spacer(modifier = Modifier.height(Dimensions.size24dp))

                        HealthMetrics(viewModel = viewModel, onSaved = { editWeight, editHeight ->
                            if (!accessToken.isNullOrEmpty()) {
                                viewModel.saveHealthMetrics(
                                    accessToken!!, editWeight, editHeight
                                )
                            }
                        })

                        Button(
                            onClick = { onNavigateToTestBooking() },
                            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray
                            )
                        ) {
                            Text("Test Booking", color = Color.Black)
                        }

                        Button(
                            onClick = { showAlertDialog = true },
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent, contentColor = Color.White
                            ),
                            border = ButtonDefaults.outlinedButtonBorder
                        ) {
                            Text("Log Out")
                        }
                    }
                } else {
                    // Edit Profile Screen
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Account Information",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = "Manage your personal information",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        ProfileTextField(
                            value = name, onValueChange = { name = it }, label = "Full Name"
                        )
                        ProfileTextField(
                            value = email, onValueChange = { email = it }, label = "Email"
                        )
                        ProfileTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = "Phone Number",
                            enabled = false,
                        )
                        ProfileTextField(
                            value = dateOfBirth,
                            onValueChange = { dateOfBirth = it },
                            label = "Date of Birth",
                            enabled = false,
                        )

                        Text(
                            text = "Address",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        ProfileTextField(
                            value = address1,
                            onValueChange = { address1 = it },
                            label = "Address Line 1"
                        )
                        ProfileTextField(
                            value = address2,
                            onValueChange = { address2 = it },
                            label = "Address Line 2 (optional)"
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ProfileTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = "City",
                                modifier = Modifier.weight(1f)
                            )
                            ProfileTextField(
                                value = state,
                                onValueChange = { state = it },
                                label = "State",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        ProfileTextField(
                            value = pincode, onValueChange = { pincode = it }, label = "Pincode"
                        )
                        ProfileTextField(
                            value = country, onValueChange = { country = it }, label = "Country"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val addressData = UpdateAddressListResponse(
                                        address_line_1 = address1,
                                        address_line_2 = address2,
                                        city = city,
                                        state = state,
                                        pincode = pincode,
                                        country = country,
                                        di_address_id = addressId
                                    )

                                    viewModel.updateProfile(
                                        name = name,
                                        email = email,
                                        phone = phone,
                                        address = addressData,
                                        diAddressId = addressId
                                    ) { success, message ->
                                        if (success) {
                                            isEditing = false
                                            viewModel.loadAddresses() // Reload addresses after successful update
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8B5CF6)
                                )
                            ) {
                                Text("Save")
                            }
                            Button(
                                onClick = { isEditing = false },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent, contentColor = Color.White
                                ),
                                border = ButtonDefaults.outlinedButtonBorder
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                }


            }




            if (showAlertDialog) {
                ShowAlertDialog(
                    modifier = Modifier,
                    title = "Log out",
                    message = "You will be logged out of your Deep Holistics account. However this doesn't affect your logged data. Do you want to still logout?",
                    onDismiss = { showAlertDialog = false },
                    onLogout = {
                        viewModel.logout()
                        showAlertDialog = false
                    })
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(label: String, value: String) {
    Column(
        modifier = Modifier.padding(vertical = Dimensions.size8dp)
    ) {
        Text(
            text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White
        )
        Text(
            text = value, fontSize = 14.sp, color = Color.Gray
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White) },
        modifier = modifier.fillMaxWidth().padding(vertical = Dimensions.size8dp),
        enabled = enabled,
        colors = TextFieldColors(
            cursorColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedTrailingIconColor = Color.White,
            unfocusedTrailingIconColor = Color.White,
            disabledTrailingIconColor = Color.White,
            focusedIndicatorColor = AppColors.Pink,
            unfocusedIndicatorColor = Color.White,
            disabledIndicatorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            disabledTextColor = Color.White,
            disabledLeadingIconColor = Color.White,
            disabledPlaceholderColor = Color.White,
            disabledLabelColor = Color.White,
            focusedPlaceholderColor = Color.White,
            unfocusedPlaceholderColor = Color.White,
            unfocusedLeadingIconColor = Color.White,
            errorCursorColor = Color.White,
            errorLabelColor = Color.White,
            errorLeadingIconColor = Color.White,
            errorTrailingIconColor = Color.White,
            errorContainerColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            errorPlaceholderColor = Color.White,
            errorTextColor = Color.White,
            focusedLeadingIconColor = Color.White,
            focusedSupportingTextColor = Color.White,
            unfocusedSupportingTextColor = Color.White,
            disabledSupportingTextColor = Color.White,
            errorSupportingTextColor = Color.White,
            focusedPrefixColor = Color.White,
            unfocusedPrefixColor = Color.White,
            disabledPrefixColor = Color.White,
            errorPrefixColor = Color.White,
            focusedSuffixColor = Color.White,
            unfocusedSuffixColor = Color.White,
            disabledSuffixColor = Color.White,
            errorSuffixColor = Color.White,
            textSelectionColors = TextSelectionColors(
                handleColor = Color.White, backgroundColor = Color.White
            )
        ),
        shape = RoundedCornerShape(Dimensions.size8dp)
    )
}

@Composable
fun ProfileCompletionCard(
    progress: Float = 0.5f,
    answered: Int,
    total: Int,
    onNavigateToQuestionnaire: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(Dimensions.size16dp),
        shape = RoundedCornerShape(Dimensions.size16dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B))
    ) {
        Column(modifier = Modifier.padding(Dimensions.size14dp)) {
            Text(
                text = "Profile Completion",
                color = AppColors.textPrimaryColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "Complete your profile to get better health insights",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = Dimensions.size4dp, bottom = Dimensions.size16dp)
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(Dimensions.size8dp)
                    .clip(RoundedCornerShape(Dimensions.size4dp)),
                color = AppColors.BlueStroke,
                trackColor = Color(0xFF23232A),
            )
            Text(
                text = "${(progress * 100).toInt()}% Complete",
                color = Color(0xFF4F8CFF),
                fontSize = FontSize.textSize14sp,
                fontFamily = FontFamily.medium(),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(Dimensions.size20dp))
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(Color(0xFF23232A), RoundedCornerShape(Dimensions.size12dp))
                    .padding(Dimensions.size16dp), verticalAlignment = Alignment.CenterVertically
            ) {
                if(answered != total) {
                    Box(
                        modifier = Modifier.size(Dimensions.size32dp)
                            .background(Color(0xFFFFC107), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Man,
                            contentDescription = null,
                            tint = AppColors.Black,
                            modifier = Modifier.size(Dimensions.size20dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.size(Dimensions.size32dp)
                            .background(AppColors.lightGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = AppColors.White,
                            modifier = Modifier.size(Dimensions.size20dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Medical History Questionnaire",
                        fontFamily = FontFamily.medium(),
                        color = AppColors.textPrimaryColor,
                        fontSize = FontSize.textSize16sp,
                    )
                    Text(
                        text = "$answered/$total",
                        fontFamily = FontFamily.medium(),
                        fontSize = FontSize.textSize14sp,
                        color = AppColors.inputHint,
                    )
                }
            }
            if (answered != total) {
                Button(
                    onClick = onNavigateToQuestionnaire,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.BlueStroke),
                    shape = RoundedCornerShape(Dimensions.size8dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Man,
                        contentDescription = null,
                        tint = AppColors.textPrimaryColor,
                        modifier = Modifier.size(Dimensions.size18dp)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.size6dp))
                    Text(
                        text = "Complete Remaining Questions",
                        color = AppColors.textPrimaryColor,
                        fontSize = FontSize.textSize14sp,
                    )
                }
            }
        }
    }
}

/*

private fun getQuestionnaireCount(
    result: Questionnaire?,
): Pair<Int, Int> {
    val totalCount = result?.ha_questions?.size ?: 0
    val completedCount = getAnsweredQuestionCount(result?.ha_questions ?: emptyList())
    Logger.e("result --> $totalCount, $completedCount")
//    return Pair(totalCount, completedCount)
    return totalCount to completedCount
}
*/

/*
fun getAnsweredQuestionCount(questions: List<Question>): Int {
    val questionMap = questions.associateBy { it.id }
    val firstQuestion = questions.find { it.is_first_question == true } ?: return 0

    var count = 0
    var currentQuestion = firstQuestion

    while (true) {
        val hasSelected = currentQuestion.answers?.any { it.is_selected == true }

        if (hasSelected == true) {
            count++
            val nextId = currentQuestion.default_next_question_id
            if (nextId == null || !questionMap.containsKey(nextId)) break
            currentQuestion = questionMap[nextId]!!
        } else {
            break
        }
    }
    return count
}
*/

fun getQuestionnaireCount(result: Questionnaire?): Pair<Int, Int> {
    val questions = result?.ha_questions
    var total = 0
    var answered = 0

    questions?.forEach { question ->
        total += 1

        when (question.input_element) {
            "radio", "checkbox" -> {
                val selected = question.answers?.any { it.is_selected == true } == true
                if (selected) answered += 1
            }

            "textbox" -> {
                if (!question.text_answer.isNullOrBlank()) answered += 1
            }

            else -> {
                // Optional: handle other input types
            }
        }
    }

    return Pair(total, answered)
}

