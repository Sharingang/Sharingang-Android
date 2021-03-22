package com.example.sharingang.items

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class CachedItemRepository constructor(private val store: ItemStore) : ItemRepository {
    private val _items: MutableLiveData<List<Item>> = MutableLiveData(listOf())

    override fun items(): LiveData<List<Item>> {
        return _items
    }

    override suspend fun refreshItems() {
        _items.postValue(store.getAllItems())
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