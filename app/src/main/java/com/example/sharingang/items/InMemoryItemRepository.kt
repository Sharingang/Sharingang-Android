package com.example.sharingang.items

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * In-memory implementation of the ItemRepository
 * The data is not persisted.
 */
class InMemoryItemRepository @Inject constructor() : ItemRepository {

    private val itemsMap = HashMap<String, Item>()
    private val itemsLiveData = MutableLiveData<List<Item>>()

    init {
        itemsLiveData.value = itemsMap.values.toList()
    }

    override suspend fun getItem(id: String): Item? {
        return itemsMap[id]
    }

    override suspend fun getAllItems(): List<Item> {
        return itemsMap.values.toList()
    }

    override fun items(): LiveData<List<Item>> {
        return itemsLiveData
    }

    override suspend fun refreshItems() {
    }

    override suspend fun addItem(item: Item): String {
        require(item.id == null)

        val id = UUID.randomUUID().toString()
        itemsMap[id] = item.copy(id = id)
        itemsLiveData.postValue(itemsMap.values.toList())
        return id
    }

    override suspend fun updateItem(item: Item): Boolean {
        requireNotNull(item.id)

        itemsMap[item.id] = item
        itemsLiveData.postValue(itemsMap.values.toList())
        return true
    }

    override suspend fun deleteItem(id: String): Boolean {
        itemsMap.remove(id)
        itemsLiveData.postValue(itemsMap.values.toList())
        return true
    }
}