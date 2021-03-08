package com.example.sharingang.items

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ItemsViewModel models the state of the fragment for viewing items
 */
class ItemsViewModel : ViewModel() {

    private val itemsList = ArrayList<Item>()
    private val _items = MutableLiveData<List<Item>>()

    init {
        _items.value = itemsList
    }

    /**
     * The last item created
     */
    val items: LiveData<List<Item>>
        get() = _items

    /**
     * Add a new item.
     *
     * @param item the item to be added
     */
    fun addItem(item: Item) {
        itemsList.add(item)
        _items.value = itemsList
    }
}
