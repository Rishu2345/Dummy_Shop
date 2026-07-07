package com.assasement.dummyShop.viewModel.homeScreenViewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import clickretina.assasement.skillforge.viewModel.AppViewModel
import com.assasement.dummyShop.db.ProductEntity
import com.assasement.dummyShop.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class HomeScreenVM(
    savedStateHandle: SavedStateHandle,
    repository: ProductRepository
): AppViewModel(savedStateHandle) {
    val productsFlow: Flow<PagingData<ProductEntity>> = repository.getProductsStream()
        .cachedIn(viewModelScope)
}