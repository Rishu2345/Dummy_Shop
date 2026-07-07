package com.assasement.dummyShop.view.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.assasement.dummyShop.db.ProductEntity
import com.assasement.dummyShop.view.components.ErrorState
import com.assasement.dummyShop.view.components.FullScreenLoader
import com.assasement.dummyShop.view.components.ProductCard
import com.assasement.dummyShop.view.components.ProductSearchBar
import com.assasement.dummyShop.viewModel.homeScreenViewModel.HomeUiState
import com.assasement.dummyShop.viewModel.homeScreenViewModel.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val products = viewModel.products.collectAsLazyPagingItems()
    val content = uiState as? HomeUiState.Content ?: HomeUiState.Content()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            HomeHeader(
                uiState = content,
                onCategorySelected = viewModel::selectCategory,
                onSearchClick = onSearchClick,
            )

            AnimatedContent(
                targetState = products.loadState.refresh,
                label = "home-refresh-state",
            ) { refreshState ->
                when (refreshState) {
                    is LoadState.Loading -> FullScreenLoader()
                    is LoadState.Error -> ErrorState(
                        message = "Couldn't load products",
                        onRetry = { products.retry() },
                    )
                    is LoadState.NotLoading -> ProductGrid(
                        products = products,
                        favoriteIds = favoriteIds,
                        onFavoriteClick = viewModel::toggleFavorite,
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    uiState: HomeUiState.Content,
    onCategorySelected: (String) -> Unit,
    onSearchClick: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
        Text(
            text = "DummyShop",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(12.dp))
        ProductSearchBar(
            query = "",
            onQueryChange = {},
            onSearch = onSearchClick,
            readOnly = true,
        )
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.categories.size) { index ->
                val category = uiState.categories[index]
                FilterChip(
                    selected = category == uiState.selectedCategory,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) },
                )
            }
        }
    }
}

@Composable
private fun ProductGrid(
    products: LazyPagingItems<ProductEntity>,
    favoriteIds: Set<Int>,
    onFavoriteClick: (Int) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            count = products.itemCount,
            key = products.itemKey { it.id },
        ) { index ->
            products[index]?.let { product ->
                ProductCard(
                    product = product,
                    isFavorite = product.id in favoriteIds,
                    onFavoriteClick = { onFavoriteClick(product.id) },
                    onAddClick = {},
                )
            }
        }
        appendFooter(products)
    }
}

private fun LazyGridScope.appendFooter(products: LazyPagingItems<ProductEntity>) {
    item(span = { GridItemSpan(maxLineSpan) }) {
        when (products.loadState.append) {
            is LoadState.Loading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
            is LoadState.Error -> Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                TextButton(onClick = { products.retry() }) { Text("Retry") }
            }
            else -> Unit
        }
    }
}
