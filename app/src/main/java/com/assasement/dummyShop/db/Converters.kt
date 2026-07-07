package com.assasement.dummyShop.db

import androidx.room.TypeConverter
import com.assasement.dummyShop.model.Dimensions
import com.assasement.dummyShop.model.Meta
import com.assasement.dummyShop.model.Review
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(list: List<String>): String = json.encodeToString(list)

    @TypeConverter
    fun toStringList(data: String): List<String> = json.decodeFromString(data)

    @TypeConverter
    fun fromReviewList(list: List<Review>): String = json.encodeToString(list)

    @TypeConverter
    fun toReviewList(data: String): List<Review> = json.decodeFromString(data)

    @TypeConverter
    fun fromDimensions(dimensions: Dimensions): String = json.encodeToString(dimensions)

    @TypeConverter
    fun toDimensions(data: String): Dimensions = json.decodeFromString(data)

    @TypeConverter
    fun fromMeta(meta: Meta): String = json.encodeToString(meta)

    @TypeConverter
    fun toMeta(data: String): Meta = json.decodeFromString(data)
}