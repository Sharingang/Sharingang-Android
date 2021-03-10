package com.example.sharingang.items

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

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

    override fun getAllItemsLiveData(): LiveData<List<Item>> {
        return itemsLiveData
    }

    override suspend fun addItem(item: Item): String {
        val id = UUID.randomUUID().toString()
        item.id = id
        itemsMap[id] = item
        itemsLiveData.postValue(itemsMap.values.toList())
        return id
    }
}