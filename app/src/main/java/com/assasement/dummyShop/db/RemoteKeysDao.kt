package com.assasement.dummyShop.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE productId = :id AND queryTag = :tag")
    suspend fun remoteKeysByProductId(id: Int, tag: String): RemoteKeys?

    @Query("DELETE FROM remote_keys WHERE queryTag = :tag")
    suspend fun clearByTag(tag: String)
}