package com.example.sharingang.database.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sharingang.models.Item
import java.util.*
import kotlin.collections.HashMap

/**
 * In-memory implementation of the ItemRepository
 * The data is not persisted.
 */
class InMemoryItemRepository : ItemRepository {

    private val itemsMap = HashMap<String, Item>()
    private val itemsLiveData = MutableLiveData<List<Item>>()

    init {
        itemsLiveData.value = itemsMap.values.toList()
    }

    override suspend fun get(id: String): Item? {
        return itemsMap[id]
    }

    override suspend fun getAll(): List<Item> {
        return itemsMap.values.toList()
    }

    override fun items(): LiveData<List<Item>> {
        return itemsLiveData
    }

    override suspend fun userItems(userId: String): List<Item> {
        return itemsMap.values.filter { item ->
            item.userId == userId
        }
    }

    override suspend fun refreshItems() {
    }

    override suspend fun set(item: Item): String? {
        return if (item.id == null) {
            add(item)
        } else {
            if (update(item)) {
                item.id
            } else {
                null
            }
        }
    }

    private fun add(item: Item): String {
        require(item.id == null)

        val id = UUID.randomUUID().toString()
        itemsMap[id] = item.copy(id = id)
        itemsLiveData.postValue(itemsMap.values.toList())
        return id
    }

    private fun update(item: Item): Boolean {
        requireNotNull(item.id)

        itemsMap[item.id] = item
        itemsLiveData.postValue(itemsMap.values.toList())
        return true
    }

    override suspend fun delete(id: String): Boolean {
        itemsMap.remove(id)
        itemsLiveData.postValue(itemsMap.values.toList())
        return true
    }
}
