package com.example.sharingang.items

import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
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

    enum class OBSERVABLES {
        ALL_ITEMS, SEARCH_RESULTS, WISHLIST
    }

    private val _navigateToEditItem = MutableLiveData<Item?>()
    val navigateToEditItem: LiveData<Item?>
        get() = _navigateToEditItem

    private val _navigateToDetailItem = MutableLiveData<Item?>()
    val navigateToDetailItem: LiveData<Item?>
        get() = _navigateToDetailItem

    private val _refreshing = MutableLiveData(false)
    val refreshing: LiveData<Boolean>
        get() = _refreshing


    private val _searchResults = MutableLiveData<List<Item>>(listOf())
    val searchResults: LiveData<List<Item>>
        get() = _searchResults

    private val _wishlistItem : MutableLiveData<List<Item>> = MutableLiveData(ArrayList())
    val wishlistItem : LiveData<List<Item>>
        get() = _wishlistItem

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
            itemRepository.add(item)
        }
    }


    fun clearSearchResults() {
        _searchResults.value = listOf()
    }

    fun setWishList(list: List<Item>){
        viewModelScope.launch {
            _wishlistItem.postValue(list)
        }
    }

    /**
     * Searches through database, ignores searchName/
     * categoryID if null/empty.
     *
     * @param searchName string searched for
     * @param categoryID category searched for
     */
    fun searchItems(searchName: String, categoryID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val results = itemRepository.getAll().filter { item ->
                // If we have a category, it should match
                val matchCategory = categoryID == 0 || item.category == categoryID

                // If we have a search term, it should match
                val matchName = searchName.isEmpty() || item.title.toLowerCase(Locale.getDefault())
                    .contains(searchName.toLowerCase(Locale.getDefault()))

                matchCategory && matchName
            }
            _searchResults.postValue(results)
        }
    }

    /**
     * Replace the old item by a new one.
     *
     * @param updatedItem the updated item containing the ID of the existing one
     */
    fun updateItem(updatedItem: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            itemRepository.update(updatedItem)
        }
    }

    fun onEditItemClicked(item: Item) {
        _navigateToEditItem.value = item
    }

    fun onEditItemNavigated() {
        _navigateToEditItem.value = null
    }

    fun onViewItem(item: Item) {
        _navigateToDetailItem.value = item
    }

    fun onViewItemNavigated() {
        _navigateToDetailItem.value = null
    }

    fun onSellItem(item: Item) {
        viewModelScope.launch {
            itemRepository.update(item.copy(sold = !item.sold))
        }
    }

    fun setupItemAdapter(): ItemsAdapter {
        val onEdit = { item: Item -> onEditItemClicked(item) }
        val onView = { item: Item -> onViewItem(item) }
        val onSell = { item: Item -> onSellItem(item) }
        return ItemsAdapter(ItemListener(onEdit, onView, onSell))
    }

    fun addObserver(LifeCycleOwner: LifecycleOwner, adapter: ItemsAdapter, type: OBSERVABLES) {
        val observable: LiveData<List<Item>> = when (type) {
            OBSERVABLES.ALL_ITEMS -> items
            OBSERVABLES.SEARCH_RESULTS -> searchResults
            OBSERVABLES.WISHLIST -> wishlistItem
        }
        observable.observe(LifeCycleOwner, {
            adapter.submitList(it)
        })
    }

    fun setupItemNavigation(LifeCycleOwner: LifecycleOwner, navController: NavController,
                            actionEdit: (Item)->NavDirections, actionDetail: (Item)->NavDirections ){
        navigateToEditItem.observe(LifeCycleOwner, { item ->
            item?.let {
                navController.navigate(
                        actionEdit(item)
                )
                onEditItemNavigated()
            }
        })

        navigateToDetailItem.observe(LifeCycleOwner, { item ->
            item?.let {
                navController.navigate(
                        actionDetail(item)
                )
                onViewItemNavigated()
            }
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
