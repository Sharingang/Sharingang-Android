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

    private suspend fun doRefreshItems(): List<Item> =
        // This is necessary, since you want to avoid doing this work on the main thread
        withContext(Dispatchers.IO) {
            val newItems = store.getAll()
            itemDao.insert(newItems)
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

    override suspend fun add(item: Item): String? {
        return thenRefresh { store.add(item) }
    }

    override suspend fun get(id: String): Item? {
        return itemDao.getItem(id)
    }

    override suspend fun getAll(): List<Item> {
        return doRefreshItems()
    }

    override suspend fun update(item: Item): Boolean {
        return thenRefresh { store.update(item) }
    }

    override suspend fun delete(id: String): Boolean {
        return thenRefresh { store.delete(id) }
    }
}

