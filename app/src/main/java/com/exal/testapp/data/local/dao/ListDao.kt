package com.exal.testapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.exal.testapp.data.local.entity.ListEntity

@Dao
interface ListDao {
    @Query("SELECT * FROM list_table WHERE type = :type ORDER BY createdAt DESC")
    fun getListsByType(type: String): PagingSource<Int, ListEntity>

    @Query("SELECT * FROM list_table WHERE type = :type ORDER BY createdAt DESC")
    suspend fun getAllListsByType(type: String): List<ListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lists: List<ListEntity>)

    @Query("DELETE FROM list_table WHERE type = :type")
    suspend fun clearByType(type: String)

    @Query("SELECT * FROM list_table WHERE type = :type ORDER BY createdAt DESC LIMIT 5")
    fun getFiveLatestData(type: String): PagingSource<Int, ListEntity>

}