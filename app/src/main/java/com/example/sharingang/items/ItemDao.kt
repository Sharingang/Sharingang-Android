package com.example.sharingang.items

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDao {
    @Query("SELECT * from item")
    fun getItems(): List<Item>

    @Insert
    fun insert(item: Item)
}
