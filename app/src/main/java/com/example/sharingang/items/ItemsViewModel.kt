package com.example.sharingang.items

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ItemsViewModel models the state of the fragment for viewing items
 */
@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _navigateToEditItem = MutableLiveData<Item?>()
    val navigateToEditItem: LiveData<Item?>
        get() = _navigateToEditItem

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

    /**
     * Replace the old item in the list by a new one. If it
     * doesn't exist, just add the new item.
     *
     * @param oldItem the item to be replaced
     * @param newItem the new item that should replace the other one
     */
    fun updateItem(oldItem: Item, newItem: Item) {
        newItem.id = oldItem.id
        viewModelScope.launch(Dispatchers.IO) {
            itemRepository.updateItem(newItem)
        }
    }

    fun onEditItemClicked(item: Item) {
        _navigateToEditItem.value = item
    }

    fun onEditItemNavigated() {
        _navigateToEditItem.value = null
    }
}
