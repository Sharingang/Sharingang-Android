package com.example.sharingang.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Item represents an item available for sharing, or for sale.
 */
@Entity(tableName = "item")
data class Item(
    @ColumnInfo(name = "description") val description: String,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L
)
