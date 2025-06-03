package com.healthanalytics.android.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
//import com.healthanalytics.android.core.ImageLoaderFactory
//import com.healthanalytics.android.core.ImageLoaderFactory.imageLoader
import com.healthanalytics.android.data.api.Product
import com.seiko.imageloader.rememberImagePainter
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MarketPlaceScreen(
    modifier: Modifier = Modifier, viewModel: MarketPlaceViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    println("The Ui state --> $uiState")

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is ProductsUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is ProductsUiState.Success -> {
                ProductsGrid(
                    products = state.products, modifier = Modifier.fillMaxSize()
                )
            }

            is ProductsUiState.Error -> {
                ErrorView(
                    message = state.message,
                    onRetry = { viewModel.loadProducts() },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun ProductsGrid(
    products: List<Product?>?, modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        products?.forEach { item ->
            item {
                ProductCard(product = item)
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product?, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            // Score Badge
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
            ) {
                Text(
                    text = "${(product?.stock?.toString())}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Product Image
                Image(
                    painter = rememberImagePainter(
                        product?.img_urls.toString().substringBeforeLast("?")
                    ),
                    contentDescription = product?.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                        .clip(MaterialTheme.shapes.small)
                )


//                Image(
//                    imageUrl = product?.img_urls.toString(),
//                    contentDescription = product?.name,
//                    imageLoader = ImageLoaderFactory.imageLoader,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier.fillMaxSize())


                // Title and Brand
                Text(
                    text = product?.name.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                product?.name?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingBar(rating = product?.rating?.toDouble() ?: 0.0)
                    Text(
                        text = "(${product?.rating})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Prices
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${product?.price}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    product?.price?.let {
                        Text(
                            text = "$${it}",
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tags
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    product?.tags?.forEach { tag ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = tag.toString().uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingBar(
    rating: Double, modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            val starColor = if (index < rating) {
                Color(0xFFFFC107) // Yellow
            } else {
                Color.Gray
            }
            Icon(
                imageVector = Icons.Default.Preview,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun ErrorView(
    message: String, onRetry: () -> Unit, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

// Floating Action Button
@Composable
fun AddToCartFab(
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick, modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Add, contentDescription = "Add to Cart"
        )
    }
} 