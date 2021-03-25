package com.example.sharingang.items

import android.util.Log
import androidx.lifecycle.*
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

    init {
        viewModelScope.launch {
            itemRepository.refreshItems()
        }
    }

    private val _navigateToEditItem = MutableLiveData<Item?>()
    val navigateToEditItem: LiveData<Item?>
        get() = _navigateToEditItem

    private val _focusedItem = MutableLiveData<Item?>()
    val focusedItem: LiveData<Item?>
        get() = _focusedItem

    private val _viewingItem = MutableLiveData(false)
    val viewingItem: LiveData<Boolean>
        get() = _viewingItem

    private val _refreshing = MutableLiveData(false)
    val refreshing: LiveData<Boolean>
        get() = _refreshing


    private val _searchResults = MutableLiveData<List<Item>>(listOf())
    val searchResults: LiveData<List<Item>>
        get() = _searchResults

    /**
     * The last item created
     */
    val items: LiveData<List<Item>>
        get() = itemRepository.items()

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

    fun searchItems(searchName: String?){
        val listSearchResults = ArrayList<Item>()
        if(searchName == null || searchName.isEmpty()){
            _searchResults.postValue(listSearchResults)
            return
        }

        viewModelScope.launch(Dispatchers.IO){
            for(item in itemRepository.getAllItems()){
                if(item.title.toLowerCase().contains(searchName!!.toLowerCase())){
                    listSearchResults.add(item)
                }
            }
            _searchResults.postValue(listSearchResults)
        }
    }
    /**
     * Replace the old item by a new one.
     *
     * @param updatedItem the updated item containing the ID of the existing one
     */
    fun updateItem(updatedItem: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            itemRepository.updateItem(updatedItem)
        }
    }

    fun onEditItemClicked(item: Item) {
        _navigateToEditItem.value = item
    }

    fun onEditItemNavigated() {
        _navigateToEditItem.value = null
    }

    fun onViewItem(item: Item) {
        _focusedItem.value = item
        _viewingItem.value = true
    }

    fun onViewItemNavigated() {
        _viewingItem.value = false
    }

    fun setupItemAdapter(): ItemsAdapter {
        val onEdit = { item: Item -> onEditItemClicked(item) }
        val onView = { item: Item -> onViewItem(item) }
        return ItemsAdapter(ItemListener(onEdit, onView))
    }

    fun addObserver(LifeCycleOwner: LifecycleOwner, adapter: ItemsAdapter) {
        items.observe(LifeCycleOwner, {
            it?.let { adapter.submitList(it) }
        })
    }

    fun addSearchObserver(LifeCycleOwner: LifecycleOwner, adapter: ItemsAdapter){
        searchResults.observe(LifeCycleOwner, {
            it?.let { adapter.submitList(it) }
        })
    }

    fun refresh() {
        _refreshing.value = true
        viewModelScope.launch {
            itemRepository.refreshItems()
            // Since we're in a coroutine, need to use post instead
            _refreshing.postValue(false)
        }
    }
}
