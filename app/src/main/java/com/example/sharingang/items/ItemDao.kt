package com.example.sharingang.items

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sharingang.users.User

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

    @Transaction
    fun replace(items: List<Item>) {
        clear()
        insert(items)
    }
}
