package com.assasement.dummyShop.network

import com.assasement.dummyShop.model.Category
import com.assasement.dummyShop.model.Product
import com.assasement.dummyShop.model.ProductsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Locale

interface ApiService {

    @GET("products ")
    suspend fun getProducts(
        @Query("limit") limit:Int,
        @Query("skip") skip:Int
    ): ProductsResponse


    @GET("products/categories")
    suspend fun getAllProductCategories():List<Category>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Product

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category") category: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): ProductsResponse

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") q: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): ProductsResponse

    @GET("products")
    suspend fun getSortedProducts(
        @Query("sortBy") sortBy: String,
        @Query("order") order: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): ProductsResponse
}
