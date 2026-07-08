package com.assasement.dummyShop.viewModel.productViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assasement.dummyShop.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repository: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Idle)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private var productId: Int? = null
    private var favorite = false

    fun loadProduct(id: Int) {
        if (productId == id && _uiState.value is ProductUiState.Content) return
        productId = id

        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            _uiState.value = runCatching { repository.getProductDetail(id) }
                .fold(
                    onSuccess = { product -> ProductUiState.Content(product = product, isFavorite = favorite) },
                    onFailure = { ProductUiState.Error(it.message ?: "Couldn't load product") },
                )
        }
    }

    fun retry() {
        productId?.let(::loadProduct)
    }

    fun toggleFavorite() {
        favorite = !favorite
        val current = _uiState.value
        if (current is ProductUiState.Content) {
            _uiState.value = current.copy(isFavorite = favorite)
        }
    }
}
