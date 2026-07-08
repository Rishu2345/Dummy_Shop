package com.assasement.dummyShop.di

import com.assasement.dummyShop.db.AppDatabase
import com.assasement.dummyShop.db.DatabaseProvider
import com.assasement.dummyShop.network.ApiService
import com.assasement.dummyShop.network.RetrofitProvider
import com.assasement.dummyShop.repository.ProductRepository
import com.assasement.dummyShop.viewModel.homeScreenViewModel.HomeViewModel
import com.assasement.dummyShop.viewModel.productViewModel.ProductViewModel
import com.assasement.dummyShop.viewModel.searchViewModels.SearchViewModel
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appKoinModule = module {

    single<Json> {
        Json {
            encodeDefaults = false
            ignoreUnknownKeys = true
        }
    }

    single<ApiService> {
        RetrofitProvider.api
    }

    single<AppDatabase> {
        DatabaseProvider.getDatabase(androidContext())
    }

    single {
        ProductRepository(get(), get())
    }

    viewModel { HomeViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { ProductViewModel(get()) }
}

