package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.components.DHToolBar
import com.healthanalytics.android.payment.RazorpayHandler
import com.healthanalytics.android.payment.RazorpayResultListener
import com.healthanalytics.android.payment.startRazorpayFlow
import com.healthanalytics.android.presentation.screens.onboard.viewmodel.OnboardViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppStrings
import com.healthanalytics.android.presentation.theme.AppTextStyles
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontSize
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

@Composable
fun PaymentScreenContainer(
    onboardViewModel: OnboardViewModel,
    onBackClick: () -> Unit,
    isPaymentCompleted: () -> Unit,
    razorpayHandler: RazorpayHandler,
) {
    PaymentScreen(
        onBackClick = onBackClick, onContinueClick = {
            val orderDetail = onboardViewModel.getGeneratedOrderDetail()

            if (orderDetail != null) {
                startRazorpayFlow(
                    amount = orderDetail.amount?.toInt() ?: 0,
                    currency = orderDetail.currency ?: "INR",
                    description = orderDetail.description ?: "",
                    orderId = orderDetail.payment_order_id ?: "",
                    razorpayHandler = razorpayHandler,
                    listener = object : RazorpayResultListener {
                        override fun onPaymentSuccess(paymentId: String?) {
                            if (orderDetail.payment_order_id != null) {
                                onboardViewModel.getPaymentStatus(orderDetail.payment_order_id)
                                isPaymentCompleted()
                            }
                        }

                        override fun onPaymentError(code: Int, message: String?) {

                        }
                    })
            }
        })
}

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

            DHToolBar(
                title = AppStrings.PAYMENT, onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(Dimensions.size50dp))

            // Main content
            Column(
                modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start
            ) {
                // Payment Confirmation Card
                NextStepCard(
                    icon = "💳",
                    title = "Payment Confirmation",
                    description = "Secure online payment will be processed after confirming your details.",
                    modifier = Modifier.padding(bottom = Dimensions.size24dp)
                )

                // At-Home Blood Draw Card
                NextStepCard(
                    icon = "🏷️",
                    title = "At-Home Blood Draw",
                    description = "A certified phlebotomist will visit your address at the scheduled time for sample collection.",
                    modifier = Modifier.padding(bottom = Dimensions.size24dp)
                )

                // Access Dashboard Card
                NextStepCard(
                    icon = "⚙️",
                    title = "Access your Dashboard",
                    description = "After processing your sample, you'll get access to your comprehensive health dashboard with detailed biomarker insights.",
                    modifier = Modifier.padding(bottom = Dimensions.size48dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue Button
            Button(
                onClick = onContinueClick,
                modifier = Modifier.fillMaxWidth().height(Dimensions.buttonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF665771), contentColor = AppColors.textPrimary
                ),
                shape = RoundedCornerShape(Dimensions.cornerRadiusLarge)
            ) {
                Text(
                    text = "Continue to Pay ₹4,999", style = AppTextStyles.buttonText.copy(
                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.size16dp))
        }
    }
}

@Composable
private fun NextStepCard(
    icon: String, title: String, description: String, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = Color(0xFF444463).copy(alpha = 0.2f)
        ), shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Dimensions.cardPadding),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier.size(48.dp).background(
                    color = AppColors.backgroundDark, shape = RoundedCornerShape(12.dp)
                ), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon, fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(Dimensions.size16dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = FontSize.textSize16sp,
                    fontFamily = com.healthanalytics.android.presentation.theme.FontFamily.semiBold(),
                    color = AppColors.textPrimary,
                    modifier = Modifier.padding(bottom = Dimensions.size8dp)
                )

                Text(
                    text = description,
                    fontSize = FontSize.textSize14sp,
                    fontFamily = com.healthanalytics.android.presentation.theme.FontFamily.medium(),
                    color = AppColors.textSecondary
                )
            }
        }
    }
}

class PaymentScreenNav(
    private val onboardViewModel: OnboardViewModel,
    private val razorpayHandler: RazorpayHandler,
    private val isLoggedIn: () -> Unit
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        PaymentScreenContainer(
            onboardViewModel = onboardViewModel,
            onBackClick = { navigator.pop() },
            isPaymentCompleted = { isLoggedIn() },
            razorpayHandler = razorpayHandler
        )
    }
}
