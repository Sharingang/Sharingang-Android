package com.example.sharingang.items

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ItemsViewModel models the state of the fragment for viewing items
 */
class ItemsViewModel : ViewModel() {

    private val _lastItem = MutableLiveData<Item?>()

    /**
     * The last item created
     */
    val lastItem: LiveData<Item?>
        get() = _lastItem

    /**
     * Add a new item.
     *
     * @param item the item to be added
     */
    fun addItem(item: Item) {
        _lastItem.value = item
    }
}
