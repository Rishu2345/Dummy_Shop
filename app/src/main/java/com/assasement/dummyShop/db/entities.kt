package com.assasement.dummyShop.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.assasement.dummyShop.model.Dimensions
import com.assasement.dummyShop.model.Meta
import com.assasement.dummyShop.model.Review


@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val tags: List<String>,
    val brand: String?,
    val sku: String,
    val weight: Int,
    val dimensions: Dimensions,
    val warrantyInformation: String,
    val shippingInformation: String,
    val availabilityStatus: String,
    val reviews: List<Review>,
    val returnPolicy: String,
    val minimumOrderQuantity: Int,
    val meta: Meta,
    val images: List<String>,
    val thumbnail: String
)

@Entity(tableName = "remote_keys",primaryKeys = ["productId", "queryTag"])
data class RemoteKeys(
    val productId: Int,
    val prevKey: Int?,
    val nextKey: Int?,
    val queryTag: String
)