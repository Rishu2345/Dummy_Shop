package com.assasement.dummyShop.view.product

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.assasement.dummyShop.model.Product
import com.assasement.dummyShop.view.components.ErrorState
import com.assasement.dummyShop.view.components.FullScreenLoader
import com.assasement.dummyShop.viewModel.productViewModel.ProductUiState
import com.assasement.dummyShop.viewModel.productViewModel.ProductViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductScreen(
    productId: Int,
    onBackClick: () -> Unit,
    viewModel: ProductViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()



    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        TopAppBar(
            title = { Text("Product", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            scrollBehavior = scrollBehavior,
            windowInsets = WindowInsets(0,0,0,0),
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
        )

        AnimatedContent(
            targetState = uiState,
            label = "product-detail-state",
            modifier = Modifier.fillMaxSize(),
        ) { state ->
            when (state) {
                ProductUiState.Idle,
                ProductUiState.Loading -> FullScreenLoader()

                is ProductUiState.Error -> ErrorState(
                    message = state.message,
                    onRetry = viewModel::retry,
                )

                is ProductUiState.Content -> ProductContent(
                    product = state.product,
                    isFavorite = state.isFavorite,
                    onFavoriteClick = viewModel::toggleFavorite,
                )
            }
        }
    }
}


@Composable
private fun ProductContent(
    product: Product,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + scaleIn(initialScale = 0.98f),
        exit = fadeOut(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            ProductImage(product = product, isFavorite = isFavorite, onFavoriteClick = onFavoriteClick)
            Spacer(Modifier.height(18.dp))
            ProductHeader(product)
            Spacer(Modifier.height(18.dp))
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(18.dp))
            ProductChips(product)
            Spacer(Modifier.height(14.dp))
            DetailInfoCard(product)
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("Add to cart")
            }
        }
    }
}

@Composable
private fun ProductImage(
    product: Product,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        AsyncImage(
            model = product.images.firstOrNull() ?: product.thumbnail,
            contentDescription = product.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), CircleShape),
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun ProductHeader(product: Product) {
    Text(
        text = product.category.uppercase(LocalLocale.current.platformLocale),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(6.dp))
    Text(
        text = product.title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
    )
    Spacer(Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                    text = "\$${String.format(Locale.US, "%.2f", product.price)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "${String.format(Locale.US, "%.1f", product.discountPercentage)}% off",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
        RatingBadge(product.rating)
    }
}

@Composable
private fun RatingBadge(rating: Double) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(
                text = String.format(LocalLocale.current.platformLocale, " %.1f", rating),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun ProductChips(product: Product) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AssistChip(
            onClick = {},
            leadingIcon = { Icon(Icons.Filled.Verified, contentDescription = null) },
            label = { Text(product.warrantyInformation) },
        )
        AssistChip(
            onClick = {},
            leadingIcon = { Icon(Icons.Filled.Inventory2, contentDescription = null) },
            label = { Text(product.availabilityStatus) },
        )
        product.tags.take(3).forEach { tag ->
            AssistChip(onClick = {}, label = { Text(tag) })
        }
    }
}

@Composable
private fun DetailInfoCard(product: Product) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ProductInfoRow("Brand", product.brand ?: "Unknown")
            ProductInfoRow("Stock", "${product.stock} available")
            ProductInfoRow("SKU", product.sku)
            ProductInfoRow("Minimum order", product.minimumOrderQuantity.toString())
            ProductInfoRow("Return", product.returnPolicy)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.LocalShipping,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = product.shippingInformation,
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun ProductInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.SemiBold,
        )
    }
}



