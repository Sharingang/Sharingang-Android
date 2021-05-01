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
    private val itemRepository: ItemRepository,
) : ViewModel() {

    init {
        viewModelScope.launch {
            itemRepository.refreshItems()
        }
    }

    enum class OBSERVABLES {
        ALL_ITEMS, SEARCH_RESULTS, USER_ITEMS, WISHLIST, ORDERED_ITEMS
    }

    enum class ORDERING {
        DATE, PRICE, NAME, CATEGORY
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

    private val _orderedItems = MutableLiveData<List<Item>>(listOf())
    val orderedItemsResult: LiveData<List<Item>>
        get() = _orderedItems

    private val _userItems = MutableLiveData<List<Item>>()
    val userItems: LiveData<List<Item>>
        get() = _userItems

    private val _wishlistItem: MutableLiveData<List<Item>> = MutableLiveData(ArrayList())
    val wishlistItem: LiveData<List<Item>>
        get() = _wishlistItem

    private val _rated: MutableLiveData<Boolean> = MutableLiveData(true)
    val rated: LiveData<Boolean>
        get() = _rated


    /**
     * The last item created
     */
    val items: LiveData<List<Item>>
        get() = itemRepository.items()

    /**
     * Add or update an item.
     *
     * Add if it's id is null otherwise update it.
     *
     * @param item the item to be added / set
     * @param callback Will be called when finished with the item's id or null in case of error
     */
    fun setItem(item: Item, callback: ((String?) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val itemId = itemRepository.set(item)
            if (callback != null) {
                viewModelScope.launch(Dispatchers.Main) {
                    callback(itemId)
                }
            }
        }
    }

    /**
     * Get all the items of the user
     *
     * @param userId the id of the user
     */
    fun getUserItem(userId: String?) {
        if (userId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                _userItems.postValue(itemRepository.userItems(userId))
            }
        }
    }

    fun clearSearchResults() {
        _searchResults.value = listOf()
    }

    fun setWishList(list: List<Item>) {
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

                matchCategory && matchName && !item.sold
            }
            _searchResults.postValue(results)
        }
    }

    /**
     * Order items in the database
     *
     * @param orderBy ORDERING
     * @param isAscending either ascending or descending order
     */
    fun orderItems(orderBy: ORDERING, isAscending: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            var results = itemRepository.getAll().sortedWith(compareBy {
                when (orderBy) {
                    ORDERING.DATE -> it.createdAt
                    ORDERING.PRICE -> it.price
                    ORDERING.NAME -> it.title
                    ORDERING.CATEGORY -> it.category
                }
            })
            if (!isAscending) {
                results = results.asReversed()
            }
            _orderedItems.postValue(results)
        }
    }

    private fun onEditItemClicked(item: Item) {
        _navigateToEditItem.value = item
    }

    private fun onEditItemNavigated() {
        _navigateToEditItem.value = null
    }

    private fun onViewItem(item: Item) {
        _navigateToDetailItem.value = item
    }

    private fun onViewItemNavigated() {
        _navigateToDetailItem.value = null
    }

    private suspend fun onSellItem(item: Item) {
        itemRepository.set(item.copy(sold = !item.sold))
    }

    fun setRated(item: Item?) {
        if (item != null) {
            _rated.postValue(item.rated)
        }
    }

    suspend fun sellItem(item: Item?) {
        if (item != null) {
            onSellItem(item)
        }
    }

    fun setupItemAdapter(userId: String?): ItemsAdapter {
        val onView = { item: Item -> onViewItem(item) }
        return ItemsAdapter(ItemListener(onView), userId)
    }

    fun addObserver(LifeCycleOwner: LifecycleOwner, adapter: ItemsAdapter, type: OBSERVABLES) {
        val observable: LiveData<List<Item>> = when (type) {
            OBSERVABLES.ALL_ITEMS -> items
            OBSERVABLES.SEARCH_RESULTS -> searchResults
            OBSERVABLES.USER_ITEMS -> userItems
            OBSERVABLES.WISHLIST -> wishlistItem
            OBSERVABLES.ORDERED_ITEMS -> orderedItemsResult
        }
        observable.observe(LifeCycleOwner, {
            adapter.submitList(it)
        })
    }

    fun rateItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            itemRepository.set(item.copy(rated = true))
            _rated.postValue(true)
        }
    }

    fun setupItemNavigation(
        LifeCycleOwner: LifecycleOwner,
        navController: NavController,
        actionDetail: (Item) -> NavDirections,
    ) {
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
