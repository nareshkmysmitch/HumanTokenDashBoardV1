package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.payment.RazorpayHandler
import com.healthanalytics.android.payment.RazorpayResultListener
import com.healthanalytics.android.payment.startRazorpayFlow
import com.healthanalytics.android.presentation.screens.onboard.viewmodel.OnboardViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppTextStyles
import com.healthanalytics.android.presentation.theme.Dimensions
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun PaymentScreenContainer(
    onboardViewModel: OnboardViewModel,
    onBackClick: () -> Unit,
    isPaymentCompleted: () -> Unit,
    razorpayHandler: RazorpayHandler,
) {

    PaymentScreen(
        onBackClick = onBackClick,
        onContinueClick = {
            val orderDetail = onboardViewModel.getGeneratedOrderDetail()

            if (orderDetail !=null){
                startRazorpayFlow(
                    amount = orderDetail.amount?.toInt() ?: 0,
                    currency = orderDetail.currency ?: "INR",
                    description = orderDetail.description ?: "" ,
                    orderId = orderDetail.payment_order_id ?: "",
                    razorpayHandler = razorpayHandler,
                    listener = object : RazorpayResultListener {
                        override fun onPaymentSuccess(paymentId: String?) {
                            if (orderDetail.payment_order_id != null){
                                onboardViewModel.getPaymentStatus(orderDetail.payment_order_id)
                                isPaymentCompleted()
                            }
                        }

                        override fun onPaymentError(code: Int, message: String?) {

                        }
                    }
                )
            }
        }
    )
}

@Composable
fun PaymentScreen(
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {
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
            // Top section with back button and logo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimensions.spacingMedium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                TextButton(
                    onClick = onBackClick,
                    colors = ButtonDefaults.textButtonColors(
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
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                // Title
                Text(
                    text = "Next Steps",
                    style = AppTextStyles.headingLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
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

            // Continue Button
            Button(
                onClick = onContinueClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.buttonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B5B95),
                    contentColor = AppColors.textPrimary
                ),
                shape = RoundedCornerShape(Dimensions.cornerRadiusLarge)
            ) {
                Text(
                    text = "Continue to Pay ₹4,999",
                    style = AppTextStyles.buttonText.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))
        }
    }
}

@Composable
private fun NextStepCard(
    icon: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.cardPadding),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFF3A3A3A),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(Dimensions.spacingMedium))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = AppTextStyles.headingMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = AppColors.textPrimary,
                    modifier = Modifier.padding(bottom = Dimensions.spacingSmall)
                )

                Text(
                    text = description,
                    style = AppTextStyles.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    color = AppColors.textSecondary
                )
            }
        }
    }
}
