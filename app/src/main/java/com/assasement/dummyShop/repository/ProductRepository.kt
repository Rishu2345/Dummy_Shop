package com.assasement.dummyShop.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.assasement.dummyShop.db.AppDatabase
import com.assasement.dummyShop.db.ProductEntity
import com.assasement.dummyShop.model.Category
import com.assasement.dummyShop.model.Product
import com.assasement.dummyShop.network.ApiService
import com.assasement.dummyShop.network.ProductQuery
import com.assasement.dummyShop.network.ProductRemoteMediator
import com.assasement.dummyShop.utils.toEntity
import com.assasement.dummyShop.utils.toProduct
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
class ProductRepository(
    private val api: ApiService,
    private val db: AppDatabase
) {
    private fun buildPager(
        query: ProductQuery,
        pagingSourceFactory: () -> PagingSource<Int, ProductEntity>
    ): Flow<PagingData<ProductEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            remoteMediator = ProductRemoteMediator(query, api, db),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun getAllProducts(): Flow<PagingData<ProductEntity>> =
        buildPager(ProductQuery.All) { db.productDao().pagingSourceAll() }

    fun getProductsByCategory(category: String): Flow<PagingData<ProductEntity>> =
        buildPager(ProductQuery.ByCategory(category)) {
            db.productDao().pagingSourceByCategory(category)
        }

    fun searchProducts(query: String): Flow<PagingData<ProductEntity>> =
        buildPager(ProductQuery.Search(query)) {
            db.productDao().pagingSourceSearch(query)
        }

    fun getSortedProducts(sortBy: String, order: String): Flow<PagingData<ProductEntity>> =
        buildPager(ProductQuery.Sorted(sortBy, order)) {
            if (sortBy == "price" && order == "asc") db.productDao().pagingSourceSortedByPriceAsc()
            else db.productDao().pagingSourceSortedByPriceDesc()
        }

    suspend fun getCategories(): List<Category> = api.getAllProductCategories()

    suspend fun getProductDetail(id: Int): Product {
        val cached = db.productDao().getProductById(id)
        if (cached != null) return cached.toProduct()

        val remote = api.getProductById(id)
        db.productDao().insertAll(listOf(remote.toEntity()))
        return remote
    }
}

