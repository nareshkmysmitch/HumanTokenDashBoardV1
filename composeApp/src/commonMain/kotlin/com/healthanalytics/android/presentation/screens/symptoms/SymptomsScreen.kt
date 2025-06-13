package com.healthanalytics.android.presentation.screens.symptoms

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.data.models.Symptom
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomsScreen(
    viewModel: SymptomsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val submitSuccess by viewModel.submitSuccess.collectAsState()

    LaunchedEffect(submitSuccess) {
        if (submitSuccess == true) {
            onNavigateHome()
            viewModel.clearSelectedSymptoms()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadSymptoms()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Report Symptoms",
                        color = AppColors.textPrimary,
                        fontSize = FontSize.textSize20sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.textPrimary
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.clearSelectedSymptoms()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = AppColors.textPrimary
                        )
                    ) {
                        Text(
                            "Cancel",
                            fontSize = FontSize.textSize16sp,
                            fontFamily = FontFamily.regular()
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.BlueBackground
                )
            )
        },
        containerColor = AppColors.backgroundDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.BlueBackground)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimensions.screenPadding)
            ) {

                Text(
                    text = "Select any symptoms you're currently experiencing",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(vertical = Dimensions.size8dp),
                    fontSize = FontSize.textSize20sp,
                    fontFamily = FontFamily.semiBold(),
                    color = AppColors.textPrimary
                )

                Text(
                    text = "${viewModel.getSelectedSymptomsCount()} symptoms selected",
                    fontSize = FontSize.textSize16sp,
                    fontFamily = FontFamily.regular(),
                    color = AppColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = Dimensions.size8dp)
                )

                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = AppColors.primary,
                            modifier = Modifier.size(Dimensions.size40dp)
                        )
                    }
                } else if (state.error != null) {
                    Text(
                        text = state.error ?: "An error occurred",
                        fontSize = FontSize.textSize16sp,
                        fontFamily = FontFamily.regular(),
                        color = AppColors.error,
                        modifier = Modifier.padding(Dimensions.screenPadding)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.size16dp)
                    ) {
                        state.symptoms.forEach { (category, symptoms) ->
                            item {
                                CategoryCard(
                                    category = category,
                                    symptoms = symptoms,
                                    selectedSymptoms = state.selectedSymptoms,
                                    onSymptomClick = { viewModel.toggleSymptom(it) }
                                )
                            }
                        }
                    }
                }

                // Bottom Bar with Submit Button
                if (viewModel.getSelectedSymptomsCount() > 0) {
                    Column(
                        modifier = Modifier.background(AppColors.Transparent),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { viewModel.submitSelectedSymptoms() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.BlueButton
                            ),
                            enabled = state.selectedSymptoms.isNotEmpty()
                        ) {
                            Text(
                                "Submit Symptoms",
                                fontSize = FontSize.textSize16sp,
                                fontFamily = FontFamily.bold()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: String,
    symptoms: List<Symptom>,
    selectedSymptoms: Set<String>,
    onSymptomClick: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppColors.BlueCardBackground),
        shape = RoundedCornerShape(Dimensions.cornerRadiusLarge),
        modifier = Modifier.fillMaxWidth().animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.size20dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MonitorHeart, // Replace with appropriate category icon
                    contentDescription = null,
                    tint = AppColors.BlueButton,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = category,
                    fontSize = FontSize.textSize20sp,
                    fontFamily = FontFamily.semiBold(),
                    color = AppColors.textPrimary
                )
            }

            Text(
                text = "${symptoms.size} symptoms available",
                fontSize = FontSize.textSize14sp,
                fontFamily = FontFamily.regular(),
                color = AppColors.textSecondary
            )

            symptoms.forEach { symptom ->
                Spacer(modifier = Modifier.height(8.dp))
                SymptomItem(
                    symptom = symptom,
                    isSelected = symptom.id in selectedSymptoms,
                    onClick = { symptom.id?.let { onSymptomClick(it) } }
                )
            }
        }
    }
}

@Composable
private fun SymptomItem(
    symptom: Symptom,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (isSelected) AppColors.BlueContainer.copy(alpha = 0.5f) else AppColors.BlueCardBackground,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isSelected) {
                AppColors.BlueStroke
            } else {
                Color.Gray.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = symptom.name ?: "",
                color = Color.White,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.Green,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
} 