package com.healthanalytics.android.presentation.health

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.data.api.HealthMetric
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HealthDataScreen(
    viewModel: HealthDataViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredMetrics = viewModel.getFilteredMetrics()
    val dummyAccessToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiQkVUQV8wMzcyNGE3Yi0wZjA5LTQ1ODYtYmYyMy1hYTQ1NzA5NzVhYjciLCJzZXNzaW9uX2lkIjoiOGM0MmFlMzAtZmVkMC00NTNjLWIwMzEtYmQyYmFjNzQ5N2Y0IiwidXNlcl9pbnRfaWQiOiI0NzUiLCJpYXQiOjE3NDg0OTkwODgsImV4cCI6MTc0OTEwMzg4OH0.jbbY5r1g-SSzYvII3EkcfzFfdDF2OHZwifx9DFuH20E"

    LaunchedEffect(Unit) {
        viewModel.loadHealthMetrics(dummyAccessToken)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Text(
            text = "Health Data",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        // Last Updated
        Text(
            text = "Last updated: ${formatDate(uiState.lastUpdated)}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            val filters = listOf("Normal", "Low", "High", "Optimal")
            items(filters) { filter ->
                FilterChip(
                    selected = uiState.selectedFilter == filter,
                    onClick = { viewModel.updateFilter(if (uiState.selectedFilter == filter) null else filter) },
                    label = { Text(filter) }
                )
            }
        }

        // Metrics List
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredMetrics) { metric ->
                    MetricCard(metric = metric)
                }
            }
        }
    }
}

@Composable
fun MetricCard(metric: HealthMetric) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = metric.displayName,
                    style = MaterialTheme.typography.titleMedium
                )
                StatusChip(status = metric.displayRating)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${metric.value} ${metric.unit}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Range: ${metric.range}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = metric.displayDescription,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Last updated: ${formatDate(metric.updatedAt)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "normal" -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        "low" -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        "high" -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        "optimal" -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}

private fun formatDate(dateString: String): String {
    return dateString
    /* return try {
         val instant = Instant.parse(dateString)
         val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
         "${localDateTime.date} ${localDateTime.hour}:${localDateTime.minute}"
     } catch (e: Exception) {
         dateString
     }*/
} 