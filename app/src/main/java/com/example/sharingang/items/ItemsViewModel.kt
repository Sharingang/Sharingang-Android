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

    private val _navigateToEditItem = MutableLiveData<Item?>()
    val navigateToEditItem: LiveData<Item?>
        get() = _navigateToEditItem

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

    /**
     * Replace the old item in the list by a new one. If it
     * doesn't exist, just add the new item.
     *
     * @param oldItem the item to be replaced
     * @param newItem the new item that should replace the other one
     */
    fun updateItem(oldItem: Item, newItem: Item) {
        if (itemsList.contains(oldItem)) {
            val index = itemsList.indexOf(oldItem)
            itemsList.remove(oldItem)
            itemsList.add(index, newItem)
            _items.value = itemsList
        } else {
            addItem(newItem)
        }
    }

    fun onEditItemClicked(item: Item) {
        _navigateToEditItem.value = item
    }

    fun onEditItemNavigated() {
        _navigateToEditItem.value = null
    }
}
