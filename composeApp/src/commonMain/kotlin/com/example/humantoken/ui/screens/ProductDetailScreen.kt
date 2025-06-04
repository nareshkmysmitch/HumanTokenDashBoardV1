package com.example.humantoken.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.presentation.theme.AppColors
import com.seiko.imageloader.rememberImagePainter
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(product: Product, onNavigateBack: () -> Unit) {
    println("product -> $product")
    val scrollState = rememberScrollState()
    var quantity by remember { mutableStateOf(1) }
    var selectedTab by remember { mutableStateOf(3) } // Reviews tab selected by default

    BackHandler(enabled = true, onBack = onNavigateBack)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    product.name?.let {
                        Text(
                            text = it,
                            color = AppColors.primary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_calendar_icon),
                            contentDescription = "back arrow",
                            tint = AppColors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            // Product Image
            if (product.img_urls?.isNotEmpty() == true && product.img_urls.firstOrNull() != null) {
                product.img_urls.firstOrNull()?.let {
                    Image(
                        painter = rememberImagePainter(it),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Product Info Section
            product.name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            product.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val price = if (product.price != null) {
                "₹${product.price}"
            } else {
                "--"
            }
            Text(
                text = price,
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
                val rating = if (product.rating != null) {
                    product.rating.toString()
                } else {
                    "0"
                }
                val nRating = if (product.n_rating != null) {
                    product.n_rating.toString()
                } else {
                    "0"
                }
                val ratingText = " ($rating) · $nRating reviews"
                Text(
                    text = ratingText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                product.tags?.forEach { tag ->
                    SuggestionChip(
                        onClick = { },
                        label = { tag?.replaceFirstChar { it.uppercase() }?.let { Text(it) } }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quantity Selector
            Row(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color(0xFF1C1B1F),
                        shape = RoundedCornerShape(
                            12.dp
                        )
                    )
                    .clip(RoundedCornerShape(12.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                        .clickable(onClick = { if (quantity > 1) quantity-- })
                        .padding(8.dp)
                ) {
                    Text(
                        text = "−",
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                        .clickable(onClick = { product.stock?.let { if (quantity < it) quantity++ } })
                        .padding(8.dp)
                ) {
                    Text(
                        text = "+",
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
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
            product.stock?.let {
                if (it > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.Green
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Only ${product.stock} left")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tabs Section
            Column(modifier = Modifier.fillMaxWidth()) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 16.dp,
//                    containerColor = Color(0xFF1C1B1F),
//                    contentColor = Color.Black,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            height = 2.dp,
                            color = Color.Black
                        )
                    }
                ) {
                    listOf(
                        "Product Details",
                        "Ingredients",
                        "Directions",
                        "Reviews"
                    ).forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (selectedTab == index) {
                                            FontWeight.SemiBold
                                        } else {
                                            FontWeight.Normal
                                        }
                                    ),
                                    color = if (selectedTab == index) {
                                        Color.Black
                                    } else {
                                        Color.Black.copy(alpha = 0.6f)
                                    }
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tab Content
                when (selectedTab) {
                    0 -> {
                        // Product Details Tab
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Product Information",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            ProductInfoRow("SKU:", product.sku ?: "")
                            ProductInfoRow("Category:", product.category?.firstOrNull() ?: "")
                            ProductInfoRow("Stock:", "${product.stock ?: 0} available")
                        }
                    }
                    1 -> {
                        // Ingredients Tab
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Product ingredients information will be available soon.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black.copy(alpha = 0.8f)
                            )
                        }
                    }
                    2 -> {
                        // Directions Tab
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Usage directions will be available soon.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black.copy(alpha = 0.8f)
                            )
                        }
                    }
                    3 -> {
                        // Reviews Tab
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Customer Reviews",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                                OutlinedButton(
                                    onClick = { },
                                    border = BorderStroke(1.dp, Color.Black),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Write a Review",
                                        color = Color.Black
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Default.StarBorder,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    text = " Based on 0 reviews",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black.copy(alpha = 0.8f)
                                )
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Message,
                                        contentDescription = null,
                                        tint = Color.Black.copy(alpha = 0.6f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = "No reviews yet. Be the first to review this product.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Black.copy(alpha = 0.6f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
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

@Composable
private fun ProductInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
} 