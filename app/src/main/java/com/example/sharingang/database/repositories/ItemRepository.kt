package com.example.sharingang.database.repositories

import androidx.lifecycle.LiveData
import com.example.sharingang.models.Item
import com.example.sharingang.database.store.ItemStore

/**
 * Represents a repository for accessing items
 *
 * This is suitable for use directly in the UI, since it provides LiveData methods,
 * and the ability to be refreshed according to UI actions.
 */
interface ItemRepository : ItemStore {
    /**
     * The current list of items that are available, as LiveData
     */
    fun items(): LiveData<List<Item>>

    /**
     * The current user items that are available
     * @param[userId] id of user
     */
    suspend fun userItems(userId: String): List<Item>?

    /**
     * Make sure that the repository holds up to date items
     */
    suspend fun refreshItems()
}
