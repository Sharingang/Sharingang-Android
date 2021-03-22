package com.example.sharingang.items

import androidx.lifecycle.LiveData
import javax.inject.Inject


class CachedItemRepository @Inject constructor(
    private val itemDao: ItemDao,
    private val store: ItemStore
) : ItemRepository {

    override fun items(): LiveData<List<Item>> {
        return itemDao.getAllItems()
    }

    override suspend fun refreshItems() {
        val newItems = store.getAllItems()
        itemDao.insert(newItems)
    }

    override suspend fun addItem(item: Item): String? {
        val ret = store.addItem(item)
        refreshItems()
        return ret
    }

    override suspend fun getItem(id: String): Item? {
        val ret = store.getItem(id)
        refreshItems()
        return ret
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