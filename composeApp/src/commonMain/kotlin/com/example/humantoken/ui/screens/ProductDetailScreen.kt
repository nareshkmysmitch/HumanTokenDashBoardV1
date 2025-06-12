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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.healthanalytics.android.presentation.screens.marketplace.CartActionState
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.screens.marketplace.ProductDetailsState
import com.healthanalytics.android.presentation.theme.AppColors
import com.seiko.imageloader.rememberImagePainter
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product, onNavigateBack: () -> Unit, viewModel: MarketPlaceViewModel,
) {
    val scrollState = rememberScrollState()
    var quantity by remember { mutableStateOf(1) }
    var selectedTab by remember { mutableStateOf(1) }

    // Collect states
    val cartActionState by viewModel.cartActionState.collectAsState()
    val productDetailsState by viewModel.productDetailsState.collectAsState()
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // Fetch product details when the screen is first shown
    LaunchedEffect(Unit) {
        product.product_id?.let { viewModel.getProductDetails(it) }
    }

    // Handle cart action state changes
    LaunchedEffect(cartActionState) {
        when (cartActionState) {
            is CartActionState.Success -> {
                snackbarMessage = (cartActionState as CartActionState.Success).message
            }

            is CartActionState.Error -> {
                snackbarMessage = (cartActionState as CartActionState.Error).message
            }

            else -> {}
        }
    }

    BackHandler(enabled = true, onBack = onNavigateBack)
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.AppBackgroundColor,
                    navigationIconContentColor = AppColors.Black,
                    titleContentColor = AppColors.Black
                ),
                title = {
                    when (productDetailsState) {
                        is ProductDetailsState.Success -> {
                            (productDetailsState as ProductDetailsState.Success).product.name?.let {
                                Text(
                                    text = it,
                                    color = AppColors.Black,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        else -> {
                            product.name?.let {
                                Text(
                                    text = it,
                                    color = AppColors.Black,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back arrow",
                            tint = AppColors.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        when (productDetailsState) {
            is ProductDetailsState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ProductDetailsState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (productDetailsState as ProductDetailsState.Error).message,
                        color = Color.Red
                    )
                }
            }

            is ProductDetailsState.Success -> {
                val currentProduct = (productDetailsState as ProductDetailsState.Success).product
                Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                    Spacer(modifier = Modifier.height(50.dp))
                    // Product Image
                    if (currentProduct.img_urls?.isNotEmpty() == true && currentProduct.img_urls.firstOrNull() != null) {
                        currentProduct.img_urls.firstOrNull()?.let {
                            Image(
                                painter = rememberImagePainter(it),
                                contentDescription = currentProduct.name,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth().height(300.dp)
                                    .padding(top = 16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Product Info Section
                    currentProduct.name?.let {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = it,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    currentProduct.description?.let {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val price = if (currentProduct.price != null) {
                        "₹${currentProduct.price}"
                    } else {
                        "--"
                    }
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = price,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rating Section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        repeat(5) {
                            Icon(
                                imageVector = Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        val rating = if (currentProduct.rating != null) {
                            currentProduct.rating.toString()
                        } else {
                            "0"
                        }
                        val nRating = if (currentProduct.n_rating != null) {
                            currentProduct.n_rating.toString()
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
                        currentProduct.tags?.forEach { tag ->
                            SuggestionChip(onClick = { }, label = {
                                tag?.replaceFirstChar { it.uppercase() }?.let { Text(it) }
                            })
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Quantity Selector
                    Row(
                        modifier = Modifier.border(
                            width = 1.dp, color = Color(0xFF1C1B1F), shape = RoundedCornerShape(
                                12.dp
                            )
                        ).clip(RoundedCornerShape(12.dp)).padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.clip(
                                RoundedCornerShape(
                                    topStart = 8.dp, bottomStart = 8.dp
                                )
                            ).clickable(onClick = { if (quantity > 1) quantity-- })
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "−",
                                fontSize = 20.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )
                        }
                        Box(
                            modifier = Modifier.clip(
                                RoundedCornerShape(
                                    topEnd = 8.dp, bottomEnd = 8.dp
                                )
                            )
                                .clickable(onClick = { currentProduct.stock?.let { if (quantity < it) quantity++ } })
                                .padding(horizontal = 16.dp, vertical = 8.dp)
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
                        onClick = {
                            val productId = currentProduct.product_id
                            val variantId = currentProduct.variants?.firstOrNull()?.variant_id
                            productId?.let { productId ->
                                variantId?.let { variantId ->
                                    viewModel.addToCart(productId, variantId)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE91E63)
                        )
                    ) {
                        Text(
                            text = "Add to Cart", modifier = Modifier.padding(vertical = 8.dp)
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
                    currentProduct.stock?.let {
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
                                Text("Only ${currentProduct.stock} left")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tabs Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ScrollableTabRow(
                            selectedTabIndex = selectedTab, edgePadding = 16.dp,
                            //                    containerColor = Color(0xFF1C1B1F),
                            //                    contentColor = Color.Black,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    height = 2.dp,
                                    color = Color.Black
                                )
                            }) {
                            listOf(
                                "Product Details", "Ingredients", "Directions", "Reviews"
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
                                    })
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Tab Content
                        when (selectedTab) {
                            0 -> {
                                // Product Details Tab
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                ) {
                                    Text(
                                        text = "Product Information",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    ProductInfoRow("SKU:", currentProduct.sku ?: "")
                                    ProductInfoRow(
                                        "Category:", currentProduct.category?.firstOrNull() ?: ""
                                    )
                                    ProductInfoRow(
                                        "Stock:", "${currentProduct.stock ?: 0} available"
                                    )
                                }
                            }

                            1 -> {
                                // Ingredients Tab
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
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
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
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
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
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
                                                text = "Write a Review", color = Color.Black
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
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp),
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black.copy(alpha = 0.6f)
        )
        Text(
            text = value, style = MaterialTheme.typography.bodyLarge, color = Color.Black
        )
    }
} 