package com.example.sharingang.database.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sharingang.models.Item

/**
 * Dao for an item database.
 */
@Dao
interface ItemDao {
    /**
     * Function to get all items from database.
     * @return List of all items contained in database.
     */
    @Query("SELECT * FROM item")
    fun getAllItems(): LiveData<List<Item>>

    /**
     * Function to get one item from database.
     * @param[id] The id of the item to get.
     * @return Item corresponding to the id.
     */
    @Query("SELECT * FROM item WHERE id = :id")
    suspend fun getItem(id: String): Item?

    /**
     * Function to get all items posted by a user.
     * @param[userId] The id of the user.
     * @return All items posted by the user.
     */
    @Query("SELECT * FROM item WHERE userId = :userId")
    suspend fun getUserItem(userId: String): List<Item>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<Item>)

    /**
     * Function to remove all items from database.
     */
    @Query("DELETE FROM item")
    fun clear()

    /**
     * Function to replace all items in database with a new list of items.
     * @param[items] List of new items.
     */
    @Transaction
    fun replace(items: List<Item>) {
        clear()
        insert(items)
    }
}
