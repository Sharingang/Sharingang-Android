package com.example.sharingang.database.cache

import androidx.lifecycle.LiveData
import com.example.sharingang.models.Item
import com.example.sharingang.database.room.ItemDao
import com.example.sharingang.database.repositories.ItemRepository
import com.example.sharingang.database.store.ItemStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject


/**
 * Class to implement an ItemRepository in cache.
 * @property itemDao used item data access object
 * @property store used ItemStore
 */
class CachedItemRepository @Inject constructor(
    private val itemDao: ItemDao,
    private val store: ItemStore
) : ItemRepository {

    override fun items(): LiveData<List<Item>> {
        return itemDao.getAllItems()
    }

    private suspend fun doRefreshItems(): List<Item> =
        // This is necessary, since you want to avoid doing this work on the main thread
        withContext(Dispatchers.IO) {
            val newItems = store.getAll()
            itemDao.replace(newItems)
            newItems
        }


    override suspend fun refreshItems() {
        doRefreshItems()
    }

    private suspend fun <T> thenRefresh(fn: suspend () -> T): T {
        val ret = fn()
        refreshItems()
        return ret
    }

    override suspend fun set(item: Item): String? {
        return thenRefresh { store.set(item) }
    }

    override suspend fun get(id: String): Item? {
        return itemDao.getItem(id)
    }

    override suspend fun getAll(): List<Item> {
        return doRefreshItems()
    }

    override suspend fun userItems(userId: String): List<Item>? {
        return itemDao.getUserItem(userId)
    }

    override suspend fun delete(id: String): Boolean {
        return thenRefresh { store.delete(id) }
    }

    override suspend fun getLastTimeUpdate(id: String): Date {
        return thenRefresh { store.getLastTimeUpdate(id) }
    }

    override suspend fun setLastTimeUpdate(id: String, newValue: Date) {
        return thenRefresh { store.setLastTimeUpdate(id, newValue) }
    }
}

