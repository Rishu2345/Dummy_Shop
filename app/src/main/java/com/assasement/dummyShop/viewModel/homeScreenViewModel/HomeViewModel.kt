package com.assasement.dummyShop.viewModel.homeScreenViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.assasement.dummyShop.db.ProductEntity
import com.assasement.dummyShop.repository.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Idle : HomeUiState
    data class Content(
        val selectedCategory: String = "All",
        val categories: List<String> = listOf("All"),
    ) : HomeUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val repository: ProductRepository,
) : ViewModel() {
    private val selectedCategory = MutableStateFlow("All")
    private val categories = MutableStateFlow(listOf("All", "Smartphones", "Laptops", "Fragrances", "Skincare"))
    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    val uiState: StateFlow<HomeUiState> = combine(selectedCategory, categories) { selected, available ->
        HomeUiState.Content(selectedCategory = selected, categories = available)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState.Idle)

    val products: Flow<PagingData<ProductEntity>> = selectedCategory
        .flatMapLatest { category ->
            if (category == "All") repository.getAllProducts() else repository.getProductsByCategory(category.lowercase().replace(" ", "-"))
        }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            runCatching { repository.getCategories() }
                .getOrNull()
                ?.map { it.name }
                ?.takeIf { it.isNotEmpty() }
                ?.let { categories.value = listOf("All") + it }
        }
    }

    fun selectCategory(category: String) {
        selectedCategory.value = category
    }

    fun toggleFavorite(productId: Int) {
        _favoriteIds.value = _favoriteIds.value.toMutableSet().apply {
            if (!add(productId)) remove(productId)
        }
    }
}
