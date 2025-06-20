package com.healthanalytics.android.presentation.screens.consultation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.components.SecondaryButton
 import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.data.models.home.BloodData
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppTextStyles
import com.healthanalytics.android.presentation.theme.FontFamily
import org.koin.compose.koinInject

@Composable
fun ConsultationListScreen(
    viewModel: ConsultationViewModel = koinInject(),
    prefs: PreferencesViewModel = koinInject(),
    onNavigateToDetail: (BloodData?) -> Unit = {}
) {

    val preferencesState by prefs.uiState.collectAsState()
    val consultations by viewModel.consultUiState.collectAsState()

    LaunchedEffect(preferencesState.data) {
        preferencesState.data?.let { token ->
            prefs.saveAccessToken(token)
            viewModel.loadConsultations(token)
        }
    }

    Column(Modifier.fillMaxSize().background(AppColors.Black)) {
        Text(
            "Professional Services",
            fontSize = 16.sp,
            color = AppColors.White,
            fontFamily = FontFamily.bold(),
            modifier = Modifier.padding(14.dp)
        )
        Text(
            "Connect with healthcare professionals for personalized guidance based on your health data",
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = AppColors.textSecondary,
            fontFamily = FontFamily.medium(),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(16.dp))
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(consultations.products ?: emptyList()) {
                ConsultationCard(it)
            }
        }
    }
}

@Composable
fun ConsultationCard(product: Product?) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 4.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon based on type
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = AppColors.Pink,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    product?.name ?: "",
                    fontSize = 14.sp,
                    color = AppColors.White,
                    fontFamily = FontFamily.bold(),
                )
            }
            Spacer(Modifier.height(8.dp))

            val minutes = product?.meta_data?.duration ?: 0

            Text(
                "₹${product?.price} / ${("$minutes min")}",
                fontSize = 14.sp,
                color = AppColors.Pink,
                fontFamily = FontFamily.pilBold(),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                product?.description ?: "",
                fontSize = 12.sp,
                fontFamily = FontFamily.medium(),
                color = AppColors.textSecondary,
                lineHeight = 16.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Specialties",
                style = AppTextStyles.caption,
                color = AppColors.textSecondary,
                fontFamily = FontFamily.medium(),
            )
            Spacer(Modifier.height(4.dp))

            FlowRow(
                modifier = Modifier.padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                product?.tags?.forEach { specialty -> Chip(specialty) }
            }
            Spacer(Modifier.height(16.dp))
            SecondaryButton(
                isEnable = true,
                onclick = { /* TODO: Book consultation */ },
                buttonName = "Book Consultation"
            )
        }
    }
}

@Composable
fun Chip(text: String?) {
    Box(
        Modifier.background(AppColors.CardGrey, RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text.toString(),
            color = AppColors.White,
            fontSize = 12.sp,
            fontFamily = FontFamily.regular(),
        )
    }
} 