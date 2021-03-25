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

    enum class OBSERVABLES{
        ALL_ITEMS, SEARCH_RESULTS
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


    fun clearSearchResults(){
        _searchResults.value = listOf<Item>()
    }

    /**
     * Searches through database, ignores searchName/
     * categoryID if null/empty.
     *
     * @param searchName string searched for
     * @param categoryID category searched for
     */
    fun searchItems(searchName: String?, categoryID : Int){
        val categoryResults = HashSet<Item>()
        val nameResults = HashSet<Item>()
        viewModelScope.launch(Dispatchers.IO){
            if (categoryID == 0) categoryResults.addAll(itemRepository.getAllItems()) else{
                for(item in itemRepository.getAllItems()){
                    if(item.category == categoryID){
                        categoryResults.add(item)
                    }
                }
            }
            if(searchName == null || searchName.isEmpty()) nameResults.addAll(itemRepository.getAllItems()) else{
                for(item in itemRepository.getAllItems()){
                    if(item.title.toLowerCase().contains(searchName!!.toLowerCase())){
                        nameResults.add(item)
                    }
                }
            }
            _searchResults.postValue(categoryResults.intersect(nameResults).toList())
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

    fun onSellItem(item: Item) {
        viewModelScope.launch {
            itemRepository.updateItem(item.copy(sold = !item.sold))
        }
    }

    fun setupItemAdapter(): ItemsAdapter {
        val onEdit = { item: Item -> onEditItemClicked(item) }
        val onView = { item: Item -> onViewItem(item) }
        val onSell = { item: Item -> onSellItem(item) }
        return ItemsAdapter(ItemListener(onEdit, onView, onSell))
    }

    fun addObserver(LifeCycleOwner: LifecycleOwner, adapter: ItemsAdapter, type : OBSERVABLES) {
        val observable : LiveData<List<Item>> = when(type){
            OBSERVABLES.ALL_ITEMS -> items
            OBSERVABLES.SEARCH_RESULTS -> searchResults
        }
        observable.observe(LifeCycleOwner, {
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
