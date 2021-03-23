package com.example.sharingang.items

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class CachedItemRepository @Inject constructor(
    private val itemDao: ItemDao,
    private val store: ItemStore
) : ItemRepository {

    override fun items(): LiveData<List<Item>> {
        return itemDao.getAllItems()
    }

    override suspend fun refreshItems() {
        // This is necessary, since you want to avoid doing this work on the main thread
        withContext(Dispatchers.IO) {
            val newItems = store.getAllItems()
            itemDao.insert(newItems)
        }
    }

    override suspend fun addItem(item: Item): String? {
        val ret = store.addItem(item)
        refreshItems()
        return ret
    }

    override suspend fun getItem(id: String): Item? {
        return itemDao.getItem(id)
    }

    override suspend fun getAllItems(): List<Item> {
        val ret = store.getAllItems()
        refreshItems()
        return ret
    }

    override suspend fun updateItem(item: Item): Boolean {
        val ret = store.updateItem(item)
        refreshItems()
        return ret
    }

    override suspend fun deleteItem(id: String): Boolean {
        val ret = store.deleteItem(id)
        refreshItems()
        return ret
    }
}