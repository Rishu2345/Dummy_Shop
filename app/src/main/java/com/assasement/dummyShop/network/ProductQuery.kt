package com.assasement.dummyShop.network

sealed class ProductQuery {
    data object All : ProductQuery()
    data class ByCategory(val category: String) : ProductQuery()
    data class Search(val query: String) : ProductQuery()
    data class Sorted(val sortBy: String, val order: String) : ProductQuery()
}