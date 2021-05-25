package com.example.sharingang.viewmodels

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.example.sharingang.imagestore.ImageStore
import com.example.sharingang.database.repositories.ItemRepository
import com.example.sharingang.ui.adapters.ItemListener
import com.example.sharingang.ui.adapters.ItemsAdapter
import com.example.sharingang.models.Item
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
    private val imageStore: ImageStore
) : ViewModel() {

    init {
        viewModelScope.launch {
            itemRepository.refreshItems()
        }
    }

    /**
     * Enum class representing all lists of the viewModel that can be observed.
     */
    enum class OBSERVABLES {
        ALL_ITEMS, SEARCH_RESULTS, USER_ITEMS_AND_REQUESTS, WISHLIST, ORDERED_ITEMS, SOLD_ITEMS
    }

    /**
     * Enum class representing all the ways to order a list of items.
     */
    enum class ORDERING {
        DATE, PRICE, NAME, CATEGORY
    }

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

    private val _userItemsAndRequests = MutableLiveData<List<Item>>()
    val userItemsAndRequests: LiveData<List<Item>>
        get() = _userItemsAndRequests

    private val _userSoldItems = MutableLiveData<List<Item>>()
    val userSoldItems: LiveData<List<Item>>
        get() = _userSoldItems

    private val _wishlistItem: MutableLiveData<List<Item>> = MutableLiveData(ArrayList())
    val wishlistItem: LiveData<List<Item>>
        get() = _wishlistItem

    private val _reviews: MutableLiveData<Map<String, Boolean>> = MutableLiveData()
    val reviews: LiveData<Map<String, Boolean>>
        get() = _reviews


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
            val uploadUrl = item.image?.let {
                if (!it.startsWith("https://")) {
                    imageStore.store(it.toUri())?.toString()
                } else {
                    it
                }
            }
            val itemId = itemRepository.set(item.copy(image = uploadUrl))
            if (callback != null) {
                viewModelScope.launch(Dispatchers.Main) {
                    callback(itemId)
                }
            }
        }
    }

    /**
     * Get all the items of the user, either their offers of requests depending on the
     * second parameter
     *
     * @param userId the id of the user
     * @param isRequest whether we should show the requests or the offers
     */
    fun getUserOffersAndRequests(userId: String?, isRequest: Boolean) {
        if (userId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                _userItemsAndRequests.postValue(
                    itemRepository.userItems(userId)?.filter { item ->
                        !item.sold && isRequest == item.request
                    }
                )
            }
        }
    }

    /**
     * Get all items sold by a user.
     *
     * @param[userId] the id of the user.
     */
    fun getUserSoldItems(userId: String?) {
        if (userId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                _userSoldItems.postValue(
                    itemRepository.userItems(userId)?.filter { item ->
                        item.sold
                    }
                )
            }
        }
    }

    /**
     * Clear the results of the search.
     */
    fun clearSearchResults() {
        _searchResults.value = listOf()
    }

    /**
     * Set the value of the wishlist.
     * @param[list] List of items to set as wishlist.
     */
    fun setWishList(list: List<Item>) {
        viewModelScope.launch {
            _wishlistItem.postValue(list)
        }
    }

    /**
     * Set the value of the reviews
     * @param[item] item whose reviews to use
     */
    fun setReviews(item: Item){
        _reviews.postValue(item.reviews)
    }

    /**
     * Searches through database, ignores searchName/
     * categoryID if null/empty.
     *
     * @param searchName string searched for
     * @param categoryID category searched for
     * @param onlyDiscounts if we search only discounted items
     */
    fun searchItems(searchName: String, categoryID: Int, onlyDiscounts: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val results = itemRepository.getAll().filter { item ->
                // If we have a category, it should match
                val matchCategory = categoryID == 0 || item.category == categoryID

                // If we have a search term, it should match
                val matchName = searchName.isEmpty() || item.title.lowercase()
                    .contains(searchName.lowercase())

                val matchDiscount = !onlyDiscounts || item.discount

                matchCategory && matchName && matchDiscount && !item.sold
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

    fun updateReview(item: Item, userId: String?, reviewed: Boolean) {
        if (userId == null) return
        viewModelScope.launch(Dispatchers.IO) {
            val newReviews = item.reviews.toMutableMap()
            newReviews[userId] = reviewed
            itemRepository.set(item.copy(reviews = newReviews))
            _reviews.postValue(newReviews)
        }
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

    /**
     * Sell an item.
     * @param[item] item to be sold.
     */
    suspend fun sellItem(item: Item?) {
        if (item != null) {
            onSellItem(item)
        }
    }

    /**
     * Setup the item adapter for a recycle view.
     */
    fun setupItemAdapter(): ItemsAdapter {
        val onView = { item: Item -> onViewItem(item) }
        return ItemsAdapter(ItemListener(onView))
    }

    /**
     * Add an observer to one of the observable lists of the view model.
     * @param[LifeCycleOwner]
     * @param[adapter] Adapter to be used to display items.
     * @param[type] The enum describing the list to be observed.
     */
    fun addObserver(LifeCycleOwner: LifecycleOwner, adapter: ItemsAdapter, type: OBSERVABLES) {
        val observable: LiveData<List<Item>> = when (type) {
            OBSERVABLES.ALL_ITEMS -> items
            OBSERVABLES.SEARCH_RESULTS -> searchResults
            OBSERVABLES.USER_ITEMS_AND_REQUESTS -> userItemsAndRequests
            OBSERVABLES.WISHLIST -> wishlistItem
            OBSERVABLES.ORDERED_ITEMS -> orderedItemsResult
            OBSERVABLES.SOLD_ITEMS -> userSoldItems
        }
        observable.observe(LifeCycleOwner, {
            adapter.submitList(it)
        })
    }

    /**
     * Sets up the navigation when clicking on items in a recycler view.
     *
     * @param[LifeCycleOwner]
     * @param[navController] Navigation controller used.
     * @param[actionDetail] The action to take on item click.
     */
    fun setupItemNavigation(
        LifeCycleOwner: LifecycleOwner,
        navController: NavController,
        actionDetail: (Item) -> NavDirections
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

    /**
     * Update list of items, reads from database.
     */
    fun refresh() {
        _refreshing.value = true
        viewModelScope.launch {
            itemRepository.refreshItems()
            // Since we're in a coroutine, need to use post instead
            _refreshing.postValue(false)
        }
    }
}
