package com.example.sharingang.items

import androidx.lifecycle.LiveData

interface ItemRepository {
    suspend fun getAllItems(): List<Item>

    suspend fun addItem(item: Item): String?

    suspend fun getItem(id: String): Item?

    fun getAllItemsLiveData(): LiveData<List<Item>>
}