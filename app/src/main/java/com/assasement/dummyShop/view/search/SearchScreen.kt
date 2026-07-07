package com.assasement.dummyShop.view.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.assasement.dummyShop.view.components.EmptyState
import com.assasement.dummyShop.view.components.ErrorState
import com.assasement.dummyShop.view.components.ProductCard
import com.assasement.dummyShop.view.components.ProductSearchBar
import com.assasement.dummyShop.view.components.SuggestionChip
import com.assasement.dummyShop.viewModel.searchViewModels.SearchUiState
import com.assasement.dummyShop.viewModel.searchViewModels.SearchViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val results = viewModel.results.collectAsLazyPagingItems()
    val focusRequester = FocusRequester()
    val content = uiState as? SearchUiState.Content ?: SearchUiState.Content()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            ProductSearchBar(
                query = content.query,
                onQueryChange = viewModel::onQueryChange,
                onSearch = { viewModel.submitSearch() },
                modifier = Modifier.focusRequester(focusRequester),
            )
            AnimatedVisibility(visible = content.query.isNotBlank() && results.loadState.refresh is LoadState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
            }
            Spacer(Modifier.height(18.dp))

            if (content.query.isBlank()) {
                SearchSuggestions(
                    recentSearches = content.recentSearches,
                    trending = content.trending,
                    onSuggestionClick = { value ->
                        viewModel.onQueryChange(value)
                        viewModel.submitSearch(value)
                    },
                    onClearAll = viewModel::clearRecentSearches,
                )
            } else {
                when (val refresh = results.loadState.refresh) {
                    is LoadState.Error -> ErrorState(
                        message = "Couldn't load products",
                        onRetry = { results.retry() },
                    )
                    else -> {
                        if (refresh is LoadState.NotLoading && results.itemCount == 0) {
                            EmptyState("No products found for '${content.query}'")
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(
                                    count = results.itemCount,
                                    key = results.itemKey { it.id },
                                ) { index ->
                                    results[index]?.let { product ->
                                        ProductCard(
                                            product = product,
                                            isFavorite = product.id in favoriteIds,
                                            onFavoriteClick = { viewModel.toggleFavorite(product.id) },
                                            onAddClick = {},
                                        )
                                    }
                                }
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    when (results.loadState.append) {
                                        is LoadState.Loading -> Row(
                                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                                            horizontalArrangement = Arrangement.Center,
                                        ) { CircularProgressIndicator() }
                                        is LoadState.Error -> Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                        ) { TextButton(onClick = { results.retry() }) { Text("Retry") } }
                                        else -> Unit
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchSuggestions(
    recentSearches: List<String>,
    trending: List<String>,
    onSuggestionClick: (String) -> Unit,
    onClearAll: () -> Unit,
) {
    if (recentSearches.isNotEmpty()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Recent", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            TextButton(onClick = onClearAll) { Text("Clear all") }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            recentSearches.forEach { search ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSuggestionClick(search) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.History, contentDescription = null)
                    Text(search, modifier = Modifier.padding(start = 12.dp))
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }

    Text("Trending Now", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(10.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        trending.forEach { suggestion ->
            SuggestionChip(text = suggestion, onClick = { onSuggestionClick(suggestion) })
        }
    }
}
