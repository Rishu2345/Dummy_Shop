package com.assasement.dummyShop.viewModel.searchViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.assasement.dummyShop.db.ProductEntity
import com.assasement.dummyShop.repository.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.milliseconds

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data class Content(
        val query: String = "",
        val recentSearches: List<String> = emptyList(),
        val trending: List<String> = defaultTrending,
    ) : SearchUiState
}

private val defaultTrending = listOf(
    "Wireless Earbuds",
    "Mechanical Keyboard",
    "Smartphones",
    "Laptops",
    "Fragrances",
    "Skincare",
)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchViewModel(
    private val repository: ProductRepository,
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val recentSearches = MutableStateFlow<List<String>>(emptyList())
    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    val uiState: StateFlow<SearchUiState> = combine(query, recentSearches) { queryValue, recent ->
        SearchUiState.Content(
            query = queryValue,
            recentSearches = recent,
            trending = defaultTrending,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState.Idle)

    val results: Flow<PagingData<ProductEntity>> = query
        .debounce(500.milliseconds)
        .map { it.trim() }
        .distinctUntilChanged()
        .flatMapLatest { value ->
            if (value.isBlank()) flowOf(PagingData.empty()) else repository.searchProducts(value)
        }
        .cachedIn(viewModelScope)

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun submitSearch(value: String = query.value) {
        val normalized = value.trim()
        if (normalized.isBlank()) return
        query.value = normalized
        recentSearches.value = (listOf(normalized) + recentSearches.value.filterNot { it.equals(normalized, ignoreCase = true) }).take(5)
    }

    fun clearRecentSearches() {
        recentSearches.value = emptyList()
    }

    fun toggleFavorite(productId: Int) {
        _favoriteIds.value = _favoriteIds.value.toMutableSet().apply {
            if (!add(productId)) remove(productId)
        }
    }
}


