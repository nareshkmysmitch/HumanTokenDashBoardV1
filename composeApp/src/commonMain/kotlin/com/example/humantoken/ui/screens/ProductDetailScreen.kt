package com.example.humantoken.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.api.Product
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProductDetailScreen(product: Product, onNavigateBack: () -> Unit) {
    val scrollState = rememberScrollState()
    var quantity by remember { mutableStateOf(1) }
    var selectedTab by remember { mutableStateOf(3) } // Reviews tab selected by default

    BackHandler(enabled = true, onBack = onNavigateBack)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Product Image
            Image(
                painter = painterResource(Res.drawable.ic_calendar_icon),
                contentDescription = "Yellow watering can",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Product Info Section
            Text(
                text = "Yellow watering can",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Vintage vibrant watering can",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "₹40.99",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Rating Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = " (0.0) · 0 reviews",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Bathroom", "Seasonal", "Living-Room").forEach { tag ->
                    SuggestionChip(
                        onClick = { },
                        label = { Text(tag) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quantity Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (quantity > 1) quantity-- }
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease quantity")
                }
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(
                    onClick = { quantity++ }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase quantity")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add to Cart Button
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63)
                )
            ) {
                Text(
                    text = "Add to Cart",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Shipping Info
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoRow(Icons.Default.LocalShipping, "Free shipping over $50")
                InfoRow(Icons.Default.Verified, "Quality guaranteed")
                InfoRow(Icons.Default.Assignment, "60-day return policy")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stock Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.Green
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Only 10 left")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp
            ) {
                listOf("Product Details", "Ingredients", "Directions", "Reviews").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reviews Section
            Text(
                text = "Customer Reviews",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = "Based on 0 reviews",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Write a Review")
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
} 