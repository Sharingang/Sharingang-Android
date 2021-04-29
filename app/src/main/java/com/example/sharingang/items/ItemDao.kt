package com.example.sharingang.items

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun getAllItems(): LiveData<List<Item>>

    @Query("SELECT * FROM item WHERE id = :id")
    suspend fun getItem(id: String): Item?

    @Query("SELECT * FROM item WHERE userId = :userId")
    suspend fun getUserItem(userId: String): List<Item>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<Item>)

    @Query("DELETE FROM item")
    fun clear()
}
