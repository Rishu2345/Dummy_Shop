package com.assasement.dummyShop.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY id ASC")
    fun pagingSourceAll(): PagingSource<Int, ProductEntity>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY id ASC")
    fun pagingSourceByCategory(category: String): PagingSource<Int, ProductEntity>

    @Query("SELECT * FROM products WHERE title LIKE '%' || :query || '%' ORDER BY id ASC")
    fun pagingSourceSearch(query: String): PagingSource<Int, ProductEntity>

    @Query("SELECT * FROM products ORDER BY price ASC")
    fun pagingSourceSortedByPriceAsc(): PagingSource<Int, ProductEntity>

    @Query("SELECT * FROM products ORDER BY price DESC")
    fun pagingSourceSortedByPriceDesc(): PagingSource<Int, ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun clearAll()

    @Query("DELETE FROM products WHERE category = :category")
    suspend fun clearCategory(category: String)
}