package com.exal.testapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.exal.testapp.data.local.entity.ListEntity

@Dao
interface ListDao {
    @Query("SELECT * FROM list_table WHERE type = :type ORDER BY id ASC")
    fun getListsByType(type: String): PagingSource<Int, ListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lists: List<ListEntity>)

    @Query("DELETE FROM list_table WHERE type = :type")
    suspend fun clearByType(type: String)

    @Query("SELECT * FROM list_table WHERE type = :type ORDER BY createdAt DESC LIMIT 5")
    fun getFiveLatestData(type: String): PagingSource<Int, ListEntity>

    @Query("DELETE FROM list_table")
    suspend fun clearAll()
}