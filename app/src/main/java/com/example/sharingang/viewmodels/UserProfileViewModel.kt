package com.example.sharingang.viewmodels

import androidx.lifecycle.*
import com.example.sharingang.auth.CurrentUserProvider
import com.example.sharingang.database.repositories.ItemRepository
import com.example.sharingang.database.repositories.UserRepository
import com.example.sharingang.models.Item
import com.example.sharingang.models.User
import com.example.sharingang.utils.notification.subscribeToTopic
import com.example.sharingang.utils.notification.unsubscribeFromTopic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val currentUserProvider: CurrentUserProvider,
    private val itemRepository: ItemRepository,
    private val userRepository: UserRepository
) : ViewModel() {


    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?>
        get() = _userId

    private val _wishlistContains: MutableLiveData<Boolean> = MutableLiveData(false)
    val wishlistContains: LiveData<Boolean>
        get() = _wishlistContains

    private val _subscriptionsContains: MutableLiveData<Boolean> = MutableLiveData(false)
    val subscriptionsContains: LiveData<Boolean>
        get() = _subscriptionsContains

    private val _rating = MutableLiveData(0f)
    val rating: LiveData<Float>
        get() = _rating

    val user: LiveData<User?> =
        Transformations.switchMap(_userId) { id ->
            if (id != null) {
                userRepository.user(id)
            } else {
                null
            }
        }

    fun refreshListUI(viewModel: ItemsViewModel) {
        val userId = currentUserProvider.getCurrentUserId()
        viewModelScope.launch(Dispatchers.IO) {
            if (userId != null) {
                val itemList = ArrayList<Item>()
                for (str in userRepository.get(userId)!!.wishlist) {
                    val item = itemRepository.get(str)
                    if (item != null) {
                        itemList.add(item)
                    }
                }
                viewModel.setWishList(itemList)
            }
        }
    }

    fun setUser(userId: String?) {
        _userId.postValue(userId)
    }

    fun refreshRating(userId: String?) {
        if (userId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val userTemp = userRepository.get(userId)
                if (userTemp != null) {
                    _rating.postValue(userTemp.rating.toFloat() / userTemp.numberOfRatings)
                }
            }
        }
    }

    fun updateUserRating(userId: String?, rating: Int) {
        if (userId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val userTemp = userRepository.get(userId)
                if (userTemp != null) {
                    val newNumRatings = userTemp.numberOfRatings + 1
                    val newSumRating = userTemp.rating + rating
                    userRepository.update(
                        userTemp.copy(
                            rating = newSumRating,
                            numberOfRatings = newNumRatings
                        )
                    )
                }
            }
        }
    }

    fun buyItem(userId: String, itemId: String){
        viewModelScope.launch(Dispatchers.IO){
            val userTemp = userRepository.get(userId)
            if(userTemp != null) {
                userRepository.update(
                    userTemp.copy(
                        purchaseHistory = userTemp.purchaseHistory + itemId
                    )
                )
            }
        }
    }

    /**
     * Check whether the connected user is subscribed
     * to this category's notifications
     * @param category the String of the category
     * @return whether the set contains this category already
     */
    fun subscriptionContains(userId: String, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepository.get(userId)
            if (user != null) {
                _subscriptionsContains.postValue(user.subscriptions.contains(category))
            }
        }
    }

    /**
     * Add or remove a category to the connected
     * user's list of subscriptions
     * @param category the category to add or remove
     */
    fun subscriptionSet(userId: String, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepository.get(userId)
            if (user != null) {
                val subs = ArrayList(user.subscriptions)
                when (user.subscriptions.contains(category)) {
                    true -> {
                        subs.remove(category)
                        _subscriptionsContains.postValue(false)
                        unsubscribeFromTopic(category)
                    }
                    false -> {
                        subs.add(category)
                        _subscriptionsContains.postValue(true)
                        subscribeToTopic(category)
                    }
                }
                user.subscriptions = subs
                userRepository.update(user)
            }
        }
    }

    /**
     * Resubscribe to all the topics of the user on login
     * @param userId the id of the user
     */
    fun loginResubscribe(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepository.get(userId)
            user?.subscriptions?.forEach { subscribeToTopic(it) }
        }
    }

    /**
     * Unsubscribe from all topics of the user on logout
     * @param userId the id of the user
     */
    fun logoutUnsubscribe(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepository.get(userId)
            if (user != null) {
                val subs = user.subscriptions
                for (sub in subs) {
                    if (sub.isNotEmpty()) {
                        unsubscribeFromTopic(sub)
                    }
                }
            }
        }
    }

    fun wishlistContains(item: Item?) {
        val userId = currentUserProvider.getCurrentUserId()
        if (item != null && userId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                _wishlistContains.postValue(userRepository.get(userId)!!.wishlist.contains(item.id!!))
            }
        } else {
            _wishlistContains.postValue(false)
        }
    }

    fun modifyWishList(item: Item?) {
        val userId = currentUserProvider.getCurrentUserId()
        if (item != null && userId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val user: User? = userRepository.get(userId)

                val currentList = ArrayList(user!!.wishlist)
                val add = user.wishlist.contains(item.id!!)
                _wishlistContains.postValue(!add)
                if (!add) {
                    currentList.add(item.id)
                } else {
                    currentList.remove(item.id)
                }
                user.wishlist = currentList
                userRepository.update(user)
            }
        }
    }


}

