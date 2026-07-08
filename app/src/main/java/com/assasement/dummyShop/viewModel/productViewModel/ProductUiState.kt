package com.assasement.dummyShop.viewModel.productViewModel

import com.assasement.dummyShop.model.Product

sealed interface ProductUiState {
    data object Idle : ProductUiState
    data object Loading : ProductUiState

    data class Content(
        val product: Product,
        val isFavorite: Boolean = false,
    ) : ProductUiState

    data class Error(val message: String) : ProductUiState
}
