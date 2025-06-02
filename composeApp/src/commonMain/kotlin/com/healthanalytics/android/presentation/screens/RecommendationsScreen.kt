package com.healthanalytics.android.presentation.screens

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

@Composable
fun RecommendationsScreen() {
    // Mock recommendations data - in real app, this would come from API
    val recommendations = listOf(
        Recommendation(
            id = "1",
            title = "Increase Vitamin D Intake",
            description = "Based on your recent lab results, consider increasing vitamin D supplementation.",
            priority = "High",
            category = "Nutrition"
        ),
        Recommendation(
            id = "2",
            title = "Regular Cardio Exercise",
            description = "Your cardiovascular markers suggest 30 minutes of cardio 3-4 times per week.",
            priority = "Medium",
            category = "Exercise"
        ),
        Recommendation(
            id = "3",
            title = "Sleep Quality Improvement",
            description = "Consider establishing a consistent sleep schedule to improve recovery metrics.",
            priority = "Medium",
            category = "Lifestyle"
        )
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Recommendations",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (recommendations.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No recommendations available. Complete your health assessment to get personalized recommendations.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recommendations) { recommendation ->
                    RecommendationCard(recommendation = recommendation)
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(recommendation: Recommendation) {
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
                    text = recommendation.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                
                PriorityChip(priority = recommendation.priority)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            CategoryChip(category = recommendation.category)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = recommendation.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun PriorityChip(priority: String) {
    val backgroundColor = when (priority.lowercase()) {
        "high" -> MaterialTheme.colorScheme.error
        "medium" -> MaterialTheme.colorScheme.tertiary
        "low" -> MaterialTheme.colorScheme.outline
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
            text = priority.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun CategoryChip(category: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = category,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

data class Recommendation(
    val id: String,
    val title: String,
    val description: String,
    val priority: String,
    val category: String
)