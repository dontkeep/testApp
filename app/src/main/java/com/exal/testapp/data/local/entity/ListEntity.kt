package com.exal.testapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "list_table")
data class ListEntity(
    @PrimaryKey val id: String,
    val title: String?,
    val type: String?,
    val totalExpenses: String?,
    val totalProducts: Int?,
    val totalItems: Int?,
    val createdAt: String?,
    val boughtAt: String?,
    val image: String?
)