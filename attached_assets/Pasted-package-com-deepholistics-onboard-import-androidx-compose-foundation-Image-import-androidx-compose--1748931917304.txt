package com.deepholistics.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deepholistics.onboard.helper.ScreenBackground
import com.deepholistics.onboard.viewmodel.ProfileViewModel
import com.deepholistics.res.AppColors
import com.deepholistics.res.AppDimens.textSizeXl
import com.deepholistics.ui.ShowAlertDialog
import humantokenv2.composeapp.generated.resources.Res
import humantokenv2.composeapp.generated.resources.ic_payment
import humantokenv2.composeapp.generated.resources.ic_summary_calendar
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onNavigateBack: () -> Unit, profileViewModel: ProfileViewModel) {

    var showAlertDialog by remember { mutableStateOf(false) }
    ScreenBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Your Profile",
                            color = AppColors.TextPrimary,
                            fontSize = textSizeXl,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_summary_calendar),
                                contentDescription = "back arrow",
                                tint = AppColors.TextGrey
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Account Information Section
                ProfileSection(
                    title = "Account Information",
                    subtitle = "Manage your personal information"
                ) {
                    UserProfileCard(
                        name = "John Doe",
                        email = "john.doe@example.com"
                    )

                    ProfileMenuItem(
                        icon = painterResource(Res.drawable.ic_payment),
                        title = "Edit Profile",
                        onClick = { /* Handle edit profile */ }
                    )

                    ProfileMenuItem(
                        icon = painterResource(Res.drawable.ic_payment),
                        title = "Change Password",
                        onClick = { /* Handle change password */ }
                    )
                }

                // Subscription Section
                ProfileSection(
                    title = "Subscription",
                    subtitle = "Manage your subscription plan"
                ) {
                    SubscriptionCard(
                        planName = "Premium Plan",
                        nextBillingDate = "June 15, 2025",
                        isActive = true
                    )

                    ProfileMenuItem(
                        icon = painterResource(Res.drawable.ic_payment),
                        title = "Change Plan",
                        onClick = { /* Handle change plan */ }
                    )

                    ProfileMenuItem(
                        icon = painterResource(Res.drawable.ic_payment),
                        title = "Cancel Subscription",
                        textColor = Color(0xFFFF6B6B),
                        onClick = { /* Handle cancel subscription */ }
                    )
                }

                // Privacy & Security Section
                ProfileSection(
                    title = "Privacy & Security",
                    subtitle = "Manage your security preferences"
                ) {
                    Text(
                        text = "Two-Factor Authentication",
                        color = AppColors.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = "Add an extra layer of security to your account",
                        color = AppColors.TextPrimary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    ProfileMenuItem(
                        icon = painterResource(Res.drawable.ic_payment),
                        title = "Enable 2FA",
                        onClick = { /* Handle enable 2FA */ }
                    )
                }

                // Sessions Section
                ProfileSection(
                    title = "Sessions",
                    subtitle = "Manage your active sessions"
                ) {
                    ProfileMenuItem(
                        icon = painterResource(Res.drawable.ic_payment),
                        title = "View Active Sessions",
                        onClick = { /* Handle view sessions */ }
                    )

                    ProfileMenuItem(
                        icon = painterResource(Res.drawable.ic_payment),
                        title = "Log Out",
                        onClick = { /* Handle logout */
                            showAlertDialog = true
                        }
                    )
                }

                // Danger Zone Section
                ProfileSection(
                    title = "Danger Zone",
                    subtitle = "Permanent account actions",
                    titleColor = Color(0xFFFF6B6B)
                ) {
                    ProfileMenuItem(
                        icon = painterResource(Res.drawable.ic_payment),
                        title = "Delete Account",
                        textColor = Color(0xFFFF6B6B),
                        onClick = { /* Handle delete account */ }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        if (showAlertDialog) {
            ShowAlertDialog(
                modifier = Modifier,
                title = "Log out",
                message = "You will be logged out of your Deep Holistics account. However this doesn\\’t affect your logged data. Do you want to still logout?",
                onDismiss = {
                    showAlertDialog = false
                },
                onLogout = { profileViewModel.userLogOut() })
        }
    }
    val logOutApi by profileViewModel.apiState.collectAsStateWithLifecycle()
    if (logOutApi?.isSuccessful == true) {
        showAlertDialog = false
    }
}

@Composable
private fun ProfileSection(
    title: String,
    subtitle: String,
    titleColor: Color = AppColors.TextPrimary,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            color = titleColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = subtitle,
            color = AppColors.TextPrimary,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A2A2A)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun UserProfileCard(
    name: String,
    email: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF8B5CF6)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_payment),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = name,
                color = AppColors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = email,
                color = AppColors.TextPrimary,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun SubscriptionCard(
    planName: String,
    nextBillingDate: String,
    isActive: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = planName,
                color = AppColors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            if (isActive) {
                Surface(
                    color = Color(0xFF8B5CF6),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Active",
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Next billing date: $nextBillingDate",
            color = AppColors.TextPrimary,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: Painter,
    title: String,
    textColor: Color = AppColors.TextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = icon,
            contentDescription = title,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = textColor,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Image(
            painter = icon,
            contentDescription = "Navigate",
            modifier = Modifier.size(16.dp)
        )
    }
}
