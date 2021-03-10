package com.example.sharingang.items

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ItemsViewModel models the state of the fragment for viewing items
 */
class ItemsViewModel(
    // TODO use dependency injection, maybe Hilt
    private val itemRepository: ItemRepository = FirestoreItemRepository(true)
) : ViewModel() {

    /**
     * The last item created
     */
    val items: LiveData<List<Item>>
        get() = itemRepository.getAllItemsLiveData()

    /**
     * Add a new item.
     *
     * @param item the item to be added
     */
    fun addItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            itemRepository.addItem(item)
        }
    }
}
