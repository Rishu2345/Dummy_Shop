package com.assasement.dummyShop.utils

import com.assasement.dummyShop.db.ProductEntity
import com.assasement.dummyShop.model.Product

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    title = title,
    description = description,
    category = category,
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    tags = tags,
    brand = brand,
    sku = sku,
    weight = weight,
    dimensions = dimensions,
    warrantyInformation = warrantyInformation,
    shippingInformation = shippingInformation,
    availabilityStatus = availabilityStatus,
    reviews = reviews,
    returnPolicy = returnPolicy,
    minimumOrderQuantity = minimumOrderQuantity,
    meta = meta,
    images = images,
    thumbnail = thumbnail
)