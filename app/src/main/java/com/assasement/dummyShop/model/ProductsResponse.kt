package com.assasement.dummyShop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductsResponse(
    @SerialName("products")
    val products: List<Product>,
    val total:Int,
    val skip:Int,
    val limit:Int
)
