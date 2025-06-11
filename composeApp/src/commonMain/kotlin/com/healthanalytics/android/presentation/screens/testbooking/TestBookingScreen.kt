package com.healthanalytics.android.presentation.screens.testbooking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.api.Product
import org.koin.compose.koinInject

@Composable
fun TestBookingScreen(
    viewModel: TestBookingViewModel = koinInject(),
    onNavigateBack: () -> Unit,
    onNavigateToSchedule: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    BackHandler(enabled = true, onBack = { onNavigateBack() })
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Test Booking",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Schedule comprehensive health screenings and diagnostic tests",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.7f)
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Test Grid
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFF50057))
                }
            } else if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.availableTests) { test ->
                        TestCard(
                            test = test,
                            isSelected = test in state.selectedTests,
                            onSelect = { viewModel.toggleTestSelection(test) }
                        )
                    }
                }
            }
        }

        // Bottom Bar
        if (state.selectedTests.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Color(0xFF1A1A1A)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${state.selectedTests.size} tests selected",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
//                            text = "Total: ₹${String.format("%.2f", state.totalAmount)}",
                            text = "Total: ₹${state.totalAmount.toFloat()}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Button(
                        onClick = { onNavigateToSchedule() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF50057)
                        )
                    ) {
                        Text("Schedule Tests")
                    }
                }
            }
        }
    }
}

@Composable
private fun TestCard(
    test: Product,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1A1A1A),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = test.name ?: "",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 4.dp, end = 12.dp),
                        maxLines = 2,
                    )
                    if (!test.description.isNullOrEmpty()) {
                        Text(
                            text = test.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Text(
                        text = "₹${test.price ?: "0.00"}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    if (!test.vendor_name.isNullOrEmpty()) {
                        Text(
                            text = test.vendor_name,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                IconButton(
                    onClick = onSelect,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = if (isSelected) Color.Green.copy(alpha = 0.5f) else Color.White.copy(
                                alpha = 0.1f
                            ),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (!isSelected) Icons.Default.Add else Icons.Default.Check,
                        contentDescription = if (isSelected) "Remove test" else "Add test",
                        tint = Color.White
                    )
                }
            }
        }
    }
} 