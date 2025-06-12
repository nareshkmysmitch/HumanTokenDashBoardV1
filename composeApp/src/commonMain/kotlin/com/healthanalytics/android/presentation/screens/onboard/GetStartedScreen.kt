package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.components.PrimaryButton
import com.healthanalytics.android.presentation.theme.*
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.rounded_logo
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable

@Composable
fun GetStartedScreen(
    onGetStarted: () -> Unit,
    onLogin: () -> Unit,
    onViewAllBiomarkers: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.backgroundDark)
            .padding(Dimensions.screenPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Dimensions.size32dp))
            Image(
                painter = painterResource(Res.drawable.rounded_logo),
                contentDescription = AppStrings.appName,
                modifier = Modifier.size(Dimensions.size80dp)
            )
            Spacer(modifier = Modifier.height(Dimensions.size32dp))
            Text(
                text = "Welcome to Human Token",
                style = AppTextStyles.headingLarge,
                color = AppColors.textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimensions.size8dp))
            Text(
                text = "Your comprehensive health intelligence platform",
                style = AppTextStyles.bodyLarge,
                color = AppColors.textSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimensions.size32dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.PurpleCardBackground),
                shape = RoundedCornerShape(Dimensions.cornerRadiusLarge),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(Dimensions.size20dp)) {
                    Text(
                        text = "What is Human Token?",
                        style = AppTextStyles.headingSmall,
                        color = AppColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(Dimensions.size8dp))
                    Text(
                        text = "Human Token is an advanced health analytics platform that transforms complex biomarker data into intuitive, interactive visualizations. We help you understand your health insights through comprehensive lab testing and AI-powered analysis.",
                        style = AppTextStyles.bodyMedium,
                        color = AppColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(Dimensions.size12dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = "CLIA-certified labs",
                            style = AppTextStyles.caption,
                            color = AppColors.success
                        )
                        Text(
                            text = "FDA-approved tests",
                            style = AppTextStyles.caption,
                            color = AppColors.success
                        )
                        Text(
                            text = "HIPAA-compliant",
                            style = AppTextStyles.caption,
                            color = AppColors.success
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(Dimensions.size24dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.PurpleCardBackground),
                shape = RoundedCornerShape(Dimensions.cornerRadiusLarge),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(Dimensions.size20dp)) {
                    Text(
                        text = "HT Ultimate Panel - 100+ Biomarkers",
                        style = AppTextStyles.headingSmall,
                        color = AppColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(Dimensions.size8dp))
                    Text(
                        text = "Our comprehensive panel tests over 100 advanced biomarkers across multiple health domains:",
                        style = AppTextStyles.bodyMedium,
                        color = AppColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(Dimensions.size16dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Metabolic Health", style = AppTextStyles.bodyLarge, color = AppColors.textPrimary)
                            Text("• Glucose, HbA1c, Insulin", style = AppTextStyles.bodySmall, color = AppColors.textSecondary)
                            Text("• HOMA-IR", style = AppTextStyles.bodySmall, color = AppColors.textSecondary)
                            Spacer(modifier = Modifier.height(Dimensions.size8dp))
                            Text("Hormone Health", style = AppTextStyles.bodyLarge, color = AppColors.textPrimary)
                            Text("• Testosterone, SHBG", style = AppTextStyles.bodySmall, color = AppColors.textSecondary)
                            Text("• DHEAS, Cortisol", style = AppTextStyles.bodySmall, color = AppColors.textSecondary)
                        }
                        Spacer(modifier = Modifier.width(Dimensions.size16dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Heart Health", style = AppTextStyles.bodyLarge, color = AppColors.textPrimary)
                            Text("• Complete Lipid Panel", style = AppTextStyles.bodySmall, color = AppColors.textSecondary)
                            Text("• Advanced Ratios & Markers", style = AppTextStyles.bodySmall, color = AppColors.textSecondary)
                            Spacer(modifier = Modifier.height(Dimensions.size8dp))
                            Text("Nutrients & Vitamins", style = AppTextStyles.bodyLarge, color = AppColors.textPrimary)
                            Text("• Vitamins D, B12, Folate", style = AppTextStyles.bodySmall, color = AppColors.textSecondary)
                            Text("• Essential Minerals", style = AppTextStyles.bodySmall, color = AppColors.textSecondary)
                        }
                    }
                    Spacer(modifier = Modifier.height(Dimensions.size16dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "+ View All Biomarkers",
                            style = AppTextStyles.bodyMedium,
                            color = AppColors.secondary,
                            modifier = Modifier.clickable { onViewAllBiomarkers() }
                        )
                    }
                    Spacer(modifier = Modifier.height(Dimensions.size16dp))
                    Text(
                        text = "Starting at",
                        style = AppTextStyles.caption,
                        color = AppColors.textSecondary
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "₹14,999",
                            style = AppTextStyles.bodyMedium,
                            color = AppColors.textSecondary,
                            modifier = Modifier.padding(end = Dimensions.size8dp).weight(1f)
                        )
                        Text(
                            text = "₹9,999",
                            style = AppTextStyles.headingMedium,
                            color = AppColors.success
                        )
                    }
                    Text(
                        text = "Results in 3-5 business days • At-home collection available",
                        style = AppTextStyles.caption,
                        color = AppColors.textSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(Dimensions.size32dp))
            PrimaryButton(
                isEnable = true,
                onclick = onGetStarted,
                buttonName = "Get Started"
            )
            Spacer(modifier = Modifier.height(Dimensions.size16dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? Continue to ",
                    style = AppTextStyles.bodySmall,
                    color = AppColors.textSecondary
                )
                Text(
                    text = "login",
                    style = AppTextStyles.bodySmall,
                    color = AppColors.secondary,
                    modifier = Modifier.clickable { onLogin() }
                )
            }
        }
    }
} 