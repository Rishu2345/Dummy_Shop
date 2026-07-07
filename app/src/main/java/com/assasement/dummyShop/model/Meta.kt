package com.assasement.dummyShop.model

import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    val createdAt: String,
    val updatedAt: String,
    val barcode: String,
    val qrCode: String
)