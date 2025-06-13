package com.healthanalytics.android.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.data.models.Biomarker
import com.healthanalytics.android.presentation.components.AppCard
import com.healthanalytics.android.presentation.theme.AppColors
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    token: String,
//    repository: HealthRepository = HealthRepository()
) {
    var biomarkers by remember { mutableStateOf<List<Biomarker>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(token) {
        scope.launch {
//            repository.getHealthData(token).fold(
//                onSuccess = { data ->
//                    biomarkers = data
//                    isLoading = false
//                },
//                onFailure = { error ->
//                    errorMessage = error.message ?: "Failed to load health data"
//                    isLoading = false
//                }
//            )
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Health Dashboard",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage.isNotEmpty() -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            biomarkers.isEmpty() -> {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No health data available. Please sync your data from the settings.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(biomarkers) { biomarker ->
                        BiomarkerCard(biomarker = biomarker)
                    }
                }
            }
        }
    }
}

@Composable
fun BiomarkerCard(biomarker: Biomarker) {
    AppCard(
        modifier = Modifier.fillMaxWidth().background(AppColors.white),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = biomarker.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.DarkPurple
                )
                
                StatusChip(status = biomarker.status)
            }
            
            if (biomarker.value != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${biomarker.value} ${biomarker.unit ?: ""}",
                    fontSize = 16.sp,
                    color = AppColors.DarkPurple
                )
            }
            
            if (biomarker.description != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = biomarker.description,
                    fontSize = 14.sp,
                    color = AppColors.DarkPurple.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val backgroundColor = when (status.lowercase()) {
        "optimal" -> MaterialTheme.colorScheme.primary
        "suboptimal" -> MaterialTheme.colorScheme.tertiary
        "critical" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }
    
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}