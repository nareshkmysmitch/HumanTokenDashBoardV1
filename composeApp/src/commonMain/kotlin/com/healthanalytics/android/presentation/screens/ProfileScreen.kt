package com.healthanalytics.android.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.models.UpdateAddressListResponse
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.ui.ShowAlertDialog
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: MarketPlaceViewModel = koinViewModel()
) {
    var showAlertDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("Agent Nash") }
    var email by remember { mutableStateOf("agentnash@yopmail.com") }
    var phone by remember { mutableStateOf("+91 9677004512") }
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

    // Load addresses when the screen is first shown
    LaunchedEffect(Unit) {
        viewModel.loadAddresses()
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
                title = {
                    Text(
                        text = if (isEditing) "Edit Profile" else "Your Profile",
                        color = AppColors.primary,
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
                            painter = painterResource(Res.drawable.ic_calendar_icon),
                            contentDescription = "back arrow",
                            tint = AppColors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            color = Color.Black
        ) {
            if (!isEditing) {
                // Profile View Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
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
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF8B5CF6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_calendar_icon),
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
                                        text = email,
                                        fontSize = 14.sp,
                                        color = Color.Gray
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8B5CF6)
                                )
                            ) {
                                Text("Edit Profile")
                            }
                        }
                    }

                    Button(
                        onClick = { showAlertDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder
                    ) {
                        Text("Log Out")
                    }
                }
            } else {
                // Edit Profile Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
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
                        value = name,
                        onValueChange = { name = it },
                        label = "Full Name"
                    )
                    ProfileTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email"
                    )
                    ProfileTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Phone Number",
                        enabled = false
                    )
                    ProfileTextField(
                        value = dateOfBirth,
                        onValueChange = { dateOfBirth = it },
                        label = "Date of Birth"
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
                        value = pincode,
                        onValueChange = { pincode = it },
                        label = "Pincode"
                    )
                    ProfileTextField(
                        value = country,
                        onValueChange = { country = it },
                        label = "Country"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
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
                                    di_address_id = addressId // You might want to get this from your user data
                                )
                                
                                viewModel.updateProfile(
                                    name = name,
                                    email = email,
                                    phone = phone,
                                    address = addressData,
                                    diAddressId = addressId
                                ) { success, message ->
                                    if (success) {
                                        onNavigateBack()
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
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            border = ButtonDefaults.outlinedButtonBorder
                        ) {
                            Text("Cancel")
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
                    onLogout = { showAlertDialog = false }
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(label: String, value: String) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Gray
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
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White) },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
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
                handleColor = Color.White,
                backgroundColor = Color.White
            )
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

//@OptIn(ExperimentalMaterial3Api::class)