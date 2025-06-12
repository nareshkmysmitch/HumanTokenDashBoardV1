package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.presentation.components.AppCard
import com.healthanalytics.android.presentation.components.FilledAppButton
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppTextStyles
import com.healthanalytics.android.presentation.theme.Dimensions
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun PaymentScreen(
    onBackClick: () -> Unit = {}, onContinueClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize().background(AppColors.backgroundDark)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(Dimensions.cardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top section with back button and logo
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Dimensions.spacingMedium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                TextButton(
                    onClick = onBackClick, colors = ButtonDefaults.textButtonColors(
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

            Spacer(modifier = Modifier.height(Dimensions.spacingXXLarge))

            // Main content
            Column(
                modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start
            ) {
                // Title
                Text(
                    text = "Next Steps",
                    style = AppTextStyles.headingLarge.copy(
                        fontSize = 32.sp, fontWeight = FontWeight.Bold
                    ),
                    color = AppColors.textPrimary,
                    modifier = Modifier.padding(bottom = Dimensions.spacingXXLarge)
                )

                // Payment Confirmation Card
                NextStepCard(
                    icon = "💳",
                    title = "Payment Confirmation",
                    description = "Secure online payment will be processed after confirming your details.",
                    modifier = Modifier.padding(bottom = Dimensions.spacingLarge)
                )

                // At-Home Blood Draw Card
                NextStepCard(
                    icon = "🏷️",
                    title = "At-Home Blood Draw",
                    description = "A certified phlebotomist will visit your address at the scheduled time for sample collection.",
                    modifier = Modifier.padding(bottom = Dimensions.spacingLarge)
                )

                // Access Dashboard Card
                NextStepCard(
                    icon = "⚙️",
                    title = "Access your Dashboard",
                    description = "After processing your sample, you'll get access to your comprehensive health dashboard with detailed biomarker insights.",
                    modifier = Modifier.padding(bottom = Dimensions.spacingXXLarge)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            FilledAppButton(
                onClick = onContinueClick,
                modifier = Modifier.fillMaxWidth().height(Dimensions.buttonHeight)
            ) {
                Text(
                    text = "Continue to Pay ₹4,999",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold
                    ),
                    color = AppColors.White
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))
        }
    }
}

@Composable
private fun NextStepCard(
    icon: String, title: String, description: String, modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(
                    color = AppColors.DarkPurple.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon, fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title, style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp, fontWeight = FontWeight.SemiBold
                    ), color = AppColors.DarkPurple, modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = description, style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp, lineHeight = 20.sp
                    ), color = AppColors.DarkPurple.copy(alpha = 0.7f)
                )
            }
        }
    }
}
