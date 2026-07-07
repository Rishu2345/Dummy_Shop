package com.assasement.dummyShop.di

import com.assasement.dummyShop.network.ApiService
import com.assasement.dummyShop.network.RetrofitProvider
import com.assasement.dummyShop.repository.ProductRepository
import kotlinx.serialization.json.Json
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

    single {
        ProductRepository(get())
    }

}

