package com.example.sharingang.items

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

/**
 * ItemsViewModel models the state of the fragment for viewing items
 */
class ItemsViewModel : ViewModel() {

    // TODO use dependency injection, maybe Hilt
    private val itemRepository: ItemRepository = FirestoreItemRepository()

    /**
     * The last item created
     */
    val items: LiveData<List<Item>>
        get() = itemRepository.getAllItems()

    /**
     * Add a new item.
     *
     * @param item the item to be added
     */
    fun addItem(item: Item) {
        itemRepository.addItem(item)
    }
}
