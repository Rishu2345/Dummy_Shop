package com.assasement.dummyShop.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.assasement.dummyShop.db.AppDatabase
import com.assasement.dummyShop.db.ProductEntity
import com.assasement.dummyShop.db.RemoteKeys
import com.assasement.dummyShop.utils.toEntity
import retrofit2.HttpException
import java.io.IOException


@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val query: ProductQuery,
    private val api: ApiService,
    private val db: AppDatabase
): RemoteMediator<Int, ProductEntity>() {
    private val queryTag: String = when (query) {
        is ProductQuery.All -> "all"
        is ProductQuery.ByCategory -> "category:${query.category}"
        is ProductQuery.Search -> "search:${query.query}"
        is ProductQuery.Sorted -> "sorted:${query.sortBy}:${query.order}"
    }
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        return try {
            val skip = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull() ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    val remoteKeys = db.remoteKeysDao().remoteKeysByProductId(lastItem.id,queryTag)
                    remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }
            val limit = state.config.pageSize

            val response = when (query) {
                is ProductQuery.All ->
                    api.getProducts(limit = limit, skip = skip)

                is ProductQuery.ByCategory ->
                    api.getProductsByCategory(query.category, limit = limit, skip = skip)

                is ProductQuery.Search ->
                    api.searchProducts(query.query, limit = limit, skip = skip)

                is ProductQuery.Sorted ->
                    api.getSortedProducts(query.sortBy, query.order, limit = limit, skip = skip)
            }

            val endOfPaginationReached = (skip + limit) >= response.total

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.productDao().clearAll()
                    db.remoteKeysDao().clearByTag(queryTag)
                }
                val prevKey = if (skip == 0) null else skip - limit
                val nextKey = if (endOfPaginationReached) null else skip + limit

                val keys = response.products.map {
                    RemoteKeys(it.id, prevKey, nextKey, queryTag)
                }

                db.remoteKeysDao().insertAll(keys)
                db.productDao().insertAll(response.products.map { it.toEntity() })
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}