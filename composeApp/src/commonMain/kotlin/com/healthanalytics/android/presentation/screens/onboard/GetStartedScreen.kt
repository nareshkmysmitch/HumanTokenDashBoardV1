package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.components.PrimaryButton
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppStrings
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.rounded_logo
import org.jetbrains.compose.resources.painterResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.healthanalytics.android.presentation.screens.onboard.viewmodel.OnboardViewModel
import com.healthanalytics.android.payment.RazorpayHandler

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GetStartedScreen(
    onGetStarted: () -> Unit, onLogin: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(AppColors.backgroundDark)
            .padding(Dimensions.screenPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Dimensions.size32dp))

            Box(
                contentAlignment = Alignment.Center, modifier = Modifier.padding(
                    end = Dimensions.size16dp, bottom = Dimensions.size16dp
                ).size(Dimensions.size120dp).shadow(
                    elevation = Dimensions.size6dp,
                    shape = CircleShape,
                    clip = false,
                    ambientColor = AppColors.backgroundDark,
                    spotColor = AppColors.spot
                ).clip(CircleShape).background(AppColors.backgroundDark)
                    .padding(paddingValues = PaddingValues(Dimensions.size6dp))
            ) {
                Image(
                    painter = painterResource(Res.drawable.rounded_logo),
                    contentDescription = AppStrings.appName,
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.size32dp))
            val gradient = Brush.linearGradient(
                colors = listOf(AppColors.White, AppColors.secondary, AppColors.White),
                tileMode = TileMode.Clamp
            )
            BasicText(
                text = "Welcome to\nHuman Token",
                style = TextStyle(
                    fontSize = FontSize.textSize32sp,
                    fontFamily = FontFamily.bold(),
                    brush = gradient,
                    textAlign = TextAlign.Center,
                    lineHeight = FontSize.textSize40sp
                ),
                modifier = Modifier.fillMaxWidth()
                    .graphicsLayer(alpha = 0.99f) // Needed for gradient text
            )
            Spacer(modifier = Modifier.height(Dimensions.size8dp))
            Text(
                text = "Your comprehensive health\nintelligence platform",
                fontSize = FontSize.textSize16sp,
                fontFamily = FontFamily.regular(),
                color = AppColors.textSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimensions.size32dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground),
                shape = RoundedCornerShape(Dimensions.cornerRadiusLarge),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(Dimensions.size20dp)) {
                    Text(
                        text = "What is Human Token?",
                        fontSize = FontSize.textSize20sp,
                        fontFamily = FontFamily.semiBold(),
                        color = AppColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(Dimensions.size8dp))
                    Text(
                        text = "Human Token is an advanced health analytics platform that transforms complex biomarker data into intuitive, interactive visualizations. We help you understand your health insights through comprehensive lab testing and AI-powered analysis.",
                        fontSize = FontSize.textSize16sp,
                        fontFamily = FontFamily.regular(),
                        color = AppColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(Dimensions.size12dp))
                    androidx.compose.foundation.layout.FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val certs = listOf(
                            "CLIA-certified labs", "FDA-approved tests", "HIPAA-compliant"
                        )
                        certs.forEachIndexed { index, cert ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (index <= 0) {
                                    androidx.compose.material3.Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Security,
                                        contentDescription = null,
                                        tint = AppColors.success,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }

                                if (index > 0) {
                                    Box(
                                        modifier = Modifier.size(Dimensions.size4dp).background(
                                            color = AppColors.success, shape = CircleShape
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }

                                Text(
                                    text = cert,
                                    fontSize = FontSize.textSize12sp,
                                    fontFamily = FontFamily.regular(),
                                    color = AppColors.success
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(Dimensions.size24dp))

            var showAll by remember { mutableStateOf(false) }
            val categories = listOf(
                BiomarkerCategory(
                    icon = "\uD83D\uDCA7", // Water drop
                    title = "Metabolic Health", color = AppColors.tertiary, items = listOf(
                        "Fasting Blood Glucose",
                        "Hemoglobin A1C (HbA1c)",
                        "Fasting Insulin",
                        "HOMA-IR",
                        "Average Blood Glucose"
                    )
                ), BiomarkerCategory(
                    icon = "\u2764\uFE0F", // Heart
                    title = "Heart Health", color = AppColors.secondary, items = listOf(
                        "Total Cholesterol",
                        "LDL, HDL, Triglycerides",
                        "VLDL, Non-HDL Cholesterol",
                        "ApoB, ApoA1, Lp(a)",
                        "ApoB/ApoA1 Ratio",
                        "LDL/HDL, TC/HDL Ratios",
                        "TGL/HDL, HDL/LDL Ratios",
                        "Small LDL"
                    )
                ), BiomarkerCategory(
                    icon = "\uD83D\uDD25", // Fire
                    title = "Inflammation", color = AppColors.error, items = listOf(
                        "hs-CRP", "Homocysteine", "Ferritin"
                    )
                ), BiomarkerCategory(
                    icon = "\u2697\uFE0F", // Test tube
                    title = "Hormone Health", color = AppColors.Pink, items = listOf(
                        "Total Testosterone",
                        "Free Testosterone",
                        "SHBG",
                        "DHEAS",
                        "Cortisol (morning)"
                    )
                ), BiomarkerCategory(
                    icon = "\uD83E\uDDEA", // Microbe
                    title = "Immune Health", color = AppColors.tertiary, items = listOf(
                        "Total WBC Count",
                        "Neutrophils %, Lymphocytes %",
                        "Monocytes %, Eosinophils %",
                        "Basophils %",
                        "Absolute Counts",
                        "Immature Granulocytes"
                    )
                ), BiomarkerCategory(
                    icon = "\uD83E\uDDEA", // Blood drop
                    title = "Blood Health", color = AppColors.error, items = listOf(
                        "Total RBC Count",
                        "Hemoglobin, Hematocrit",
                        "MCV, MCH, MCHC",
                        "RDW-CV, RDW-SD",
                        "NRBC, NRBC %",
                        "Platelet Count, MPV",
                        "PCT, PDW, P-LCR"
                    )
                ), BiomarkerCategory(
                    icon = "\uD83C\uDF31", // Leaf
                    title = "Nutrients & Vitamins", color = AppColors.success, items = listOf(
                        "Vitamin D, B12, Folate",
                        "Vitamin B6, B3, A, E",
                        "Vitamin B5, B7 (Biotin)",
                        "Zinc, Selenium",
                        "Iron, TIBC, UIBC",
                        "Transferrin Saturation",
                        "Calcium"
                    )
                ), BiomarkerCategory(
                    icon = "\u26A1", // Lightning
                    title = "Thyroid Health", color = AppColors.secondary, items = listOf(
                        "TSH", "Total T3, Free T3", "Total T4, Free T4"
                    )
                ), BiomarkerCategory(
                    icon = "\uD83D\uDC8E", // Gem (for liver)
                    title = "Liver Health",
                    color = AppColors.PurpleButtonBackground,
                    items = listOf(
                        "Total Bilirubin",
                        "Direct/Indirect Bilirubin",
                        "Serum Albumin/Globulin",
                        "SGOT, SGPT",
                        "ALP, GGTP",
                        "A/G Ratio"
                    )
                ), BiomarkerCategory(
                    icon = "\uD83C\uDF79", // Cocktail glass (for kidney)
                    title = "Kidney Health", color = AppColors.tertiary, items = listOf(
                        "Creatinine", "eGFR", "Urea", "Uric Acid"
                    )
                )
            )
            val shownCategories = if (showAll) categories else categories.take(2)
            Column(modifier = Modifier.animateContentSize()) {
                shownCategories.forEach { cat ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = Dimensions.size12dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground),
                        shape = RoundedCornerShape(Dimensions.cornerRadiusLarge)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(Dimensions.size16dp)
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp).background(
                                    color = AppColors.backgroundDark,
                                    shape = RoundedCornerShape(12.dp)
                                ), contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat.icon,
                                    fontSize = 24.sp,
                                    color = cat.color,
                                    fontFamily = FontFamily.bold()
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = cat.title,
                                    fontSize = FontSize.textSize18sp,
                                    fontFamily = FontFamily.medium(),
                                    color = AppColors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(Dimensions.size4dp))
                                cat.items.forEachIndexed { idx, item ->
                                    Text(
                                        text = "• $item",
                                        fontSize = FontSize.textSize14sp,
                                        fontFamily = FontFamily.regular(),
                                        color = AppColors.textSecondary,
                                        modifier = Modifier.padding(
                                            start = Dimensions.size8dp, bottom = Dimensions.size2dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.TextButton(
                        onClick = { showAll = !showAll },
                        content = {
                            Text(
                                text = if (showAll) "– Show Less" else "+ View All Biomarkers",
                                fontSize = FontSize.textSize16sp,
                                fontFamily = FontFamily.medium(),
                                color = AppColors.secondary
                            )
                        },
                        modifier = Modifier.padding(vertical = Dimensions.size8dp)
                    )
                }

                // Price Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground),
                    shape = RoundedCornerShape(Dimensions.cornerRadiusLarge),
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = Dimensions.size8dp, bottom = Dimensions.size16dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(Dimensions.size16dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Starting at",
                                fontSize = FontSize.textSize14sp,
                                fontFamily = FontFamily.regular(),
                                color = AppColors.textSecondary
                            )
                            Text(
                                text = "Results in 3–5 business days • At-home collection available",
                                fontSize = FontSize.textSize14sp,
                                fontFamily = FontFamily.regular(),
                                color = AppColors.textSecondary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "\u20B914,999",
                                fontSize = FontSize.textSize16sp,
                                fontFamily = FontFamily.regular(),
                                color = AppColors.textSecondary,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                            Text(
                                text = "\u20B99,999",
                                fontSize = FontSize.textSize28sp,
                                fontFamily = FontFamily.semiBold(),
                                color = AppColors.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.size32dp))
            PrimaryButton(
                isEnable = true, onclick = onGetStarted, buttonName = "Get Started"
            )
            Spacer(modifier = Modifier.height(Dimensions.size16dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? Continue to ",
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.regular(),
                    color = AppColors.textSecondary
                )
                Text(
                    text = "login",
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.secondary,
                    modifier = Modifier.clickable { onLogin() })
            }
        }
    }
}

// Helper data class for biomarker categories
data class BiomarkerCategory(
    val icon: String,
    val title: String,
    val color: androidx.compose.ui.graphics.Color,
    val items: List<String>
)

class GetStartedScreenNav(
    private val onboardViewModel: OnboardViewModel,
    private val razorpayHandler: RazorpayHandler,
    private val isLoggedIn: () -> Unit
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        GetStartedScreen(onGetStarted = {
            navigator.push(LoginScreenNav(onboardViewModel, razorpayHandler, isLoggedIn))
        }, onLogin = {
            navigator.push(LoginScreenNav(onboardViewModel, razorpayHandler, isLoggedIn))
        })
    }
} 