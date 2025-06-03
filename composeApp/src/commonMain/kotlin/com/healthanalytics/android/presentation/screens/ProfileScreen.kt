package com.healthanalytics.android.presentation.screens

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.presentation.theme.AppColors
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onNavigateBack: () -> Unit) {

    var showAlertDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Your Profile",
                        color = AppColors.primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
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
                    title = "Edit Profile",
                    onClick = { /* Handle edit profile */ }
                )

                ProfileMenuItem(
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
                    title = "Change Plan",
                    onClick = { /* Handle change plan */ }
                )

                ProfileMenuItem(
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
                    color = AppColors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "Add an extra layer of security to your account",
                    color = AppColors.textPrimary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                ProfileMenuItem(
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
                    title = "View Active Sessions",
                    onClick = { /* Handle view sessions */ }
                )

                ProfileMenuItem(
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
                    title = "Delete Account",
                    textColor = Color(0xFFFF6B6B),
                    onClick = { /* Handle delete account */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAlertDialog = false
                        // Handle logout logic here
                    }
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAlertDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProfileScreen(onNavigateBack: () -> Unit) {
//    var showLogoutDialog by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(horizontal = 16.dp)
//            .verticalScroll(rememberScrollState()),
//        verticalArrangement = Arrangement.spacedBy(20.dp)
//    ) {
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Account Information Section
//            ProfileSection(
//                title = "Account Information",
//                subtitle = "Manage your personal information"
//            ) {
//                UserProfileCard(
//                    name = "John Doe",
//                    email = "john.doe@healthanalytics.com"
//                )
//
//                ProfileMenuItem(
//                    title = "Edit Profile",
//                    onClick = { /* Handle edit profile */ }
//                )
//
//                ProfileMenuItem(
//                    title = "Change Password",
//                    onClick = { /* Handle change password */ }
//                )
//            }
//
//            // Health Data Section
//            ProfileSection(
//                title = "Health Data",
//                subtitle = "Manage your health information"
//            ) {
//                ProfileMenuItem(
//                    title = "Data Export",
//                    onClick = { /* Handle data export */ }
//                )
//
//                ProfileMenuItem(
//                    title = "Privacy Settings",
//                    onClick = { /* Handle privacy settings */ }
//                )
//
//                ProfileMenuItem(
//                    title = "Data Sharing Preferences",
//                    onClick = { /* Handle data sharing */ }
//                )
//            }
//
//            // Subscription Section
//            ProfileSection(
//                title = "Subscription",
//                subtitle = "Manage your subscription plan"
//            ) {
//                SubscriptionCard(
//                    planName = "Premium Plan",
//                    nextBillingDate = "June 15, 2025",
//                    isActive = true
//                )
//
//                ProfileMenuItem(
//                    title = "Change Plan",
//                    onClick = { /* Handle change plan */ }
//                )
//
//                ProfileMenuItem(
//                    title = "Cancel Subscription",
//                    textColor = Color(0xFFFF6B6B),
//                    onClick = { /* Handle cancel subscription */ }
//                )
//            }
//
//            // Privacy & Security Section
//            ProfileSection(
//                title = "Privacy & Security",
//                subtitle = "Manage your security preferences"
//            ) {
//                Text(
//                    text = "Two-Factor Authentication",
//                    color = MaterialTheme.colorScheme.onSurface,
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Medium,
//                    modifier = Modifier.padding(bottom = 4.dp)
//                )
//
//                Text(
//                    text = "Add an extra layer of security to your account",
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    fontSize = 14.sp,
//                    modifier = Modifier.padding(bottom = 12.dp)
//                )
//
//                ProfileMenuItem(
//                    title = "Enable 2FA",
//                    onClick = { /* Handle enable 2FA */ }
//                )
//            }
//
//            // Sessions Section
//            ProfileSection(
//                title = "Sessions",
//                subtitle = "Manage your active sessions"
//            ) {
//                ProfileMenuItem(
//                    title = "View Active Sessions",
//                    onClick = { /* Handle view sessions */ }
//                )
//
//                ProfileMenuItem(
//                    title = "Log Out",
//                    onClick = { showLogoutDialog = true }
//                )
//            }
//
//            // Danger Zone Section
//            ProfileSection(
//                title = "Danger Zone",
//                subtitle = "Permanent account actions",
//                titleColor = Color(0xFFFF6B6B)
//            ) {
//                ProfileMenuItem(
//                    title = "Delete Account",
//                    textColor = Color(0xFFFF6B6B),
//                    onClick = { /* Handle delete account */ }
//                )
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//        }
//
//    // Logout confirmation dialog
//    if (showLogoutDialog) {
//        AlertDialog(
//            onDismissRequest = { showLogoutDialog = false },
//            title = { Text("Confirm Logout") },
//            text = { Text("Are you sure you want to log out?") },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        showLogoutDialog = false
//                        // Handle logout logic here
//                    }
//                ) {
//                    Text("Logout")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showLogoutDialog = false }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//}

@Composable
private fun ProfileSection(
    title: String,
    subtitle: String,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_calendar_icon),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = email,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            if (isActive) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Active",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Next billing date: $nextBillingDate",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ProfileMenuItem(
    title: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
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
            painter = painterResource(Res.drawable.ic_calendar_icon),
            contentDescription = title,
            modifier = Modifier.size(20.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = textColor,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Image(
            painter = painterResource(Res.drawable.ic_calendar_icon),
            contentDescription = "Navigate",
            modifier = Modifier.size(16.dp),
        )
    }
}
