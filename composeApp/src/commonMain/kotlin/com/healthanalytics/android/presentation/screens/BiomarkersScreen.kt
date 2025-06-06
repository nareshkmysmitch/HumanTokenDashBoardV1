package com.healthanalytics.android.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.data.models.Biomarker
import com.healthanalytics.android.presentation.screens.dashboard.StatusChip
import kotlinx.coroutines.launch

@Composable
fun BiomarkersScreen(
    token: String,
   // repository: HealthRepository = HealthRepository()
) {
    var biomarkers by remember { mutableStateOf<List<Biomarker>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(token) {
        scope.launch {
//            repository.getHealthData(token).fold(
//                onSuccess = { data ->
//                    biomarkers = data
//                    isLoading = false
//                },
//                onFailure = { error ->
//                    errorMessage = error.message ?: "Failed to load biomarkers"
//                    isLoading = false
//                }
//            )
        }
    }
    
    val categories = listOf("All") + biomarkers.map { it.category }.distinct()
    val filteredBiomarkers = if (selectedCategory == "All") {
        biomarkers
    } else {
        biomarkers.filter { it.category == selectedCategory }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Biomarkers",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (categories.size > 1) {
            LazyRow (
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        selected = selectedCategory == category
                    )
                }
            }
        }
        
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
            filteredBiomarkers.isEmpty() -> {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (selectedCategory == "All") 
                            "No biomarkers available. Connect your health data sources to see your biomarkers."
                        else 
                            "No biomarkers found in category: $selectedCategory",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredBiomarkers) { biomarker ->
                        DetailedBiomarkerCard(biomarker = biomarker)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailedBiomarkerCard(biomarker: Biomarker) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    fontWeight = FontWeight.Medium
                )
                StatusChip(status = biomarker.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (biomarker.value != null) {
                Text(
                    text = "Current Value: ${biomarker.value} ${biomarker.unit ?: ""}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            
            if (biomarker.referenceMin != null && biomarker.referenceMax != null) {
                Text(
                    text = "Reference Range: ${biomarker.referenceMin} - ${biomarker.referenceMax} ${biomarker.unit ?: ""}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            if (biomarker.category.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Category: ${biomarker.category}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            if (biomarker.description != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = biomarker.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
            
            if (biomarker.trend != "unknown") {
                Spacer(modifier = Modifier.height(8.dp))
                TrendIndicator(trend = biomarker.trend)
            }
        }
    }
}

@Composable
fun TrendIndicator(trend: String) {
    val (trendText, trendColor) = when (trend.lowercase()) {
        "improving" -> "↗ Improving" to MaterialTheme.colorScheme.primary
        "worsening" -> "↘ Worsening" to MaterialTheme.colorScheme.error
        "steady" -> "→ Steady" to MaterialTheme.colorScheme.onSurface
        else -> "- Unknown" to MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    }
    
    Text(
        text = "Trend: $trendText",
        fontSize = 14.sp,
        color = trendColor,
        fontWeight = FontWeight.Medium
    )
}