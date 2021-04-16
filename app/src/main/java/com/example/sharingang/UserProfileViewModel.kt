package com.example.sharingang

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.sharingang.items.Item
import com.example.sharingang.items.ItemRepository
import com.example.sharingang.items.ItemsAdapter
import com.example.sharingang.users.CurrentUserProvider
import com.example.sharingang.users.User
import com.example.sharingang.users.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val currentUserProvider: CurrentUserProvider,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userId = MutableLiveData<String?>(currentUserProvider.getCurrentUserId())
    val userId: LiveData<String?>
        get() = _userId

    private val _wishlistContains : MutableLiveData<Boolean> = MutableLiveData(false)
    val wishlistContains : LiveData<Boolean>
        get() = _wishlistContains

    private val _wishlistItem : MutableLiveData<List<Item?>> = MutableLiveData(ArrayList())
    val wishlistItem : LiveData<List<Item?>>
        get() = _wishlistItem

    val user: LiveData<User?> =
        Transformations.switchMap(_userId) { id ->
            if (id != null) {
                userRepository.user(id)
            } else {
                null
            }
        }

    fun setUser(userId: String?) {
        _userId.postValue(userId)
    }

    fun addWishlistObserver( LifeCyleOwner : LifecycleOwner, adapter: ItemsAdapter){
        wishlistItem.observe(LifeCyleOwner, {
            adapter.submitList(it)
        })
    }

    fun wishlistContains(item: Item?){
        if(item != null && userId != null){
            viewModelScope.launch(Dispatchers.IO) {
                _wishlistContains.postValue(userRepository.get(userId.value!!)!!.wishlist.contains(item.id!!))
            }
        }
    }


    fun modifyWishList(item: Item?) {
        if(item != null){
            viewModelScope.launch(Dispatchers.IO) {
                if(userId == null) return@launch
                val user : User? = userRepository.get(userId.value!!)

                val currentList = ArrayList(user!!.wishlist)
                val add = user.wishlist.contains(item.id!!)
                _wishlistContains.postValue(!add)
                if(!add){
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

