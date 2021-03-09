package com.example.sharingang.items

import androidx.lifecycle.LiveData

interface ItemRepository {
    fun getAllItems(): LiveData<List<Item>>

    // TODO return promise maybe?
    fun addItem(item: Item)
}