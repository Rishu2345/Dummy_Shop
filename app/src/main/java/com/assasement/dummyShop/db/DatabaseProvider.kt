package com.assasement.dummyShop.db

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase =
        INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(context, AppDatabase::class.java, "product_db")
                .build()
                .also { INSTANCE = it }
        }
}